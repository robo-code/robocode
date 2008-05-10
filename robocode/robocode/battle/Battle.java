/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Replaced the ContestantPeerVector, BulletPeerVector, and RobotPeerVector
 *       with plain Vector
 *     - Integration of new render classes placed under the robocode.gfx package
 *     - BattleView is not given via the constructor anymore, but is retrieved
 *       from the RobocodeManager. In addition, the battleView is now allowed to
 *       be null, e.g. if no GUI is available
 *     - Ported to Java 5.0
 *     - Bugfixed sounds that were cut off after first battle
 *     - Changed initialize() to use loadClass() instead of loadRobotClass() if
 *       security is turned off
 *     - Changed the way the TPS is loaded and updated
 *     - Added updateTitle() in order to manage and update the title on the
 *       RobocodeFrame
 *     - Added replay feature
 *     - Updated to use methods from the Logger, which replaces logger methods
 *       that has been (re)moved from the robocode.util.Utils class
 *     - Changed so robots die faster graphically when the battles are over
 *     - Changed cleanup to only remove the robot in the robot peers, as the
 *       robot peers themselves are used for replay recording
 *     - Added support for playing background music when the battle is ongoing
 *     - Removed unnecessary catches of NullPointerExceptions
 *     - Added support for setting the initial robot positions on the battlefield
 *     - Removed the showResultsDialog field which is replaced by the
 *       getOptionsCommonShowResults() from the properties
 *     - Simplified the code in the run() method when battle is stopped
 *     - Changed so that stop() makes the current round stop immediately
 *     - Added handling keyboard events thru a KeyboardEventDispatcher
 *     - Added mouseMoved(), mouseClicked(), mouseReleased(), mouseEntered(),
 *       mouseExited(), mouseDragged(), mouseWheelMoved()
 *     - Changed to take the new JuniorRobot class into account
 *     - When cleaning up robots their static fields are now being cleaned up
 *     - Bugfix: Changed the runRound() so that the robot are painted after
 *       they have made their turn
 *     - The thread handling for unsafe robot loading has been put in an
 *       independent UnsafeLoadRobotsThread class. In addition, the battle
 *       thread is not sharing it's run() method anymore with the
 *       UnsafeLoadRobotsThread, which has now got its own run() method
 *     - The 'running' and 'aborted' flags are now synchronized towards
 *       'battleMonitor' instead of 'this' object
 *     - Added waitTillRunning() method so another thread can be blocked until
 *       the battle has started running
 *     - Replaced synchronizedList on lists for deathEvent, robots, bullets,
 *       and contestants with a CopyOnWriteArrayList in order to prevent
 *       ConcurrentModificationExceptions when accessing these list via
 *       Iterators using public methods to this class
 *     - The moveBullets() was simplified and moved inside the runRound() method
 *     - The flushOldEvents() method was moved into the runRound() method
 *     - Major bugfix: Two robots running with exactly the same code was getting
 *       different scores. Robots listed before other robots always got a better
 *       score in the end. Hence, the getRobotsAtRandom() method has been added
 *       in order to gain fair play, and this method should be used where robots
 *       are checked and awakened in turn
 *     - Simplified the repainting of the battle
 *     - Bugfix: In wakeupRobots(), only wakeup a robot that is running and alive
 *     - A StatusEvent is now send to all alive robot each turn
 *     - Extended allowed max. length of a robot's full package name from 16 to
 *       32 characters
 *     Luis Crespo
 *     - Added sound features using the playSounds() method
 *     - Added debug step feature
 *     - Added isRunning()
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Titus Chen
 *     - Bugfix: Added Battle parameter to the constructor that takes a
 *       BulletRecord as parameter due to a NullPointerException that was raised
 *       as the battleField variable was not intialized
 *     Nathaniel Troutman
 *     - Bugfix: In order to prevent memory leaks, the cleanup() method has now
 *       been extended to cleanup all robots, but also all classes that this
 *       class refers to in order to avoid circular references. In addition,
 *       cleanup has been added to the KeyEventHandler
 *     Julian Kent
 *     - Fix: Method for using only nano second precision when using
 *       RobotPeer.wait(0, nanoSeconds) in order to prevent the millisecond
 *       granularity issue, which is typically were coarse compared to the one
 *       with nano seconds 
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *     - Refactored large methods into several smaller methods
 *******************************************************************************/
package robocode.battle;


import robocode.*;
import robocode.battleview.SafeComponent;
import robocode.battle.events.BattleEventDispatcher;
import robocode.battle.record.*;
import robocode.battle.snapshot.BattleSnapshot;
import robocode.battlefield.BattleField;
import robocode.control.BattleSpecification;
import robocode.control.RobotResults;
import robocode.dialog.RobocodeFrame;
import robocode.dialog.RobotButton;
import static robocode.io.Logger.log;
import robocode.manager.BattleManager;
import robocode.manager.RobocodeManager;
import robocode.peer.*;
import robocode.peer.robot.RobotClassManager;
import robocode.peer.robot.RobotStatistics;
import robocode.repository.RobotFileSpecification;
import robocode.robotinterfaces.IBasicRobot;
import robocode.security.RobocodeClassLoader;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The {@code Battle} class is used for controlling a battle.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Titus Chen (contributor)
 * @author Nathaniel Troutman (contributor)
 * @author Julian Kent (contributor)
 * @author Pavel Savara (contributor)
 */
public class Battle implements Runnable {

	// Allowed maximum length for a robot's full package name
	private final static int MAX_FULL_PACKAGE_NAME_LENGTH = 32;
	// Allowed maximum length for a robot's short class name
	private final static int MAX_SHORT_CLASS_NAME_LENGTH = 32;
	
	// Maximum turns to display the battle when battle ended
	private final static int TURNS_DISPLAYED_AFTER_ENDING = 35;

