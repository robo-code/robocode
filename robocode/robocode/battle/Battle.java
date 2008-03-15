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
 *     - Added handling keyboard events thru a KeyboardEventDispather
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
 *     - The 'running' and 'aborted' flags are now synchronizet towards
 *       'battleMonitor' instead of 'this' object
 *     - Added waitTillRunning() method so another thread can be blocked until
 *       the battle has started running
 *     - Replaced synchronizetList on lists for deathEvent, robots, bullets,
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
 *     Luis Crespo
 *     - Added sound features using the playSounds() method
 *     - Added debug step feature
 *     - Added isRunning()
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronizet List and HashMap
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
 *******************************************************************************/
package robocode.battle;


import robocode.*;
import robocode.battle.record.*;
import robocode.battlefield.BattleField;
import robocode.battleview.BattleView;
import robocode.control.BattleSpecification;
import robocode.control.RobotResults;
import robocode.dialog.RobocodeFrame;
import robocode.dialog.RobotButton;
import static robocode.io.Logger.log;
import robocode.manager.BattleManager;
import robocode.manager.RobocodeManager;
import robocode.manager.RobocodeProperties;
import robocode.manager.RobocodeProperties.PropertyListener;
import robocode.peer.*;
import robocode.peer.data.RobotPeerStatus;
import robocode.peer.proxies.BattleRobotProxy;
import robocode.peer.proxies.IBattleBulletProxy;
import robocode.peer.proxies.IBattleRobotProxy;
import robocode.peer.proxies.IDisplayRobotProxy;
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
 * The <code>Battle</code> class is used for controlling a battle.
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
public class Battle extends BattleData implements Runnable {

	// Maximum turns to display the battle when battle ended
	private final static int TURNS_DISPLAYED_AFTER_ENDING = 35;

	// Objects we use
	private BattleView battleView;
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

	// TPS and FPS related items
	private int desiredTPS = 30;
	private long startTimeThisSec = 0;

	// Turn skip related items
	private int maxSkippedTurns = 30;
	private int maxSkippedTurnsWithIO = 240;
	private String nonDeterministicRobots;
	private boolean deterministic = true;

	// Current round items
	private int numRounds;
	private int roundNum;
	private int turnsThisSec;
	private int framesThisSec;
	private int currentTime;
	private int endTimer;
	private int activeRobots;

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
	private TurnRecord currentTurnRecord;

	// Initial robot start positions (if any)
	private double[][] initialRobotPositions;

	// Property listener
	private PropertyListener propertyListener;

	// Key event dispatcher
	private KeyEventHandler keyHandler;

	// Dummy component used to preventing robots in accessing the real source component
	private static Component safeEventComponent;

	// Death events
	private List<IBattleRobotProxy> deathEvents = new CopyOnWriteArrayList<IBattleRobotProxy>();

