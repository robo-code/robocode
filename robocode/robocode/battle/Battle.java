/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Replaced the ContestantPeerVector, BulletPeerVector, and RobotPeerVector
 *       with plain Vector
 *     - Integration of robocode.render
 *     - BattleView is not given via the constructor anymore, but is retrieved
 *       from the RobocodeManager. In addition, the battleView is now allowed to
 *       be null, e.g. if no GUI is available
 *     - Code cleanup
 *******************************************************************************/
package robocode.battle;


import java.util.Vector;

import robocode.*;
import robocode.battlefield.BattleField;
import robocode.battleview.BattleView;
import robocode.control.BattleSpecification;
import robocode.dialog.RobotButton;
import robocode.manager.*;
import robocode.peer.*;
import robocode.peer.robot.RobotClassManager;
import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class Battle implements Runnable {

	// Objects we use
	private BattleView battleView;
	private BattleField battleField;
	private BattleManager battleManager;
	private RobocodeManager manager;

	// Battle items	
	private Thread battleThread;
	private boolean running;
	private boolean abortBattles;

	// Option related items
	private double gunCoolingRate = .1;

	// Inactivity related items
	private int inactiveTurnCount;
	private double inactivityEnergy;
	private long inactivityTime;

	// FPS related items
	private FpsManager fpsManager;
	private int initialDelay = 50;
	private int delay = 33;
	private int optimalFPS = 30;
	private int framesDisplayedThisSecond;
	private long displayFpsTime;

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
	private int stopTime;
	private int activeRobots;
	private Vector deathEvents = new Vector(); // <RobotPeer>

	// Objects in the battle
	private Vector robots; // <RobotPeer>
	private Vector contestants; // <ContestantPeer>
	private Vector bullets; // <BulletPeer>

	// Results related items
	private boolean exitOnComplete;
	private boolean showResultsDialog;

	// Results for RobocodeEngine controller
	private BattleSpecification battleSpecification;

	// Robot loading related items
	private Thread unsafeLoadRobotsThread;
	private Object unsafeLoaderMonitor = new Object();
	private boolean unsafeLoaderThreadRunning;
	private boolean robotsLoaded;

	// Pause related items
	private boolean wasPaused;

	/**
	 * Battle constructor
	 */
	public Battle(
			BattleField battleField,
			RobocodeManager manager) {
		super();

		if (manager.isGUIEnabled()) {
			this.battleView = manager.getWindowManager().getRobocodeFrame().getBattleView();
			this.battleView.setBattle(this);
		}
		this.battleField = battleField;
		this.manager = manager;
		this.battleManager = manager.getBattleManager();
		this.robots = new Vector(); // <RobotPeer>
		this.bullets = new Vector(); // <BulletPeer>
		this.contestants = new Vector(); // <ContestantPeer>
		this.fpsManager = new FpsManager(this);
	}

	/**
	 * When an object implementing interface <code>Runnable</code> is used 
	 * to create a thread, starting the thread causes the object's 
	 * <code>run</code> method to be called in that separately executing 
	 * thread. 
	 * <p>
	 * The general contract of the method <code>run</code> is that it may 
	 * take any action whatsoever.
	 *
	 * @see     java.lang.Thread#run()
	 */
	public void run() {
		running = true;

		if (unsafeLoadRobotsThread != null && Thread.currentThread() == unsafeLoadRobotsThread) {
			unsafeLoadRobots();
			return;
		}

		// Load robots
		try {
			initialize();
		} catch (NullPointerException e) {
			if (!abortBattles) {
				Utils.log("Null pointer exception in battle.initialize");
				e.printStackTrace(System.err);
				throw e;
			}
		}

		deterministic = true;
		nonDeterministicRobots = null;

		setRoundNum(0);
		while (!abortBattles && getRoundNum() < getNumRounds()) {
			if (battleView != null) {
				battleView.setTitle("Robocode: Starting Round " + (roundNum + 1) + " of " + numRounds);
			}
			try {
				setupRound();
				battleManager.setBattleRunning(true);
				if (battleView != null) {
					battleView.setTitle("Robocode: Round " + (roundNum + 1) + " of " + numRounds);
				}
				runRound();
				battleManager.setBattleRunning(false);
				cleanupRound();
			} catch (NullPointerException e) {
				if (!abortBattles) {
					Utils.log("Null pointer exception running a battle");
					throw e;
				} else {
					Utils.log("Warning:  Null pointer exception while aborting battle.");
				}
			} catch (Exception e) {
				Utils.log("Exception running a battle: " + e);
			}

			setRoundNum(getRoundNum() + 1);
		}

		for (int i = 0; i < robots.size(); i++) {
			RobotPeer r = (RobotPeer) robots.elementAt(i);

			r.out.close();
			r.getRobotThreadManager().cleanup();
		}
		unsafeLoadRobotsThread.interrupt();
		if (!abortBattles || showResultsDialog) {
			showResultsDialog = false;
			if (exitOnComplete) {
				battleManager.printResultsData(this);
			} else if (manager.getListener() != null) {
				if (!abortBattles) {
					battleManager.sendResultsToListener(this, manager.getListener());
				} else {
					manager.getListener().battleAborted(battleSpecification);
				}
			} else if (manager.isGUIEnabled()) {
				manager.getWindowManager().showResultsDialog(this);
			}
			if (battleView != null) {
				battleView.setTitle("Robocode");
				battleView.setPaintMode(BattleView.PAINTROBOCODELOGO);
				battleView.repaint();
			}
			System.gc();
			System.gc();
			System.gc();
			if (exitOnComplete) {
				System.exit(0);
			}
		} else {
			cleanup();
			if (manager.getListener() != null) {
				manager.getListener().battleAborted(battleSpecification);
			}
		}

		running = false;
	}

	public void addBullet(BulletPeer bullet) {
		bullets.add(bullet);
	}

	public void addRobot(RobotClassManager robotClassManager) {

		RobotPeer robotPeer = new RobotPeer(robotClassManager, battleManager.getManager().getRobotRepositoryManager(),
				battleManager.getManager().getProperties().getRobotFilesystemQuota());
		TeamPeer teamManager = robotClassManager.getTeamManager();

		if (teamManager != null) {
			teamManager.add(robotPeer);
			addContestant(teamManager);
		} else {
			addContestant(robotPeer);
		}
		robotPeer.setWidth(40);
		robotPeer.setHeight(40);
		robotPeer.setBattle(this);
		robotPeer.setBattleField(battleField);
		robotPeer.getOut();

		int count = 0;

		for (int i = 0; i < robots.size(); i++) {
			RobotPeer rp = (RobotPeer) robots.elementAt(i);

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

	public Vector getContestants() { // <ContestantPeer>
		return contestants;
	}

	public void cleanup() {
		robots.clear();
	}

	public void cleanupRound() {
		Utils.log("Round " + (roundNum + 1) + " cleaning up.");

		RobotPeer r;

		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);
			r.getRobotThreadManager().waitForStop();
			r.getRobotStatistics().generateTotals();
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

	public Vector getBullets() { // <BulletPeer>
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

	public int getOptimalFPS() {
		return optimalFPS;
	}

	public Vector getRobots() { // <RobotPeer>
		return robots;
	}

	public RobotPeer getRobotByName(String name) {
		for (int i = 0; i < robots.size(); i++) {
			if (((RobotPeer) robots.elementAt(i)).getName().equals(name)) {
				return (RobotPeer) robots.elementAt(i);
			}
		}
		return null;
	}

	public void setOptions() {
		setOptimalFPS(manager.getProperties().getOptionsBattleDesiredFps());
		if (battleView != null) {
			battleView.setDisplayOptions();
		}
	}

	public void initialize() {
		setOptions();

		// Starting loader thread
		ThreadGroup unsafeThreadGroup = new ThreadGroup("Robot Loader Group");

		unsafeThreadGroup.setDaemon(true);
		unsafeThreadGroup.setMaxPriority(Thread.NORM_PRIORITY);
		unsafeLoadRobotsThread = new Thread(unsafeThreadGroup, this);
		unsafeLoadRobotsThread.setName("Robot Loader");
		unsafeLoadRobotsThread.setDaemon(true);
		manager.getThreadManager().setRobotLoaderThread(unsafeLoadRobotsThread);
		unsafeLoadRobotsThread.start();

		RobotPeer r;

		if (battleView != null) {
			battleView.setPaintMode(BattleView.PAINTROBOCODELOGO);
		}
		if (manager.isGUIEnabled()) {
			manager.getWindowManager().getRobocodeFrame().clearRobotButtons();
		}

		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);
			r.preInitialize();
			if (manager.isGUIEnabled()) {
				manager.getWindowManager().getRobocodeFrame().addRobotButton(
						new RobotButton(manager.getRobotDialogManager(), r));
			}
		}
		if (manager.isGUIEnabled()) {
			manager.getWindowManager().getRobocodeFrame().validate();
		}

		if (battleView != null) {
			battleView.setPaintMode(BattleView.PAINTBATTLE);
			battleView.update();
		}
		// Pre-load robot classes without security...
		// loadClass WILL NOT LINK the class, so static "cheats" will not work.
		// in the safe robot loader the class is linked.
		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);
			try {
				String className = r.getRobotClassManager().getFullClassName();
				Class c = r.getRobotClassManager().getRobotClassLoader().loadRobotClass(className, true);

				r.getRobotClassManager().setRobotClass(c);
				r.getRobotFileSystemManager().initializeQuota();

				r = (RobotPeer) robots.elementAt(i);
				Class[] interfaces = c.getInterfaces();

				for (int j = 0; j < interfaces.length; j++) {
					if (interfaces[j].getName().equals("robocode.Droid")) {
						r.setDroid(true);
					}
				}
				double x = 0, y = 0, heading = 0;

				for (int j = 0; j < 1000; j++) {
					x = r.getWidth() + Math.random() * (battleField.getWidth() - 2 * r.getWidth());
					y = r.getHeight() + Math.random() * (battleField.getHeight() - 2 * r.getHeight());
					heading = 2 * Math.PI * Math.random();
					r.initialize(x, y, heading);
					if (validSpot(r) == true) {
						break;
					}
				}
				if (battleView != null) {
					battleView.update();
				}
			} catch (Throwable e) {
				r.out.println("SYSTEM: Could not load " + r.getName() + " : " + e);
				e.printStackTrace(r.out);
			}
		}
		abortBattles = false;
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
		Utils.log("Threads: ------------------------");
		for (int i = 0; i < systemThreads.length; i++) {
			if (systemThreads[i] != null) {
				Utils.log(systemThreads[i].getName());
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
		Utils.log("Let the games begin!");
		fpsManager.reset();
		boolean battleOver = false;

		endTimer = 0;
		stopTime = 0;

		RobotPeer r;

		currentTime = 0;
		delay = initialDelay;
		inactiveTurnCount = 0;

		while (!battleOver) {
			if (shouldPause()) {
				continue;
			}

			flushOldEvents();

			currentTime++;

			moveBullets();

			boolean zap = (inactiveTurnCount > inactivityTime);

			// Move all bots
			for (int i = 0; i < robots.size(); i++) {
				r = (RobotPeer) robots.elementAt(i);

				r.updateSayText();

				if (!r.isDead()) {
					// setWinner was here
					r.update();
				}
				if ((zap || abortBattles) && !r.isDead()) {
					if (abortBattles) {
						r.zap(5);
					} else {
						r.zap(.1);
					}
				}
			}

			handleDeathEvents();

			performScans();

			deathEvents.clear();

			if (abortBattles && getActiveRobots() > 0) {
				stopTime = endTimer;
			}

			battleOver = checkBattleOver();

			inactiveTurnCount++;

			computeActiveRobots();

			// Repaint
			if (battleView != null) {
				battleView.update();
			}

			// Robot time!
			wakeupRobots();

			fpsManager.update();

			// Delay to match framerate
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {}
		}
		if (battleView != null) {
			battleView.setPaintMode(BattleView.PAINTROBOCODELOGO);
		}
		bullets.clear();
	}

	private boolean shouldPause() {
		if (battleManager.isPaused() && abortBattles == false) {
			if (!wasPaused) {
				if (battleView != null) {
					if (roundNum < numRounds) {
						battleView.setTitle("Robocode: Round " + (roundNum + 1) + " of " + numRounds + " (paused)");
					} else {
						battleView.setTitle("Robocode (paused)");
					}
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
			return true;
		}
		if (wasPaused) {
			fpsManager.reset();
			if (battleView != null) {
				if (roundNum < numRounds) {
					battleView.setTitle("Robocode: Round " + (roundNum + 1) + " of " + numRounds);
				} else {
					battleView.setTitle("Robocode");
				}
			}
			return false;
		}
		return false;
	}

	private void computeActiveRobots() {
		RobotPeer r;
		int ar = 0;

		// Compute active robots
		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);
			if (!r.isDead()) {
				ar++;
			}
		}
		setActiveRobots(ar);
	}

	private void wakeupRobots() {
		RobotPeer r;

		// Wake up all robot threads 
		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);
			if (r.isRunning()) {
				synchronized (r) {
					// This call blocks until the
					// robot's thread actually wakes up.
					r.wakeup(this);

					// It's quite possible for simple robots to
					// complete their processing before we get here,
					// so we test if the robot is already asleep.

					if (!r.isSleeping()) {
						try {
							r.wait(manager.getCpuManager().getCpuConstant());
						} catch (InterruptedException e) {
							// ?
							Utils.log("Wait for " + r + " interrupted.");
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
						r.out.println(
								"SYSTEM: " + r.getName() + " has not performed any actions in a reasonable amount of time.");
						r.out.println("SYSTEM: No score will be generated.");
						r.getRobotStatistics().setNoScoring(true);
						r.getRobotThreadManager().forceStop();
					}
				}
			} // if isRunning
		}
	}

	private void flushOldEvents() {
		RobotPeer r;

		// New turn:  Flush any old events.	
		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);
			r.getEventManager().clear(currentTime - 1);
		}
	}

	private void moveBullets() {
		// Move all bullets
		for (int i = 0; i < bullets.size(); i++) {
			int osize = bullets.size();

			((BulletPeer) bullets.elementAt(i)).update();
			if (bullets.size() < osize) {
				i--;
			}
		}
	}

	private void handleDeathEvents() {
		RobotPeer r;

		if (deathEvents.size() > 0) {
			for (int i = 0; i < robots.size(); i++) {
				r = (RobotPeer) robots.elementAt(i);
				if (r.isDead()) {
					continue;
				}
				for (int j = 0; j < deathEvents.size(); j++) {
					RobotPeer de = (RobotPeer) deathEvents.elementAt(j);

					r.getEventManager().add(new RobotDeathEvent(de.getName()));
					if (r.getTeamPeer() == null || r.getTeamPeer() != de.getTeamPeer()) {
						r.getRobotStatistics().scoreSurvival();
					}
				}
			}
		}
		// Compute scores for dead robots
		for (int i = 0; i < deathEvents.size(); i++) {
			r = (RobotPeer) deathEvents.elementAt(i);
			if (r.getTeamPeer() == null) {
				r.getRobotStatistics().scoreDeath(getActiveContestantCount(r));
			} else {
				boolean teammatesalive = false;

				for (int j = 0; j < robots.size(); j++) {
					RobotPeer tm = (RobotPeer) robots.elementAt(j);

					if (tm.getTeamPeer() == r.getTeamPeer() && (!tm.isDead())) {
						teammatesalive = true;
						break;
					}
				}
				if (!teammatesalive) {
					r.getRobotStatistics().scoreDeath(getActiveContestantCount(r));
				}
			}
		}
	}

	private void performScans() {
		RobotPeer r;

		// Perform scans, handle messages
		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);

			if (!r.isDead()) {
				if (r.getScan() == true) {
					// Enter scan
					System.err.flush();

					r.scan();
					// Exit scan
					r.setScan(false);
				}

				if (r.getMessageManager() != null) {
					Vector messageEvents = r.getMessageManager().getMessageEvents();

					for (int j = 0; j < messageEvents.size(); j++) {
						r.getEventManager().add((MessageEvent) messageEvents.elementAt(j));
					}
					messageEvents.clear();
				}
			}
		}
	}

	private boolean checkBattleOver() {
		RobotPeer r;
		boolean battleOver = false;

		// Check game over
		if (oneTeamRemaining()) {
			if (endTimer == 0) // 4 * 30)
			{
				boolean leaderFirsts = false;
				TeamPeer winningTeam = null;

				for (int i = 0; i < robots.size(); i++) {
					r = (RobotPeer) robots.elementAt(i);
					if (!r.isDead()) {
						if (!r.isWinner()) {
							r.getRobotStatistics().scoreWinner();
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

			if (endTimer == 4 * 30) {
				for (int i = 0; i < robots.size(); i++) {
					r = (RobotPeer) robots.elementAt(i);
					if (!r.isDead()) {
						r.halt();
					}
				}
			}

			endTimer++;
			if (endTimer > 5 * 30) {
				battleOver = true;
			}
			if (abortBattles) {
				if (endTimer - stopTime > 30) {
					battleOver = true;
				}
			}
		}
		return battleOver;
	}

	public int getActiveContestantCount(RobotPeer peer) {
		int count = 0;

		for (int i = 0; i < contestants.size(); i++) {
			ContestantPeer c = (ContestantPeer) contestants.elementAt(i);

			if (c instanceof RobotPeer) {
				RobotPeer r = (RobotPeer) c;

				if (!r.isDead()) {
					count++;
				}
			} else if (c instanceof TeamPeer && c != peer.getTeamPeer()) {
				TeamPeer t = (TeamPeer) c;

				for (int j = 0; j < t.size(); j++) {
					if (!t.elementAt(j).isDead()) {
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

	public void setFPS(int frames, long time) {
		int fps = ((int) ((double) frames * (1000.0 / (double) time)));

		if (fps != optimalFPS) {
			int diffDelay = (int) ((1000.0 / optimalFPS) - (1000.0 / fps));

			if (diffDelay > 0) {
				diffDelay--;
			}
			delay += diffDelay;
			if (delay < 0) {
				delay = 0;
			}
		}
		if (battleView != null && battleView.isDisplayFps()) {
			framesDisplayedThisSecond += frames;
			displayFpsTime += time;
			if (displayFpsTime > 500) {
				battleView.setTitle(
						"Robocode: Round " + (roundNum + 1) + " of " + numRounds + " (" + 2 * framesDisplayedThisSecond
						+ " FPS)");
				displayFpsTime -= 500;
				framesDisplayedThisSecond = 0;
			}
		}
		if (manager.isGUIEnabled()
				&& (!manager.getWindowManager().getRobocodeFrame().isVisible()
						|| manager.getWindowManager().getRobocodeFrame().isIconified())) {
			delay = 0;
		}
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

	public void setOptimalFPS(int newOptimalFPS) {
		optimalFPS = newOptimalFPS;
	}

	public void setProperties(BattleProperties battleProperties) {
		try {
			setNumRounds(battleProperties.getNumRounds());
			setGunCoolingRate(battleProperties.getGunCoolingRate());
			setInactivityTime(battleProperties.getInactivityTime());
		} catch (Exception e) {
			Utils.log("Exception setting battle properties", e);
		}
	}

	public synchronized void setRobotsLoaded(boolean newRobotsLoaded) {
		robotsLoaded = newRobotsLoaded;
	}

	public void setupRound() {
		Utils.log("----------------------");
		Utils.log("Round " + (roundNum + 1) + " initializing..", false);
		currentTime = 0;

		setRobotsLoaded(false);
		while (!isUnsafeLoaderThreadRunning()) {
			// waiting for loader to start
			if (battleView != null) {
				battleView.repaint();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		RobotPeer r;

		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);
			if (roundNum > 0) {
				r.preInitialize();
			} // fake dead so robot won't display

			r.out.println("=========================");
			r.out.println("Round " + (roundNum + 1) + " of " + numRounds);
			r.out.println("=========================");
		}

		if (battleView != null) {
			battleView.setPaintMode(BattleView.PAINTBATTLE);
			battleView.update();
		}

		// Notifying loader
		synchronized (unsafeLoaderMonitor) {
			unsafeLoaderMonitor.notify();
		}
		while (!isRobotsLoaded()) {
			if (battleView != null) {
				battleView.update();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}

		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);
			if (r.getRobotClassManager().getClassNameManager().getFullPackage() != null
					&& r.getRobotClassManager().getClassNameManager().getFullPackage().length() > 18) {
				r.out.println("SYSTEM: Your package name is too long.  16 characters maximum please.");
				r.out.println("SYSTEM: Robot disabled.");
				r.setEnergy(0);
			}
			if (r.getRobotClassManager().getClassNameManager().getShortClassName().length() > 35) {
				r.out.println("SYSTEM: Your classname is too long.  32 characters maximum please.");
				r.out.println("SYSTEM: Robot disabled.");
				r.setEnergy(0);
			}
		}
		
		activeRobots = robots.size();
		manager.getThreadManager().reset();

		// Turning on robots
		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);
			manager.getThreadManager().addThreadGroup(r.getRobotThreadManager().getThreadGroup(), r);
			int waitTime = Math.min(300 * manager.getCpuManager().getCpuConstant(), 10000);

			synchronized (r) {
				try {
					Utils.log(".", false);
					r.getRobotThreadManager().start();
					// Wait for the robot to go to sleep (take action)
					r.wait(waitTime);

				} catch (InterruptedException e) {
					Utils.log("Wait for " + r + " interrupted.");
				}
			}
			if (battleView != null) {
				battleView.update();
			}
			if (!r.isSleeping()) {
				Utils.log("\n" + r.getName() + " still has not started after " + waitTime + " ms... giving up.");
			}
		}
		Utils.log("");
	}

	public void stop() {
		stop(false);
	}

	public void stop(boolean showResultsDialog) {
		if (running == false) {
			cleanup();
		} else {
			this.showResultsDialog = showResultsDialog;
			endTimer = 0;
			abortBattles = true;

			if (!showResultsDialog) {
				for (int i = 0; running == true && i < 40; i++) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
				}
				if (battleView != null) {
					battleView.setPaintMode(BattleView.PAINTROBOCODELOGO);
					battleView.repaint();
					battleView = null;
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
					unsafeLoaderMonitor.wait(180000);
				} catch (InterruptedException e) {}
			}
			// Loader awake
			if (getRoundNum() >= getNumRounds() || abortBattles == true) {
				// Robot loader thread terminating
				return;
			}
			// Loading robots
			RobotPeer r;

			for (int i = 0; i < robots.size(); i++) {
				r = (RobotPeer) robots.elementAt(i);
				r.setRobot(null);
				Class robotClass = null;

				try {
					manager.getThreadManager().setLoadingRobot(r);
					robotClass = r.getRobotClassManager().getRobotClass();
					if (robotClass == null) {
						r.out.println("SYSTEM: Skipping robot: " + r.getName());
						continue;
					}
					Robot bot = (Robot) robotClass.newInstance();

					bot.out = r.getOut();
					r.setRobot(bot);
					r.getRobot().setPeer(r);
					r.getRobot().out = r.getOut();
				} catch (IllegalAccessException e) {
					r.out.println("SYSTEM: Unable to instantiate this robot: " + e);
					r.out.println("SYSTEM: Is your constructor marked public?");
				} catch (Throwable e) {
					r.out.println("SYSTEM: An error occurred during initialization of " + r.getRobotClassManager());
					r.out.println("SYSTEM: " + e);
					e.printStackTrace(r.out);
				}
				if (getRoundNum() > 0) {
					r = (RobotPeer) robots.elementAt(i);
					double x = 0, y = 0, heading = 0;

					for (int j = 0; j < 1000; j++) {
						x = r.getWidth() + Math.random() * (battleField.getWidth() - 2 * r.getWidth());
						y = r.getHeight() + Math.random() * (battleField.getHeight() - 2 * r.getHeight());
						heading = 2 * Math.PI * Math.random();
						r.initialize(x, y, heading);
						if (validSpot(r) == true) {
							break;
						}
					}
					if (battleView != null) {
						battleView.update();
					}
				}
			} // for
			manager.getThreadManager().setLoadingRobot(null);
			setRobotsLoaded(true);
		}
	}

	public boolean validSpot(RobotPeer robot) {
		robot.updateBoundingBox();
		for (int i = 0; i < robots.size(); i++) {
			RobotPeer r = (RobotPeer) robots.elementAt(i);

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

	private synchronized boolean oneTeamRemaining() {
		if (getActiveRobots() <= 1) {
			return true;
		}

		boolean found = false;
		RobotPeer currentRobot = null;
		TeamPeer currentTeam = null;

		for (int i = 0; i < robots.size(); i++) {
			currentRobot = (RobotPeer) robots.elementAt(i);
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
	public synchronized void setUnsafeLoaderThreadRunning(boolean unsafeLoaderThreadRunning) {
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
}