	// Objects we use
	private BattleField battleField;
	private BattleManager battleManager;
	private RobocodeManager manager;

	// Battle items
	private Thread battleThread;
	private final Object battleMonitor = new Object();
	private volatile boolean running;
	private volatile boolean aborted;

	// Option related items
	private double gunCoolingRate = .1;

	// Inactivity related items
	private int inactiveTurnCount;
	private double inactivityEnergy;
	private long inactivityTime;

	// Turn skip related items
	private int maxSkippedTurns = 30;
	private int maxSkippedTurnsWithIO = 240;
	private String nonDeterministicRobots;
	private boolean deterministic = true;

	// Current round items
	private int numRounds;
	private int roundNum;
	private int currentTime;
	private int endTimer;
	private int activeRobots;
	private boolean roundOver;

	// Death events
	private List<RobotPeer> deathEvents = new CopyOnWriteArrayList<RobotPeer>();

	// Objects in the battle
	private List<RobotPeer> robots = new CopyOnWriteArrayList<RobotPeer>();
	private List<ContestantPeer> contestants = new CopyOnWriteArrayList<ContestantPeer>();
	private List<BulletPeer> bullets = new CopyOnWriteArrayList<BulletPeer>();

	// Results related items
	private boolean exitOnComplete;

	// Results for RobocodeEngine controller
	private BattleSpecification battleSpecification;

	// Robot loading related items
	private Thread unsafeLoadRobotsThread;
	private final Object unsafeLoaderMonitor = new Object();
	private boolean unsafeLoaderThreadRunning;
	private boolean robotsLoaded;

	// Replay related items
	private boolean replay;
	private boolean isRecordingEnabled;
	private static BattleRecord battleRecord;
	private RoundRecord currentRoundRecord;

	// Initial robot start positions (if any)
	private double[][] initialRobotPositions;


	private final BattleEventDispatcher eventDispatcher;

	/**
	 * Battle constructor
	 */
	public Battle(BattleField battleField, RobocodeManager manager, BattleEventDispatcher eventDispatcher) {
		super();

		this.battleField = battleField;
		this.manager = manager;
		this.eventDispatcher = eventDispatcher;

		battleManager = manager.getBattleManager();
	}

	public void setReplay(boolean replay) {
		this.replay = replay;
	}

	public boolean isReplay() {
		return replay;
	}
	