	/**
	 * Battle constructor
	 */
	public Battle(BattleField battleField, RobocodeManager manager) {
		super();

		if (manager.isGUIEnabled()) {
			battleView = manager.getWindowManager().getRobocodeFrame().getBattleView();
			battleView.setBattle(this);
		}
		this.battleField = battleField;
		this.manager = manager;

		battleManager = manager.getBattleManager();

		if (manager.isGUIEnabled()) {
			keyHandler = new KeyEventHandler(this, getDisplayRobots());
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyHandler);
		}
	}

	@Override
	public void finalize() {
		if (keyHandler != null) {
			KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyHandler);
		}
	}

	public void setReplay(boolean replay) {
		this.replay = replay;
	}

	/**
	 * When an object implementing interface <code>Runnable</code> is used
	 * to create a thread, starting the thread causes the object's
	 * <code>run</code> method to be called in that separately executing
	 * thread.
	 * <p/>
	 * The general contract of the method <code>run</code> is that it may
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
			battleRecord = isRecordingEnabled ? new BattleRecord(battleField, getBattleRobots()) : null;
		}

		while (!isAborted() && roundNum < numRounds) {
			updateTitle();
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
			for (IBattleRobotProxy robotProxy : getBattleRobots()) {
				robotProxy.getOut().close();
				robotProxy.getRobotThreadManager().cleanup();
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

					for (int i = 0; i < getBattleRobots().size(); i++) {
						IBattleRobotProxy robotProxy = getBattleRobots().get(i);

						RobotStatistics stats = new RobotStatistics(robotProxy, results[i]);

						robotProxy.replaySetStatisticsLocked(stats);
					}

					manager.getWindowManager().showResultsDialog();
				}
			}
		}

		if (battleView != null) {
			battleView.repaint();
		}

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

		// Must be done here, as this method depends on the running state
		updateTitle();
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

	public void addRobot(RobotClassManager robotClassManager) {
		RobotPeer robotPeer = new RobotPeer(this, robotClassManager,
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

		for (IBattleRobotProxy otherRobotProxy : getBattleRobots()) {
			if (otherRobotProxy.getFullClassNameWithVersion().equals(
					robotPeer.getRobotClassManager().getClassNameManager().getFullClassNameWithVersion())) {
				if (count == 0) {
					if (!otherRobotProxy.isDuplicate()) {
						otherRobotProxy.setupSetDuplicate(0);
					}
				}
				count++;
			}
		}
		if (count > 0) {
			robotPeer.getBattleProxy().setupSetDuplicate(count);
		}
		addRobotPeer(robotPeer);
	}

	public void cleanup() {
		for (RobotPeer robotPeer : getRobotPeers()) {
			robotPeer.cleanup();
		}

		cleanupData();

		if (keyHandler != null) {
			keyHandler.cleanup();
			keyHandler = null;
		}

		if (manager != null) {
			RobocodeProperties props = manager.getProperties();

			props.removePropertyListener(propertyListener);
			props = null;
			propertyListener = null;
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

			for (IBattleRobotProxy robotProxy : getBattleRobots()) {
				robotProxy.getRobotThreadManager().waitForStop();
				robotProxy.getRobotStatistics().generateTotals();
			}
		}
	}

	public void generateDeathEvents(RobotPeer r) {
		deathEvents.add(r.getBattleProxy());
	}

	public BattleField getBattleField() {
		return battleField;
	}

	public Thread getBattleThread() {
		return battleThread;
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

	/**
	 * Returns a list of all robots in random order. This method is used to gain fair play in Robocode,
	 * so that a robot placed before another robot in the list will not gain any benefit when the game
	 * checks if a robot has won, is dead, etc.
	 * This method was introduced as two equal robots like sample.RamFire got different scores even
	 * though the code was exactly the same.
	 *
	 * @return a list of robots
	 */
	private List<IBattleRobotProxy> getRobotsAtRandom() {
		int count = getBattleRobots().size();

		List<IBattleRobotProxy> list = new ArrayList<IBattleRobotProxy>(count);

		if (count > 0) {
			list.add(getBattleRobots().get(0));

			for (int i = 1; i < count; i++) {
				list.add((int) (Math.random() * i + 0.5), getBattleRobots().get(i));
			}
		}

		return list;
	}

	public void setOptions() {
		setDesiredTPS(manager.getProperties().getOptionsBattleDesiredTPS());
		if (battleView != null) {
			battleView.setDisplayOptions();
		}
	}

	public void setDesiredTPS(int desiredTPS) {
		this.desiredTPS = desiredTPS;
	}

	public void initialize() {
		// Notify that the battle is now running
		synchronized (battleMonitor) {
			running = true;
			battleMonitor.notifyAll();
		}

		if (battleView != null) {
			battleView.repaint();
		}

		if (manager.isSoundEnabled()) {
			manager.getSoundManager().playBackgroundMusic();
		}

		setOptions();

		RobocodeProperties props = manager.getProperties();

		desiredTPS = props.getOptionsBattleDesiredTPS();

		propertyListener = props.new PropertyListener() {
			@Override
			public void desiredTpsChanged(int tps) {
				desiredTPS = tps;
			}
		};

		props.addPropertyListener(propertyListener);

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

		for (IBattleRobotProxy robotProxy : getBattleRobots()) {
			robotProxy.lockWrite();
			try {
				robotProxy.setupPreInitialize();
				if (manager.isGUIEnabled()) {
					manager.getWindowManager().getRobocodeFrame().addRobotButton(
							new RobotButton(manager.getRobotDialogManager(), robotProxy.getDisplayView()));
				}
			} finally {
				robotProxy.unlockWrite();
			}
		}
		if (manager.isGUIEnabled()) {
			manager.getWindowManager().getRobocodeFrame().validate();
		}

		// Pre-load robot classes without security...
		// loadClass WILL NOT LINK the class, so static "cheats" will not work.
		// in the safe robot loader the class is linked.
		for (IBattleRobotProxy robotProxy : getBattleRobots()) {
			robotProxy.lockWrite();
			try {
				try {
					Class<?> c;

					RobotClassManager classManager = robotProxy.getRobotClassManager();
					String className = classManager.getFullClassName();

					RobocodeClassLoader classLoader = classManager.getRobotClassLoader();

					if (RobotClassManager.isSecutityOn()) {
						c = classLoader.loadRobotClass(className, true);
					} else {
						c = classLoader.loadClass(className);
					}

					classManager.setRobotClass(c);

					robotProxy.getRobotFileSystemManager().initializeQuota();

					RobotFileSpecification robotFileSpecification = classManager.getRobotSpecification();

					robotProxy.getPeer().setInfo(robotFileSpecification);
					initializeRobotPosition(robotProxy);

					if (battleView != null && !replay) {
						battleView.update();
					}
				} catch (Throwable e) {
					robotProxy.getOut().println("SYSTEM: Could not load " + robotProxy.getName() + " : " + e);
					e.printStackTrace(robotProxy.getOut());
				}
			} finally {
				robotProxy.unlockWrite();
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

	private boolean roundOver;

	private long robotStartTime;
	private long turnStartTime;
	private long frameStartTime;

	private int currentRobotMillis;
	private int totalRobotMillisThisSec;
	private int totalFrameMillisThisSec;
	private int totalTurnMillisThisSec;
	private float estFrameTimeThisSec;
	private float estimatedFPS;
	private int estimatedTurnMillisThisSec;
	private int delay;
	private boolean resetThisSec;

	public void runRound() {
		log("Let the games begin!");

		roundOver = false;
		endTimer = 0;
		currentTime = 0;
		inactiveTurnCount = 0;
		turnsThisSec = 0;
		framesThisSec = 0;
		currentRobotMillis = 0;
		totalRobotMillisThisSec = 0;
		totalFrameMillisThisSec = 0;
		estimatedFPS = 0;
		delay = 0;

		resetThisSec = true;

		if (isRecordingEnabled) {
			currentRoundRecord = new RoundRecord();
		}

		battleManager.startNewRound();

		boolean minimizedMode = battleView == null || manager.getWindowManager().getRobocodeFrame().isIconified();

		if (!minimizedMode) {
			battleView.update();
		}

		while (!roundOver) {
			runTurn();
		}

		recordRound();

		clearBullets();
	}

	public void replayRound() {
		log("Replay started");

		roundOver = false;

		endTimer = 0;
		currentTime = 0;
		turnsThisSec = 0;
		framesThisSec = 0;
		totalFrameMillisThisSec = 0;
		estimatedFPS = 0;
		delay = 0;
		resetThisSec = true;

		battleManager.startNewRound();

		boolean minimizedMode = battleView == null || manager.getWindowManager().getRobocodeFrame().isIconified();

		if (!minimizedMode) {
			battleView.update();
		}

		while (!(roundOver || isAborted())) {
			replayTurn();
		}

		clearBullets();
	}

	private void replayTurn() {

		if (shouldPause() && !battleManager.shouldStep()) {
			resetThisSec = true;
			return;
		}

		// Next turn is starting
		turnStartTime = System.currentTimeMillis();

		resetSec();

		roundOver = replayRecord();

		currentTime++;
		turnsThisSec++;

		// Set flag indication if we are running in "minimized mode"
		boolean minimizedMode = battleView == null || manager.getWindowManager().getRobocodeFrame().isIconified();

		// Store the start time before the frame update
		frameStartTime = System.currentTimeMillis();

		// Paint current battle frame
		displayTurn(minimizedMode);

		measureTime(minimizedMode);

		// Update title when second has passed
		if (resetThisSec) {
			updateTitle();
		}
	}

	private void runTurn() {
		if (shouldPause() && !battleManager.shouldStep()) {
			resetThisSec = true;
			return;
		}

		// Next turn is starting
		turnStartTime = System.currentTimeMillis();

		resetSec();

		lockRobots();
		try {
			cleanRobotEvents();

			currentTime++;
			turnsThisSec++;

			updateBullets();

			moveRobots();

			handleDeathEvents();

			performScans();

			deathEvents.clear();

			roundOver = checkBattleOver();

			inactiveTurnCount++;

			computeActiveRobots();

			recordTurn();

			addStatusEvent();

			// Store the robot start time
			robotStartTime = System.currentTimeMillis();
		} finally {
			unlockRobots();
		}

		// Robot time!
		wakeupRobots();

		// Calculate the time spend on the robots
		currentRobotMillis = (int) (System.currentTimeMillis() - robotStartTime);

		// Set flag indication if we are running in "minimized mode"
		boolean minimizedMode = battleView == null || manager.getWindowManager().getRobocodeFrame().isIconified();

		// Paint current battle frame
		displayTurn(minimizedMode);

		// statistics
		measureTime(minimizedMode);

		// Update title when second has passed
		if (resetThisSec) {
			updateTitle();
		}
	}

	private void lockRobots() {
		for (IBattleRobotProxy robotProxy : getBattleRobots()) {
			robotProxy.lockWrite();
		}
	}

	private void unlockRobots() {
		for (IBattleRobotProxy robotProxy : getBattleRobots()) {
			robotProxy.unlockWrite();
		}
	}

	private void resetSec() {
		if (resetThisSec) {
			resetThisSec = false;

			startTimeThisSec = turnStartTime;

			turnsThisSec = 0;
			framesThisSec = 0;

			totalRobotMillisThisSec = 0;
			totalFrameMillisThisSec = 0;
		}
	}

	private void addStatusEvent() {
		// Add status events for the current turn to all robots that are alive
		for (IBattleRobotProxy robotProxy : getBattleRobots()) {
			if (!robotProxy.isDead()) {
				robotProxy.getBattleEventManager().add(new StatusEvent(robotProxy));
			}
		}
	}

	private void cleanRobotEvents() {
		// New turn: flush any old events
		for (IBattleRobotProxy robotProxy : getBattleRobots()) {
			robotProxy.getBattleEventManager().clearOld(currentTime - 1);
		}
	}

	private void moveRobots() {
		boolean zap = (inactiveTurnCount > inactivityTime);

		// Move all bots
		for (IBattleRobotProxy robotProxy : getRobotsAtRandom()) {
			if (!robotProxy.isDead()) {
				robotProxy.battleUpdate(getBattleRobots());
			}
			if ((zap || isAborted()) && !robotProxy.isDead()) {
				if (isAborted()) {
					robotProxy.battleZap(5);
				} else {
					robotProxy.battleZap(.1);
				}
			}
		}
	}

	private void updateBullets() {
		// gather bullets
		for (IBattleRobotProxy robotProxy : getBattleRobots()) {

			BulletPeer bullet = robotProxy.battleGetCurrentBullet();

			if (bullet != null) {
				robotProxy.battleAdjustGunHeat(Rules.getGunHeat(bullet.getPower()));

				addBullet(bullet);
				robotProxy.battleSetCurrentBullet(null);
			}
		}

		// Update bullets
		for (IBattleBulletProxy b : getBattleBullets()) {
			b.update(getBattleRobots(), getBattleBullets());
		}

		// remove dead bullets
		for (IBattleBulletProxy b : getBattleBullets()) {
			if (b.getState() == BulletPeer.STATE_INACTIVE) {
				removeBullet((BulletPeer) b);
			}
		}
	}

	private void measureTime(boolean minimizedMode) {
		// Calculate the total time spend on robots this second
		totalRobotMillisThisSec += currentRobotMillis;

		// Calculate the total time used for the frame update
		totalFrameMillisThisSec += (int) (System.currentTimeMillis() - turnStartTime) - currentRobotMillis;

		// Calculate the total turn time this second
		totalTurnMillisThisSec = max(1, totalRobotMillisThisSec + totalFrameMillisThisSec);

		// Estimate the time remaining this second to spend on frame updates
		estFrameTimeThisSec = max(0, 1000 - desiredTPS * totalRobotMillisThisSec / turnsThisSec);

		// Estimate the possible FPS based on the estimated frame time
		estimatedFPS = max(1, framesThisSec * estFrameTimeThisSec / totalFrameMillisThisSec);

		// Estimate the time that will be used on the total turn this second
		estimatedTurnMillisThisSec = desiredTPS * totalTurnMillisThisSec / turnsThisSec;

		// Calculate delay needed for keeping the desired TPS (Turns Per Second)
		if (endTimer >= TURNS_DISPLAYED_AFTER_ENDING || minimizedMode) {
			delay = 0;
		} else {
			delay = (estimatedTurnMillisThisSec >= 1000) ? 0 : (1000 - estimatedTurnMillisThisSec) / desiredTPS;
		}

		// Set flag for if the second has passed
		resetThisSec = (System.currentTimeMillis() - startTimeThisSec) >= 1000;

		// Check if we must limit the TPS
		if (!(resetThisSec || minimizedMode)) {
			resetThisSec = ((desiredTPS - turnsThisSec) == 0);
		}

		// Delay to match desired TPS
		if (delay > 0) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// Set the thread status back to being interrupted
				Thread.currentThread().interrupt();
			}
		}
	}

	private void recordRound() {
		if (isRecordingEnabled) {
			List<RobotPeer> orderedRobots = new ArrayList<RobotPeer>(getRobotPeers());

			Collections.sort(orderedRobots);

			RobotResults results[] = new RobotResults[getBattleRobots().size()];

			int rank;

			for (int i = 0; i < getBattleRobots().size(); i++) {
				IBattleRobotProxy r = orderedRobots.get(i).getBattleProxy();

				for (rank = 0; rank < getBattleRobots().size(); rank++) {
					if (getBattleRobots().get(rank) == r) {
						break;
					}
				}
				results[rank] = r.getRobotStatistics().getResults(i + 1, isRunning());
			}

			currentRoundRecord.results = results;
			battleRecord.rounds.add(currentRoundRecord);
		}
	}

	private void displayTurn(boolean minimizedMode) {
		if (!(isAborted() || endTimer >= TURNS_DISPLAYED_AFTER_ENDING || minimizedMode)) {
			// Update the battle view if the frame has not been painted yet this second
			// or if it's time to paint the next frame
			if ((estimatedFPS * turnsThisSec / desiredTPS) >= framesThisSec) {
				battleView.update();
				framesThisSec++;
			}

			playSounds();
		}
	}

	private boolean replayRecord() {
		IBattleRobotProxy robot;
		BulletPeer bullet;
		RoundRecord roundRecord = battleRecord.rounds.get(roundNum);
		TurnRecord turnRecord = roundRecord.turns.get(currentTime);

		clearBullets();
		for (IBattleRobotProxy robotProxy : getBattleRobots()) {
			robotProxy.replaySetStateLocked(RobotPeerStatus.STATE_DEAD);
		}
		for (RobotRecord rr : turnRecord.robotStates) {
			robot = getBattleRobots().get(rr.index);
			robot.replaySetRecordLocked(rr);
		}
		for (BulletRecord br : turnRecord.bulletStates) {
			robot = getBattleRobots().get(br.owner);
			if (br.state == BulletPeer.STATE_EXPLODED) {
				bullet = new ExplosionPeer(robot, this, br);
			} else {
				bullet = new BulletPeer(robot, this, br);
			}
			addBullet(bullet);
		}
		return currentTime >= roundRecord.turns.size();
	}

	private void recordTurn() {
		if (isRecordingEnabled && endTimer < TURNS_DISPLAYED_AFTER_ENDING) {
			currentTurnRecord = new TurnRecord();
			currentRoundRecord.turns.add(currentTurnRecord);

			currentTurnRecord.robotStates = new ArrayList<RobotRecord>();

			IBattleRobotProxy rp;

			for (int i = 0; i < getBattleRobots().size(); i++) {
				rp = getBattleRobots().get(i);
				if (!rp.isDead()) {
					RobotRecord rr = new RobotRecord(i, rp);

					currentTurnRecord.robotStates.add(rr);
				}
			}

			currentTurnRecord.bulletStates = new ArrayList<BulletRecord>();
			for (IBattleBulletProxy bp : getBattleBullets()) {
				IBattleRobotProxy owner = bp.getOwner();

				for (int i = 0; i < getBattleRobots().size(); i++) {
					if (getBattleRobots().get(i) == owner) {
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
			updateTitle();
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
		for (IBattleRobotProxy robotProxy : getBattleRobots()) {
			if (!robotProxy.isDead()) {
				ar++;
			}
		}
		setActiveRobots(ar);
	}

	private void wakeupRobots() {
		// Wake up all robot threads
		for (IBattleRobotProxy robotProxy : getRobotsAtRandom()) {
			if (!robotProxy.isRunning()) {
				continue;
			}
			// This call blocks until the
			// robot's thread actually wakes up.
			robotProxy.wakeupLocked();

			if (robotProxy.isAlive()) {
				// It's quite possible for simple robots to
				// complete their processing before we get here,
				// so we test if the robot is already asleep.

				if (!robotProxy.isSleeping()) {
					try {
						long waitTime = manager.getCpuManager().getCpuConstant();

						int millisWait = (int) (waitTime / 1000000);

						for (int i = millisWait; i > 0 && !robotProxy.isSleeping(); i--) {
							robotProxy.getPeer().getSyncRoot().wait(0, 999999);
						}
						if (!robotProxy.isSleeping()) {
							robotProxy.getPeer().getSyncRoot().wait(0, (int) (waitTime % 1000000));
						}
					} catch (InterruptedException e) {
						// ?
						log("Wait for " + robotProxy + " interrupted.");
					}
				}

				if (robotProxy.isSleeping() || !robotProxy.isRunning()) {
					robotProxy.setSkippedTurnsLocked(0);
				} else {
					robotProxy.setSkippedTurnsLocked(robotProxy.getSkippedTurns() + 1);

					robotProxy.getBattleEventManager().add(new SkippedTurnEvent());

					// Actually, Robocode is never deterministic due to Robots
					// using calls to Math.random()... but the point is,
					// at least one robot skipped a turn.
					deterministic = false;
					if (nonDeterministicRobots == null) {
						nonDeterministicRobots = robotProxy.getName();
					} else if (nonDeterministicRobots.indexOf(robotProxy.getName()) == -1) {
						nonDeterministicRobots += "," + robotProxy.getName();
					}
					if ((!robotProxy.isIORobot() && (robotProxy.getSkippedTurns() > maxSkippedTurns))
							|| (robotProxy.isIORobot() && (robotProxy.getSkippedTurns() > maxSkippedTurnsWithIO))) {
						robotProxy.getOut().println(
								"SYSTEM: " + robotProxy.getName()
								+ " has not performed any actions in a reasonable amount of time.");
						robotProxy.getOut().println("SYSTEM: No score will be generated.");
						robotProxy.getRobotStatistics().setInactive();
						robotProxy.getRobotThreadManager().forceStop();
					}
				}
			}
		}
	}

	private void handleDeathEvents() {
		if (deathEvents.size() > 0) {
			for (IBattleRobotProxy robotProxy : getBattleRobots()) {
				if (!robotProxy.isDead()) {
					for (IBattleRobotProxy de : deathEvents) {
						robotProxy.getBattleEventManager().add(new RobotDeathEvent(de.getName()));
						if (robotProxy.getTeamPeer() == null || robotProxy.getTeamPeer() != de.getTeamPeer()) {
							robotProxy.getRobotStatistics().scoreSurvival();
						}
					}
				}
			}
		}
		// Compute scores for dead robots
		for (IBattleRobotProxy de : deathEvents) {
			if (de.getTeamPeer() == null) {
				de.getRobotStatistics().scoreRobotDeath(getActiveContestantCount(de));
			} else {
				boolean teammatesalive = false;

				for (IBattleRobotProxy tm : getBattleRobots()) {
					if (tm.getTeamPeer() == de.getTeamPeer() && (!tm.isDead())) {
						teammatesalive = true;
						break;
					}
				}
				if (!teammatesalive) {
					de.getRobotStatistics().scoreRobotDeath(getActiveContestantCount(de));
				}
			}
		}
	}

	private void performScans() {
		// Perform scans, handle messages
		for (IBattleRobotProxy robotProxy : getRobotsAtRandom()) {
			if (!robotProxy.isDead()) {
				if (robotProxy.getScan()) {
					// Enter scan
					System.err.flush();

					robotProxy.battleScan(getBattleRobots());
					// Exit scan
					robotProxy.battleSetScan(false);
				}

				if (robotProxy.getBattleMessageManager() != null) {
					List<MessageEvent> messageEvents = robotProxy.getBattleMessageManager().getMessageEvents();

					for (MessageEvent me : messageEvents) {
						robotProxy.getBattleEventManager().add(me);
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

				for (IBattleRobotProxy robotProxy : getRobotsAtRandom()) {
					if (!robotProxy.isDead()) {
						if (!robotProxy.isWinner()) {
							robotProxy.getRobotStatistics().scoreLastSurvivor();
							robotProxy.battleSetWinner(true);
							robotProxy.getOut().println("SYSTEM: " + robotProxy.getName() + " wins the round.");
							robotProxy.getBattleEventManager().add(new WinEvent());
							if (robotProxy.getTeamPeer() != null) {
								if (robotProxy.isTeamLeader()) {
									leaderFirsts = true;
								} else {
									winningTeam = robotProxy.getTeamPeer();
								}
							}
						}
					}
				}
				if (!leaderFirsts && winningTeam != null) {
					(winningTeam.getTeamLeader()).getBattleProxy().getRobotStatistics().scoreFirsts();
				}
			}

			if (endTimer > 4 * 30) {
				for (IBattleRobotProxy robotProxy : getBattleRobots()) {
					if (!robotProxy.isDead()) {
						robotProxy.battleSetStopping(true);
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

	public int getActiveContestantCount(IBattleRobotProxy peer) {
		int count = 0;

		for (IContestantPeer c : getContestants()) {
			if (c instanceof RobotPeer && !((RobotPeer) c).getBattleProxy().isDead()) {
				count++;
			} else if (c instanceof TeamPeer && c != peer.getTeamPeer()) {
				for (RobotPeer r : (TeamPeer) c) {
					if (!r.getBattleProxy().isDead()) {
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

		for (IBattleRobotProxy robotProxy : getBattleRobots()) {
			if (roundNum > 0) {
				robotProxy.setupPreInitialize();
			} // fake dead so robot won't display

			robotProxy.getOut().println("=========================");
			robotProxy.getOut().println("Round " + (roundNum + 1) + " of " + numRounds);
			robotProxy.getOut().println("=========================");
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

		for (IBattleRobotProxy robotProxy : getBattleRobots()) {
			robotProxy.lockWrite();
			try {
				if (robotProxy.getRobotClassManager().getClassNameManager().getFullPackage() != null
						&& robotProxy.getRobotClassManager().getClassNameManager().getFullPackage().length() > 18) {
					robotProxy.getOut().println("SYSTEM: Your package name is too long.  16 characters maximum please.");
					robotProxy.getOut().println("SYSTEM: Robot disabled.");
					robotProxy.battleSetEnergy(0);
				}
				if (robotProxy.getRobotClassManager().getClassNameManager().getShortClassName().length() > 35) {
					robotProxy.getOut().println("SYSTEM: Your classname is too long.  32 characters maximum please.");
					robotProxy.getOut().println("SYSTEM: Robot disabled.");
					robotProxy.battleSetEnergy(0);
				}
			} finally {
				robotProxy.unlockWrite();
			}
		}

		setActiveRobots(getBattleRobots().size());

		if (!replay) {
			manager.getThreadManager().reset();

			// Turning on robots
			for (IBattleRobotProxy robotProxy : getRobotsAtRandom()) {

				manager.getThreadManager().addThreadGroup(robotProxy.getRobotThreadManager().getThreadGroup(),
						(RobotPeer) robotProxy.getPeer());
				long waitTime = min(300 * manager.getCpuManager().getCpuConstant(), 10000000000L);

				robotProxy.lockWrite();
				try {

					try {
						log(".", false);

						// Add StatusEvent for the first turn
						robotProxy.getBattleEventManager().add(new StatusEvent(robotProxy));

						// Start the robot thread
						robotProxy.getRobotThreadManager().start();

						// Wait for the robot to go to sleep (take action)
						robotProxy.getPeer().getSyncRoot().wait(waitTime / 1000000, (int) (waitTime % 1000000));

					} catch (InterruptedException e) {
						log("Wait for " + robotProxy + " interrupted.");
					}
				} finally {
					robotProxy.unlockWrite();
				}

				if (!robotProxy.isSleeping()) {
					log(
							"\n" + robotProxy.getName() + " still has not started after " + (waitTime / 100000)
							+ " ms... giving up.");
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

			// Adjust the desired TPS temporary to maximum rate to stop the battle as quickly as possible
			int savedTPS = desiredTPS;

			desiredTPS = 10000;

			// Wait till the battle is not running anymore
			while (running) {
				try {
					battleMonitor.wait();
				} catch (InterruptedException e) {
					break;
				}
			}

			// Restore the desired TPS
			desiredTPS = savedTPS;
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
			for (IBattleRobotProxy robotProxy : getBattleRobots()) {
				robotProxy.getPeer().setRobot(null);
				Class<?> robotClass;

				try {
					manager.getThreadManager().setLoadingRobot((RobotPeer) robotProxy.getPeer());
					robotClass = robotProxy.getRobotClassManager().getRobotClass();
					if (robotClass == null) {
						robotProxy.getOut().println("SYSTEM: Skipping robot: " + robotProxy.getName());
						continue;
					}
					IBasicRobot bot = (IBasicRobot) robotClass.newInstance();

					robotProxy.getPeer().setRobot(bot);
				} catch (IllegalAccessException e) {
					robotProxy.getOut().println("SYSTEM: Unable to instantiate this robot: " + e);
					robotProxy.getOut().println("SYSTEM: Is your constructor marked public?");
				} catch (Throwable e) {
					robotProxy.getOut().println(
							"SYSTEM: An error occurred during initialization of " + robotProxy.getRobotClassManager());
					robotProxy.getOut().println("SYSTEM: " + e);
					e.printStackTrace(robotProxy.getOut());
				}
				if (roundNum > 0) {
					initializeRobotPosition(robotProxy);
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

			x = BattleRobotProxy.WIDTH + random() * (battleField.getWidth() - 2 * BattleRobotProxy.WIDTH);
			y = BattleRobotProxy.HEIGHT + random() * (battleField.getHeight() - 2 * BattleRobotProxy.HEIGHT);
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

	private void initializeRobotPosition(IBattleRobotProxy robot) {
		if (initialRobotPositions != null) {
			int index = getBattleRobots().indexOf(robot);

			if (index >= 0 && index < initialRobotPositions.length) {
				double[] pos = initialRobotPositions[index];

				robot.initializeLocked(pos[0], pos[1], pos[2], getBattleRobots());
				if (validSpot(robot)) {
					return;
				}
			}
		}

		double x, y, heading;

		for (int j = 0; j < 1000; j++) {
			x = BattleRobotProxy.WIDTH + random() * (battleField.getWidth() - 2 * BattleRobotProxy.WIDTH);
			y = BattleRobotProxy.HEIGHT + random() * (battleField.getHeight() - 2 * BattleRobotProxy.HEIGHT);
			heading = 2 * PI * random();

			robot.initializeLocked(x, y, heading, getBattleRobots());

			if (validSpot(robot)) {
				break;
			}
		}
	}

	private boolean validSpot(IBattleRobotProxy robotProxy) {
		robotProxy.setupUpdateBoundingBox();
		for (IBattleRobotProxy otherRobotProxy : getBattleRobots()) {
			if (otherRobotProxy != null && otherRobotProxy != robotProxy) {
				if (robotProxy.getBoundingBox().intersects(otherRobotProxy.getBoundingBox())) {
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

		for (IBattleRobotProxy robotProxy : getBattleRobots()) {
			if (!robotProxy.isDead()) {
				if (!found) {
					found = true;
					currentTeam = robotProxy.getTeamPeer();
				} else {
					if (currentTeam == null && robotProxy.getTeamPeer() == null) {
						return false;
					}
					if (currentTeam != robotProxy.getTeamPeer()) {
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
	 * Plays sounds.
	 */
	private void playSounds() {
		if (manager.isSoundEnabled()) {
			for (IBattleBulletProxy bp : getBattleBullets()) {
				if (bp.getFrame() == 0) {
					manager.getSoundManager().playBulletSound(bp, battleField.getWidth());
				}
			}

			boolean playedRobotHitRobot = false;

			for (IBattleRobotProxy rp : getBattleRobots()) {
				// Make sure that robot-hit-robot events do not play twice (one per colliding robot)
				if (rp.getState() == RobotPeerStatus.STATE_HIT_ROBOT) {
					if (playedRobotHitRobot) {
						continue;
					}
					playedRobotHitRobot = true;
				}

				manager.getSoundManager().playRobotSound(rp);
			}
		}
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
	private boolean isAborted() {
		synchronized (battleMonitor) {
			return aborted;
		}
	}

	private void updateTitle() {
		if (battleView == null) {
			return;
		}

		StringBuffer title = new StringBuffer("Robocode");

		if (isRunning()) {
			title.append(": ");

			if (currentTime == 0) {
				title.append("Starting round");
			} else {
				title.append(replay ? "Replaying round " : "Round ");
				title.append(roundNum + 1).append(" of ").append(numRounds);

				if (!battleManager.isPaused()) {
					boolean dispTps = battleView.isDisplayTPS();
					boolean dispFps = battleView.isDisplayFPS();

					if (dispTps | dispFps) {
						title.append(" (");

						if (dispTps) {
							title.append(turnsThisSec).append(" TPS");
						}
						if (dispTps & dispFps) {
							title.append(", ");
						}
						if (dispFps) {
							title.append(framesThisSec).append(" FPS");
						}
						title.append(')');
					}
				}
			}
		}
		if (battleManager.isPaused()) {
			title.append(" (paused)");
		}
		battleView.setTitle(title.toString());
	}

	public void mouseClicked(final MouseEvent e) {
		if (isRunning()) {
			for (IDisplayRobotProxy robotPeer : getDisplayRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
					robotPeer.onInteractiveEvent(new MouseClickedEvent(mirroredMouseEvent(e)));
				}
			}
		}
	}

	public void mouseEntered(final MouseEvent e) {
		if (isRunning()) {
			for (IDisplayRobotProxy robotPeer : getDisplayRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
					robotPeer.onInteractiveEvent(new MouseEnteredEvent(mirroredMouseEvent(e)));
				}
			}
		}
	}

	public void mouseExited(final MouseEvent e) {
		if (isRunning()) {
			for (IDisplayRobotProxy robotPeer : getDisplayRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
					robotPeer.onInteractiveEvent(new MouseExitedEvent(mirroredMouseEvent(e)));
				}
			}
		}
	}

	public void mousePressed(final MouseEvent e) {
		if (isRunning()) {
			for (IDisplayRobotProxy robotPeer : getDisplayRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
					robotPeer.onInteractiveEvent(new MousePressedEvent(mirroredMouseEvent(e)));
				}
			}
		}
	}

	public void mouseReleased(final MouseEvent e) {
		if (isRunning()) {
			for (IDisplayRobotProxy robotPeer : getDisplayRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
					robotPeer.onInteractiveEvent(new MouseReleasedEvent(mirroredMouseEvent(e)));
				}
			}
		}
	}

	public void mouseMoved(final MouseEvent e) {
		if (isRunning()) {
			for (IDisplayRobotProxy robotPeer : getDisplayRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
					robotPeer.onInteractiveEvent(new MouseMovedEvent(mirroredMouseEvent(e)));
				}
			}
		}
	}

	public void mouseDragged(final MouseEvent e) {
		if (isRunning()) {
			for (IDisplayRobotProxy robotPeer : getDisplayRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
					robotPeer.onInteractiveEvent(new MouseDraggedEvent(mirroredMouseEvent(e)));
				}
			}
		}
	}

	public void mouseWheelMoved(final MouseWheelEvent e) {
		if (isRunning()) {
			for (IDisplayRobotProxy robotPeer : getDisplayRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
					robotPeer.onInteractiveEvent(new MouseWheelMovedEvent(mirroredMouseWheelEvent(e)));
				}
			}
		}
	}

	// Returns a dummy component used to preventing robots in accessing the real source component
	private static Component getSafeEventComponent() {
		if (safeEventComponent == null) {
			safeEventComponent = new Label();
		}
		return safeEventComponent;
	}

	private MouseEvent mirroredMouseEvent(final MouseEvent e) {
		double scale;

		if (battleView.getWidth() < battleField.getWidth() || battleView.getHeight() < battleField.getHeight()) {
			scale = min((double) battleView.getWidth() / battleField.getWidth(),
					(double) battleView.getHeight() / battleField.getHeight());
		} else {
			scale = 1;
		}

		double dx = (battleView.getWidth() - scale * battleField.getWidth()) / 2;
		double dy = (battleView.getHeight() - scale * battleField.getHeight()) / 2;

		int x = (int) ((e.getX() - dx) / scale + 0.5);
		int y = (int) (battleField.getHeight() - (e.getY() - dy) / scale + 0.5);

		return new MouseEvent(getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), x, y,
				e.getClickCount(), e.isPopupTrigger(), e.getButton());
	}

	private MouseWheelEvent mirroredMouseWheelEvent(final MouseWheelEvent e) {
		double scale;

		if (battleView.getWidth() < battleField.getWidth() || battleView.getHeight() < battleField.getHeight()) {
			scale = min((double) battleView.getWidth() / battleField.getWidth(),
					(double) battleView.getHeight() / battleField.getHeight());
		} else {
			scale = 1;
		}

		double dx = (battleView.getWidth() - scale * battleField.getWidth()) / 2;
		double dy = (battleView.getHeight() - scale * battleField.getHeight()) / 2;

		int x = (int) ((e.getX() - dx) / scale + 0.5);
		int y = (int) (battleField.getHeight() - (e.getY() - dy) / scale + 0.5);

		return new MouseWheelEvent(getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), x, y,
				e.getClickCount(), e.isPopupTrigger(), e.getScrollType(), e.getScrollAmount(), e.getWheelRotation());
	}

	private final class KeyEventHandler implements KeyEventDispatcher {
		private Battle battle;
		private List<IDisplayRobotProxy> robots;

		public KeyEventHandler(Battle battle, List<IDisplayRobotProxy> robots) {
			this.battle = battle;
			this.robots = robots;
		}

		public boolean dispatchKeyEvent(KeyEvent e) {
			if (battle != null && battle.isRunning()) {
				for (IDisplayRobotProxy robotPeer : robots) {
					if (robotPeer.isAlive() && robotPeer.isInteractiveRobot() && robotPeer.isInteractiveListener()) {
						KeyEvent clone = cloneKeyEvent(e);

						switch (e.getID()) {
						case KeyEvent.KEY_TYPED:
							robotPeer.onInteractiveEvent(new KeyTypedEvent(clone));
							break;

						case KeyEvent.KEY_PRESSED:
							robotPeer.onInteractiveEvent(new KeyPressedEvent(clone));
							break;

						case KeyEvent.KEY_RELEASED:
							robotPeer.onInteractiveEvent(new KeyReleasedEvent(clone));
							break;
						}
					}
				}
			}
			return false;
		}

		private KeyEvent cloneKeyEvent(final KeyEvent e) {
			return new KeyEvent(getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), e.getKeyCode(),
					e.getKeyChar(), e.getKeyLocation());
		}

		public void cleanup() {
			robots = null;
			battle = null;
		}
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