	/**
	 * When an object implementing interface {@code Runnable} is used
	 * to create a thread, starting the thread causes the object's
	 * {@code run()} method to be called in that separately executing
	 * thread.
	 * <p/>
	 * The general contract of the method {@code run()} is that it may
	 * take any action whatsoever.
	 *
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		initialize();

		deterministic = true;
		nonDeterministicRobots = null;

		boolean soundInitialized = false;

		if (manager.isSoundEnabled()) {
			soundInitialized = true;
		}

		roundNum = 0;

		if (manager.isGUIEnabled()) {
			RobocodeFrame frame = manager.getWindowManager().getRobocodeFrame();

			frame.setEnableStopButton(true);
			frame.setEnableRestartButton(true);
			frame.setEnableReplayButton(false);
		}
		isRecordingEnabled = manager.getProperties().getOptionsCommonEnableReplayRecording();

		if (!replay) {
			battleRecord = isRecordingEnabled ? new BattleRecord(battleField, robots) : null;
		}

		while (!isAborted() && roundNum < numRounds) {
			try {
				setupRound();

				if (replay) {
					// Only run replay for a round if it has been recorded
					if (battleRecord != null && battleRecord.rounds.size() > roundNum) {
						replayRound();
					}
				} else {
					runRound();
				}

				cleanupRound();
			} catch (Exception e) {
				e.printStackTrace();
				log("Exception running a battle: " + e);
			}

			roundNum++;
		}

		if (!replay) {
			for (RobotPeer r : robots) {
				r.getOut().close();
				r.getRobotThreadManager().cleanup();
			}
			unsafeLoadRobotsThread.interrupt();

			if (manager.getListener() != null) {
				if (isAborted()) {
					manager.getListener().battleAborted(battleSpecification);
				} else {
					battleManager.sendResultsToListener(this, manager.getListener());
				}
			}
			if (!isAborted() && manager.isGUIEnabled() && manager.getProperties().getOptionsCommonShowResults()) {
				manager.getWindowManager().showResultsDialog();
			}

			battleManager.printResultsData(this);

			if (exitOnComplete) {
				System.exit(0);
			}
		} else {
			// Replay

			if (!isAborted()) {
				if (manager.getProperties().getOptionsCommonShowResults()) {
					RobotResults[] results = battleRecord.rounds.get(battleRecord.rounds.size() - 1).results;

					for (int i = 0; i < robots.size(); i++) {
						RobotPeer robot = robots.get(i);

						RobotStatistics stats = new RobotStatistics(robot, results[i]);

						robot.setStatistics(stats);
					}

					manager.getWindowManager().showResultsDialog();
				}
			}
		}

		eventDispatcher.onBattleEnded(false); // not aborted

		// The results dialog needs the battle object to be complete, so we
		// won't clean it up just yet, instead the ResultsDialog is responsible
		// for cleaning up the battle when its done with it.
		if (!manager.isGUIEnabled()) {
			cleanup();
		}

		if (soundInitialized) {
			manager.getSoundManager().stopBackgroundMusic();
			manager.getSoundManager().playEndOfBattleMusic();
		}

		if (manager.isGUIEnabled()) {
			RobocodeFrame frame = manager.getWindowManager().getRobocodeFrame();

			frame.setEnableStopButton(false);
			frame.setEnableReplayButton(battleRecord != null);
		}

		// Notify that the battle is over
		synchronized (battleMonitor) {
			running = false;
			battleMonitor.notifyAll();
		}
	}

	public void waitTillRunning() {
		synchronized (battleMonitor) {
			while (!running) {
				try {
					battleMonitor.wait();
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	public void addBullet(BulletPeer bullet) {
		bullets.add(bullet);
	}

	public void addRobot(RobotClassManager robotClassManager) {
		RobotPeer robotPeer = new RobotPeer(robotClassManager,
				battleManager.getManager().getProperties().getRobotFilesystemQuota());
		TeamPeer teamManager = robotClassManager.getTeamManager();

		if (teamManager != null) {
			teamManager.add(robotPeer);
			addContestant(teamManager);
		} else {
			addContestant(robotPeer);
		}
		robotPeer.setBattle(this);
		robotPeer.getOut();

		int count = 0;

		for (RobotPeer rp : robots) {
			if (rp.getRobotClassManager().getClassNameManager().getFullClassNameWithVersion().equals(
					robotPeer.getRobotClassManager().getClassNameManager().getFullClassNameWithVersion())) {
				if (count == 0) {
					if (!rp.isDuplicate()) {
						rp.setDuplicate(0);
					}
				}
				count++;
			}
		}
		if (count > 0) {
			robotPeer.setDuplicate(count);
		}
		robots.add(robotPeer);
	}

	private void addContestant(ContestantPeer c) {
		if (!contestants.contains(c)) {
			contestants.add(c);
		}
	}

	public List<ContestantPeer> getContestants() {
		return contestants;
	}

	public void cleanup() {
		for (RobotPeer r : robots) {
			// Clear all static field on the robot (at class level)
			r.cleanupStaticFields();

			// Clear the robot object by removing the reference to it
			r.setRobot(null);
			r.cleanup();
		}

		if (contestants != null) {
			contestants.clear();
			contestants = null;
		}

		if (robots != null) {
			robots.clear();
			robots = null;
		}

		battleField = null;
		battleManager = null;
		battleSpecification = null;

		// Request garbage collecting
		for (int i = 4; i >= 0; i--) { // Make sure it is run
			System.gc();
		}
	}

	private void cleanupRound() {
		if (!replay) {
			log("Round " + (roundNum + 1) + " cleaning up.");

			for (RobotPeer r : robots) {
				r.getRobotThreadManager().waitForStop();
				r.getRobotStatistics().generateTotals();
			}
		}
	}

	public void generateDeathEvents(RobotPeer r) {
		deathEvents.add(r);
	}

	public BattleField getBattleField() {
		return battleField;
	}

	public Thread getBattleThread() {
		return battleThread;
	}

	public List<BulletPeer> getBullets() {
		return bullets;
	}

	public int getCurrentTime() {
		return currentTime;
	}

	public double getGunCoolingRate() {
		return gunCoolingRate;
	}

	public long getInactivityTime() {
		return inactivityTime;
	}

	public String getNonDeterministicRobots() {
		return nonDeterministicRobots;
	}

	public int getNumRounds() {
		return numRounds;
	}

	public List<RobotPeer> getRobots() {
		return robots;
	}

	/**
	 * Returns a list of all robots in random order. This method is used to gain fair play in Robocode,
	 * so that a robot placed before another robot in the list will not gain any benefit when the game
	 * checks if a robot has won, is dead, etc.
	 * This method was introduced as two equal robots like sample.RamFire got different scores even
	 * though the code was exactly the same.
	 *
	 * @return a list of robots
	 */
	private List<RobotPeer> getRobotsAtRandom() {
		int count = robots.size();

		List<RobotPeer> list = new ArrayList<RobotPeer>(count);

		if (count > 0) {
			list.add(robots.get(0));

			for (int i = 1; i < count; i++) {
				list.add((int) (Math.random() * i + 0.5), robots.get(i));
			}
		}

		return list;
	}

	public RobotPeer getRobotByName(String name) {
		for (RobotPeer r : robots) {
			if (r.getName().equals(name)) {
				return r;
			}
		}
		return null;
	}

	public void initialize() {
		// Notify that the battle is now running
		synchronized (battleMonitor) {
			running = true;
			battleMonitor.notifyAll();
		}

		eventDispatcher.onBattleStarted();

		if (manager.isSoundEnabled()) {
			manager.getSoundManager().playBackgroundMusic();
		}

		// Starting loader thread
		ThreadGroup unsafeThreadGroup = new ThreadGroup("Robot Loader Group");

		unsafeThreadGroup.setDaemon(true);
		unsafeThreadGroup.setMaxPriority(Thread.NORM_PRIORITY);
		unsafeLoadRobotsThread = new UnsafeLoadRobotsThread();
		manager.getThreadManager().setRobotLoaderThread(unsafeLoadRobotsThread);
		unsafeLoadRobotsThread.start();

		if (manager.isGUIEnabled()) {
			manager.getWindowManager().getRobocodeFrame().clearRobotButtons();
		}

		for (RobotPeer r : robots) {
			r.preInitialize();
			if (manager.isGUIEnabled()) {
				manager.getWindowManager().getRobocodeFrame().addRobotButton(
						new RobotButton(manager.getRobotDialogManager(), r));
			}
		}
		if (manager.isGUIEnabled()) {
			manager.getWindowManager().getRobocodeFrame().validate();
		}

		// Pre-load robot classes without security...
		// loadClass WILL NOT LINK the class, so static "cheats" will not work.
		// in the safe robot loader the class is linked.
		synchronized (robots) {
			for (RobotPeer r : robots) {
				try {
					Class<?> c;

					RobotClassManager classManager = r.getRobotClassManager();
					String className = classManager.getFullClassName();

					RobocodeClassLoader classLoader = classManager.getRobotClassLoader();

					if (RobotClassManager.isSecutityOn()) {
						c = classLoader.loadRobotClass(className, true);
					} else {
						c = classLoader.loadClass(className);
					}

					classManager.setRobotClass(c);

					r.getRobotFileSystemManager().initializeQuota();

					RobotFileSpecification robotFileSpecification = classManager.getRobotSpecification();

					r.setJuniorRobot(robotFileSpecification.isJuniorRobot());
					r.setAdvancedRobot(robotFileSpecification.isAdvancedRobot());
					r.setInteractiveRobot(robotFileSpecification.isInteractiveRobot());
					r.setPaintRobot(robotFileSpecification.isPaintRobot());
					r.setTeamRobot(robotFileSpecification.isTeamRobot());
					r.setDroid(robotFileSpecification.isDroid());

					initializeRobotPosition(r);

				} catch (Throwable e) {
					r.getOut().println("SYSTEM: Could not load " + r.getName() + " : " + e);
					e.printStackTrace(r.getOut());
				}
			}
		}
	}

	public boolean isDeterministic() {
		return deterministic;
	}

	public boolean isExitOnComplete() {
		return exitOnComplete;
	}

	public synchronized boolean isRobotsLoaded() {
		return robotsLoaded;
	}

	public void printSystemThreads() {
		Thread systemThreads[] = new Thread[256];

		battleThread.getThreadGroup().enumerate(systemThreads, false);

		log("Threads: ------------------------");
		for (Thread thread : systemThreads) {
			if (thread != null) {
				log(thread.getName());
			}
		}
	}

	public void removeBullet(BulletPeer bullet) {
		bullets.remove(bullet);
	}

	public void resetInactiveTurnCount(double energyLoss) {
		if (energyLoss < 0) {
			return;
		}
		inactivityEnergy += energyLoss;
		while (inactivityEnergy >= 10) {
			inactivityEnergy -= 10;
			inactiveTurnCount = 0;
		}
	}

	public void runRound() {
		log("Let the games begin!");

		roundOver = false;
		endTimer = 0;
		currentTime = 0;
		inactiveTurnCount = 0;

		eventDispatcher.onRoundStarted();

		if (isRecordingEnabled) {
			currentRoundRecord = new RoundRecord();
		}

		battleManager.startNewRound();

		while (!roundOver) {
			if (!manager.isGUIEnabled() || manager.getWindowManager().getRobocodeFrame().isIconified()) {
				runTurn();
			} else {
				long startTime = System.currentTimeMillis();
	
			    runTurn();

				int deltaTime = (int) (System.currentTimeMillis() - startTime);
				int desiredTPS = manager.getProperties().getOptionsBattleDesiredTPS(); 
				int delay = Math.max(((1000 / desiredTPS) - deltaTime), 0);

				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}

		recordRound();

		bullets.clear();

		eventDispatcher.onRoundEnded();
	}

	public void replayRound() {
		log("Replay started");

		roundOver = false;

		eventDispatcher.onRoundStarted();

		endTimer = 0;
		currentTime = 0;

		battleManager.startNewRound();

		while (!(roundOver || isAborted())) {
			replayTurn();
		}

		bullets.clear();

		eventDispatcher.onRoundEnded();
	}

	private void replayTurn() {
		if (shouldPause() && !battleManager.shouldStep()) {
			return;
		}

		roundOver = replayRecord();

		currentTime++;

		eventDispatcher.onTurnEnded(new BattleSnapshot(this));
	}

	private void runTurn() {
		if (shouldPause() && !battleManager.shouldStep()) {
			return;
		}

		cleanRobotEvents();

		currentTime++;

		updateBullets();

		moveRobots();

		handleDeathEvents();

		performScans();

		deathEvents.clear();

		roundOver = checkBattleOver();

		inactiveTurnCount++;

		computeActiveRobots();

		recordTurn();

		addRobotEventsForTurnEnded();

		// Robot time!
		wakeupRobots();

		eventDispatcher.onTurnEnded(new BattleSnapshot(this));
	}

	private void addRobotEventsForTurnEnded() {
		// Add events for the current turn to all robots that are alive
		for (RobotPeer r : robots) {
			if (!r.isDead()) {
				// Add status event
				r.getEventManager().add(new StatusEvent(r));
	
				// Add paint event, if robot is a paint robot and its painting is enabled
				if (r.isPaintRobot() && r.isPaintEnabled()) {
					r.getEventManager().add(new PaintEvent());
				}
			}
		}
	}

	private void cleanRobotEvents() {
		for (RobotPeer r : robots) {
			r.getEventManager().clear(currentTime - 1);
		}
	}

	private void moveRobots() {
		boolean zap = (inactiveTurnCount > inactivityTime);

		// Move all bots
		for (RobotPeer r : getRobotsAtRandom()) {
			if (!r.isDead()) {
				r.update();
			}
			if ((zap || isAborted()) && !r.isDead()) {
				if (isAborted()) {
					r.zap(5);
				} else {
					r.zap(.1);
				}
			}
		}
	}

	private void updateBullets() {
		for (BulletPeer b : bullets) {
			b.update();
		}
	}

	private void recordRound() {
		if (isRecordingEnabled) {
			List<RobotPeer> orderedRobots = new ArrayList<RobotPeer>(robots);

			Collections.sort(orderedRobots);

			RobotResults results[] = new RobotResults[robots.size()];

			int rank;

			for (int i = 0; i < robots.size(); i++) {
				RobotPeer r = orderedRobots.get(i);

				for (rank = 0; rank < robots.size(); rank++) {
					if (robots.get(rank) == r) {
						break;
					}
				}
				results[rank] = r.getRobotStatistics().getResults(i + 1);
			}

			currentRoundRecord.results = results;
			battleRecord.rounds.add(currentRoundRecord);
		}
	}

	private boolean replayRecord() {
		RoundRecord roundRecord = battleRecord.rounds.get(roundNum);

		if (currentTime >= roundRecord.turns.size()) {
			return true;
		}

		TurnRecord turnRecord = roundRecord.turns.get(currentTime);

		RobotPeer robot;
		BulletPeer bullet;

		bullets.clear();

		for (RobotPeer rp : robots) {
			rp.setState(RobotState.DEAD);
		}

		for (RobotRecord rr : turnRecord.robotStates) {
			robot = robots.get(rr.index);
			robot.set(rr);
		}

		for (BulletRecord br : turnRecord.bulletStates) {
			robot = robots.get(br.owner);
			if (br.state == BulletState.EXPLODED.getValue()) {
				bullet = new ExplosionPeer(robot, this, br);
			} else {
				bullet = new BulletPeer(robot, this, br);
			}
			bullets.add(bullet);
		}

		return false;
	}

	private void recordTurn() {
		if (isRecordingEnabled && endTimer < TURNS_DISPLAYED_AFTER_ENDING) {
			TurnRecord currentTurnRecord = new TurnRecord();

			currentRoundRecord.turns.add(currentTurnRecord);

			currentTurnRecord.robotStates = new ArrayList<RobotRecord>();

			RobotPeer rp;

			for (int i = 0; i < robots.size(); i++) {
				rp = robots.get(i);
				if (!rp.isDead()) {
					RobotRecord rr = new RobotRecord(i, rp);

					currentTurnRecord.robotStates.add(rr);
				}
			}

			currentTurnRecord.bulletStates = new ArrayList<BulletRecord>();
			for (BulletPeer bp : getBullets()) {
				RobotPeer owner = bp.getOwner();

				for (int i = 0; i < robots.size(); i++) {
					if (robots.get(i) == owner) {
						BulletRecord br = new BulletRecord(i, bp);

						currentTurnRecord.bulletStates.add(br);
						break;
					}
				}
			}
		}
	}

	private boolean shouldPause() {
		if (battleManager.isPaused() && !isAborted()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// Set the thread status back to being interrupted
				Thread.currentThread().interrupt();
			}
			return true;
		}
		return false;
	}

	private void computeActiveRobots() {
		int ar = 0;

		// Compute active robots
		for (RobotPeer r : robots) {
			if (!r.isDead()) {
				ar++;
			}
		}
		setActiveRobots(ar);
	}

	private void wakeupRobots() {
		// Wake up all robot threads
		synchronized (robots) {
			for (RobotPeer r : getRobotsAtRandom()) {
				if (!r.isRunning()) {
					continue;
				}
				// This call blocks until the
				// robot's thread actually wakes up.
				r.wakeup();

				if (r.isAlive()) {
					synchronized (r) {
						// It's quite possible for simple robots to
						// complete their processing before we get here,
						// so we test if the robot is already asleep.

						if (!r.isSleeping()) {
							try {
								long waitTime = manager.getCpuManager().getCpuConstant();

								int millisWait = (int) (waitTime / 1000000);

								for (int i = millisWait; i > 0 && !r.isSleeping(); i--) {
									r.wait(0, 999999);
								}
								if (!r.isSleeping()) {
									r.wait(0, (int) (waitTime % 1000000));
								}
							} catch (InterruptedException e) {
								// ?
								log("Wait for " + r + " interrupted.");
							}
						}
					}
					if (r.isSleeping() || !r.isRunning()) {
						r.setSkippedTurns(0);
					} else {
						r.setSkippedTurns(r.getSkippedTurns() + 1);

						r.getEventManager().add(new SkippedTurnEvent());

						// Actually, Robocode is never deterministic due to Robots
						// using calls to Math.random()... but the point is,
						// at least one robot skipped a turn.
						deterministic = false;
						if (nonDeterministicRobots == null) {
							nonDeterministicRobots = r.getName();
						} else if (nonDeterministicRobots.indexOf(r.getName()) == -1) {
							nonDeterministicRobots += "," + r.getName();
						}
						if ((!r.isIORobot() && (r.getSkippedTurns() > maxSkippedTurns))
								|| (r.isIORobot() && (r.getSkippedTurns() > maxSkippedTurnsWithIO))) {
							r.getOut().println(
									"SYSTEM: " + r.getName()
									+ " has not performed any actions in a reasonable amount of time.");
							r.getOut().println("SYSTEM: No score will be generated.");
							r.getRobotStatistics().setInactive();
							r.getRobotThreadManager().forceStop();
						}
					}
				}
			}
		}
	}

	private void handleDeathEvents() {
		if (deathEvents.size() > 0) {
			for (RobotPeer r : robots) {
				if (!r.isDead()) {
					for (RobotPeer de : deathEvents) {
						r.getEventManager().add(new RobotDeathEvent(de.getName()));
						if (r.getTeamPeer() == null || r.getTeamPeer() != de.getTeamPeer()) {
							r.getRobotStatistics().scoreSurvival();
						}
					}
				}
			}
		}
		// Compute scores for dead robots
		for (RobotPeer r : deathEvents) {
			if (r.getTeamPeer() == null) {
				r.getRobotStatistics().scoreRobotDeath(getActiveContestantCount(r));
			} else {
				boolean teammatesalive = false;

				for (RobotPeer tm : robots) {
					if (tm.getTeamPeer() == r.getTeamPeer() && (!tm.isDead())) {
						teammatesalive = true;
						break;
					}
				}
				if (!teammatesalive) {
					r.getRobotStatistics().scoreRobotDeath(getActiveContestantCount(r));
				}
			}
		}
	}

	private void performScans() {
		// Perform scans, handle messages
		for (RobotPeer r : getRobotsAtRandom()) {
			if (!r.isDead()) {
				if (r.getScan()) {
					// Enter scan
					System.err.flush();

					r.scan();
					// Exit scan
					r.setScan(false);
				}

				if (r.getMessageManager() != null) {
					List<MessageEvent> messageEvents = r.getMessageManager().getMessageEvents();

					for (MessageEvent me : messageEvents) {
						r.getEventManager().add(me);
					}
					messageEvents.clear();
				}
			}
		}
	}

	private boolean checkBattleOver() {
		boolean battleOver = false;

		// Check game over
		if (isAborted() || oneTeamRemaining()) {
			if (endTimer == 0) {
				boolean leaderFirsts = false;
				TeamPeer winningTeam = null;

				for (RobotPeer r : getRobotsAtRandom()) {
					if (!r.isDead()) {
						if (!r.isWinner()) {
							r.getRobotStatistics().scoreLastSurvivor();
							r.setWinner(true);
							if (r.getTeamPeer() != null) {
								if (r.isTeamLeader()) {
									leaderFirsts = true;
								} else {
									winningTeam = r.getTeamPeer();
								}
							}
						}
					}
				}
				if (!leaderFirsts && winningTeam != null) {
					winningTeam.getTeamLeader().getRobotStatistics().scoreFirsts();
				}
			}

			if (endTimer > 4 * 30) {
				for (RobotPeer r : robots) {
					if (!r.isDead()) {
						r.setHalt(true);
					}
				}
			}

			endTimer++;
			if (endTimer > 5 * 30) {
				battleOver = true;
			}
		}
		return battleOver;
	}

	public int getActiveContestantCount(RobotPeer peer) {
		int count = 0;

		for (ContestantPeer c : contestants) {
			if (c instanceof RobotPeer && !((RobotPeer) c).isDead()) {
				count++;
			} else if (c instanceof TeamPeer && c != peer.getTeamPeer()) {
				for (RobotPeer r : (TeamPeer) c) {
					if (!r.isDead()) {
						count++;
						break;
					}
				}
			}
		}
		return count;
	}

	public void setBattleField(BattleField newBattleField) {
		battleField = newBattleField;
	}

	public void setBattleThread(Thread newBattleThread) {
		battleThread = newBattleThread;
	}

	public void setCurrentTime(int newCurrentTime) {
		currentTime = newCurrentTime;
	}

	void setDeterministic(boolean newDeterministic) {
		deterministic = newDeterministic;
	}

	public void setExitOnComplete(boolean newExitOnComplete) {
		exitOnComplete = newExitOnComplete;
	}

	public void setGunCoolingRate(double newGunCoolingRate) {
		gunCoolingRate = newGunCoolingRate;
	}

	public void setInactivityTime(long newInactivityTime) {
		inactivityTime = newInactivityTime;
	}

	public void setNumRounds(int numRounds) {
		this.numRounds = numRounds;
	}

	public void setProperties(BattleProperties battleProperties) {
		try {
			setNumRounds(battleProperties.getNumRounds());
			setGunCoolingRate(battleProperties.getGunCoolingRate());
			setInactivityTime(battleProperties.getInactivityTime());
			setInitialPositions(battleProperties.getInitialPositions());
		} catch (Exception e) {
			log("Exception setting battle properties", e);
		}
	}

	public synchronized void setRobotsLoaded(boolean newRobotsLoaded) {
		robotsLoaded = newRobotsLoaded;
	}

	public void setupRound() {
		log("----------------------");
		log("Round " + (roundNum + 1) + " initializing..", false);
		currentTime = 0;

		setRobotsLoaded(false);
		while (!isUnsafeLoaderThreadRunning()) {
			// waiting for loader to start
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Set the thread status back to being interrupted
				Thread.currentThread().interrupt();
			}
		}

		for (RobotPeer r : robots) {
			if (roundNum > 0) {
				r.preInitialize();
			} // fake dead so robot won't display

			r.getOut().println("=========================");
			r.getOut().println("Round " + (roundNum + 1) + " of " + numRounds);
			r.getOut().println("=========================");
		}

		// Notifying loader
		synchronized (unsafeLoaderMonitor) {
			unsafeLoaderMonitor.notifyAll();
		}
		while (!isRobotsLoaded()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Set the thread status back to being interrupted
				Thread.currentThread().interrupt();
			}
		}

		String name;

		for (RobotPeer r : robots) {
			name = r.getRobotClassManager().getClassNameManager().getFullPackage();
			if (name != null && name.length() > MAX_FULL_PACKAGE_NAME_LENGTH) {
				r.getOut().println(
						"SYSTEM: Your package name is too long.  " + MAX_FULL_PACKAGE_NAME_LENGTH
						+ " characters maximum please.");
				r.getOut().println("SYSTEM: Robot disabled.");
				r.setEnergy(0);
			}

			name = r.getRobotClassManager().getClassNameManager().getShortClassName();
			if (name != null && name.length() > MAX_SHORT_CLASS_NAME_LENGTH) {
				r.getOut().println(
						"SYSTEM: Your classname is too long.  " + MAX_SHORT_CLASS_NAME_LENGTH + " characters maximum please.");
				r.getOut().println("SYSTEM: Robot disabled.");
				r.setEnergy(0);
			}
		}

		activeRobots = robots.size();

		if (!replay) {
			manager.getThreadManager().reset();

			// Turning on robots
			for (RobotPeer r : getRobotsAtRandom()) {
				manager.getThreadManager().addThreadGroup(r.getRobotThreadManager().getThreadGroup(), r);
				long waitTime = min(300 * manager.getCpuManager().getCpuConstant(), 10000000000L);

				synchronized (r) {
					try {
						log(".", false);

						// Add StatusEvent for the first turn
						r.getEventManager().add(new StatusEvent(r));

						// Start the robot thread
						r.getRobotThreadManager().start();

						// Wait for the robot to go to sleep (take action)
						r.wait(waitTime / 1000000, (int) (waitTime % 1000000));

					} catch (InterruptedException e) {
						log("Wait for " + r + " interrupted.");
					}
				}
				if (!r.isSleeping()) {
					log("\n" + r.getName() + " still has not started after " + (waitTime / 100000) + " ms... giving up.");
				}
			}
		}

		log("");
	}

	public void stop() {
		synchronized (battleMonitor) {
			// Return immediately if the battle is not running
			if (!running) {
				return;
			}

			// Notify that the battle is aborted
			aborted = true;
			battleMonitor.notifyAll();

			eventDispatcher.onBattleEnded(true); // battle was aborted

			// Adjust the desired TPS temporary to maximum rate to stop the battle as quickly as possible

			// Wait till the battle is not running anymore
			while (running) {
				try {
					battleMonitor.wait();
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

	public void unsafeLoadRobots() {
		while (true) {
			// Loader waiting
			synchronized (unsafeLoaderMonitor) {
				try {
					setUnsafeLoaderThreadRunning(true);
					unsafeLoaderMonitor.wait();
				} catch (InterruptedException e) {}
			}
			// Loader awake
			if (roundNum >= numRounds || isAborted()) {
				// Robot loader thread terminating
				return;
			}
			// Loading robots
			for (RobotPeer robotPeer : robots) {
				robotPeer.setRobot(null);
				Class<?> robotClass = null;

				try {
					manager.getThreadManager().setLoadingRobot(robotPeer);
					robotClass = robotPeer.getRobotClassManager().getRobotClass();
					if (robotClass == null) {
						robotPeer.getOut().println("SYSTEM: Skipping robot: " + robotPeer.getName());
						continue;
					}
					IBasicRobot bot = (IBasicRobot) robotClass.newInstance();

					robotPeer.setRobot(bot);

					bot.setOut(robotPeer.getOut());
					bot.setPeer(robotPeer);
				} catch (IllegalAccessException e) {
					robotPeer.getOut().println("SYSTEM: Unable to instantiate this robot: " + e);
					robotPeer.getOut().println("SYSTEM: Is your constructor marked public?");
				} catch (Throwable e) {
					robotPeer.getOut().println(
							"SYSTEM: An error occurred during initialization of " + robotPeer.getRobotClassManager());
					robotPeer.getOut().println("SYSTEM: " + e);
					e.printStackTrace(robotPeer.getOut());
				}
				if (roundNum > 0) {
					initializeRobotPosition(robotPeer);
				}
			} // for
			manager.getThreadManager().setLoadingRobot(null);
			setRobotsLoaded(true);
		}
	}

	private void setInitialPositions(String initialPositions) {
		initialRobotPositions = null;

		if (initialPositions == null || initialPositions.trim().length() == 0) {
			return;
		}

		List<String> positions = new ArrayList<String>();

		Pattern pattern = Pattern.compile("([^,(]*[(][^)]*[)])?[^,]*,?");
		Matcher matcher = pattern.matcher(initialPositions);

		while (matcher.find()) {
			String pos = matcher.group();

			if (pos.length() > 0) {
				positions.add(pos);
			}
		}

		if (positions.size() == 0) {
			return;
		}

		initialRobotPositions = new double[positions.size()][3];

		String[] coords;
		double x, y, heading;

		for (int i = 0; i < positions.size(); i++) {
			coords = positions.get(i).split(",");

			x = RobotPeer.WIDTH + random() * (battleField.getWidth() - 2 * RobotPeer.WIDTH);
			y = RobotPeer.HEIGHT + random() * (battleField.getHeight() - 2 * RobotPeer.HEIGHT);
			heading = 2 * PI * random();

			int len = coords.length;

			if (len >= 1) {
				try {
					x = Double.parseDouble(coords[0].replaceAll("[\\D]", ""));
				} catch (NumberFormatException e) {}

				if (len >= 2) {
					try {
						y = Double.parseDouble(coords[1].replaceAll("[\\D]", ""));
					} catch (NumberFormatException e) {}

					if (len >= 3) {
						try {
							heading = Math.toRadians(Double.parseDouble(coords[2].replaceAll("[\\D]", "")));
						} catch (NumberFormatException e) {}
					}
				}
			}
			initialRobotPositions[i][0] = x;
			initialRobotPositions[i][1] = y;
			initialRobotPositions[i][2] = heading;
		}
	}

	private void initializeRobotPosition(RobotPeer robot) {
		if (initialRobotPositions != null) {
			int index = robots.indexOf(robot);

			if (index >= 0 && index < initialRobotPositions.length) {
				double[] pos = initialRobotPositions[index];

				robot.initialize(pos[0], pos[1], pos[2]);
				if (validSpot(robot)) {
					return;
				}
			}
		}

		double x, y, heading;

		for (int j = 0; j < 1000; j++) {
			x = RobotPeer.WIDTH + random() * (battleField.getWidth() - 2 * RobotPeer.WIDTH);
			y = RobotPeer.HEIGHT + random() * (battleField.getHeight() - 2 * RobotPeer.HEIGHT);
			heading = 2 * PI * random();

			robot.initialize(x, y, heading);

			if (validSpot(robot)) {
				break;
			}
		}
	}

	private boolean validSpot(RobotPeer robot) {
		robot.updateBoundingBox();
		for (RobotPeer r : robots) {
			if (r != null && r != robot) {
				if (robot.getBoundingBox().intersects(r.getBoundingBox())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Gets the activeRobots.
	 *
	 * @return Returns a int
	 */
	public synchronized int getActiveRobots() {
		return activeRobots;
	}

	private boolean oneTeamRemaining() {
		if (getActiveRobots() <= 1) {
			return true;
		}

		boolean found = false;
		TeamPeer currentTeam = null;

		for (RobotPeer currentRobot : robots) {
			if (!currentRobot.isDead()) {
				if (!found) {
					found = true;
					currentTeam = currentRobot.getRobotClassManager().getTeamManager();
				} else {
					if (currentTeam == null && currentRobot.getRobotClassManager().getTeamManager() == null) {
						return false;
					}
					if (currentTeam != currentRobot.getRobotClassManager().getTeamManager()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Sets the activeRobots.
	 *
	 * @param activeRobots The activeRobots to set
	 */
	private synchronized void setActiveRobots(int activeRobots) {
		this.activeRobots = activeRobots;
	}

	/**
	 * Gets the roundNum.
	 *
	 * @return Returns a int
	 */
	public int getRoundNum() {
		return roundNum;
	}

	/**
	 * Sets the roundNum.
	 *
	 * @param roundNum The roundNum to set
	 */
	public void setRoundNum(int roundNum) {
		this.roundNum = roundNum;
	}

	/**
	 * Gets the unsafeLoaderThreadRunning.
	 *
	 * @return Returns a boolean
	 */
	public synchronized boolean isUnsafeLoaderThreadRunning() {
		return unsafeLoaderThreadRunning;
	}

	/**
	 * Sets the unsafeLoaderThreadRunning.
	 *
	 * @param unsafeLoaderThreadRunning The unsafeLoaderThreadRunning to set
	 */
	private synchronized void setUnsafeLoaderThreadRunning(boolean unsafeLoaderThreadRunning) {
		this.unsafeLoaderThreadRunning = unsafeLoaderThreadRunning;
	}

	/**
	 * Gets the battleSpecification.
	 *
	 * @return Returns a BattleSpecification
	 */
	public BattleSpecification getBattleSpecification() {
		return battleSpecification;
	}

	/**
	 * Sets the battleSpecification.
	 *
	 * @param battleSpecification The battleSpecification to set
	 */
	public void setBattleSpecification(BattleSpecification battleSpecification) {
		this.battleSpecification = battleSpecification;
	}

	/**
	 * Gets the manager.
	 *
	 * @return Returns a RobocodeManager
	 */
	public RobocodeManager getManager() {
		return manager;
	}

	/**
	 * Informs on whether the battle is running or not.
	 *
	 * @return true if the battle is running, false otherwise
	 */
	public boolean isRunning() {
		synchronized (battleMonitor) {
			return running;
		}
	}

	/**
	 * Informs on whether the battle is aborted or not.
	 *
	 * @return true if the battle is aborted, false otherwise
	 */
	public boolean isAborted() {
		synchronized (battleMonitor) {
			return aborted;
		}
	}

	public void mouseClicked(final MouseEvent e) {
		if (isRunning()) {
			for (RobotPeer robotPeer : robots) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MouseClickedEvent(cloneMouseEvent(e)));
				}
			}
		}
	}

	public void mouseEntered(final MouseEvent e) {
		if (isRunning()) {
			for (RobotPeer robotPeer : robots) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MouseEnteredEvent(cloneMouseEvent(e)));
				}
			}
		}
	}

	public void mouseExited(final MouseEvent e) {
		if (isRunning()) {
			for (RobotPeer robotPeer : robots) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() ) {
					robotPeer.onInteractiveEvent(new MouseExitedEvent(cloneMouseEvent(e)));
				}
			}
		}
	}

	public void mousePressed(final MouseEvent e) {
		if (isRunning()) {
			for (RobotPeer robotPeer : robots) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MousePressedEvent(cloneMouseEvent(e)));
				}
			}
		}
	}

	public void mouseReleased(final MouseEvent e) {
		if (isRunning()) {
			for (RobotPeer robotPeer : robots) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MouseReleasedEvent(cloneMouseEvent(e)));
				}
			}
		}
	}

	public void mouseMoved(final MouseEvent e) {
		if (isRunning()) {
			for (RobotPeer robotPeer : robots) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MouseMovedEvent(cloneMouseEvent(e)));
				}
			}
		}
	}

	public void mouseDragged(final MouseEvent e) {
		if (isRunning()) {
			for (RobotPeer robotPeer : robots) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MouseDraggedEvent(cloneMouseEvent(e)));
				}
			}
		}
	}

	public void mouseWheelMoved(final MouseWheelEvent e) {
		if (isRunning()) {
			for (RobotPeer robotPeer : robots) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MouseWheelMovedEvent(cloneMouseWheelEvent(e)));
				}
			}
		}
	}

    public void keyTyped(final KeyEvent e) {
        if (isRunning()) {
            for (RobotPeer robotPeer : robots) {
                if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
                    robotPeer.onInteractiveEvent(new KeyTypedEvent(cloneKeyEvent(e)));
                }
            }
        }
    }

    public void keyPressed(final KeyEvent e) {
        if (isRunning()) {
            for (RobotPeer robotPeer : robots) {
                if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
                    robotPeer.onInteractiveEvent(new KeyPressedEvent(cloneKeyEvent(e)));
                }
            }
        }
    }

    public void keyReleased(final KeyEvent e) {
        if (isRunning()) {
            for (RobotPeer robotPeer : robots) {
                if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
                    robotPeer.onInteractiveEvent(new KeyReleasedEvent(cloneKeyEvent(e)));
                }
            }
        }
    }

    public static KeyEvent cloneKeyEvent(final KeyEvent e) {
        return new KeyEvent(SafeComponent.getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), e.getKeyCode(),
                e.getKeyChar(), e.getKeyLocation());
    }

    public static MouseEvent cloneMouseEvent(final MouseEvent e) {
        return new MouseEvent(SafeComponent.getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), e.getX(), e.getY(),
                e.getClickCount(), e.isPopupTrigger(), e.getButton());
    }

    public static MouseWheelEvent cloneMouseWheelEvent(final MouseWheelEvent e) {
        return new MouseWheelEvent(SafeComponent.getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), e.getX(), e.getY(),
                e.getClickCount(), e.isPopupTrigger(), e.getScrollType(), e.getScrollAmount(), e.getWheelRotation());
    }

    private class UnsafeLoadRobotsThread extends Thread {

		public UnsafeLoadRobotsThread() {
			super(new ThreadGroup("Robot Loader Group"), "Robot Loader");
			setDaemon(true);
		}

		@Override
		public void run() {
			// Load robots
			unsafeLoadRobots();
		}
	}
}
