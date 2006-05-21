/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.battle;


import java.util.Vector;

import robocode.MessageEvent;
import robocode.Robot;
import robocode.RobotDeathEvent;
import robocode.SkippedTurnEvent;
import robocode.battlefield.BattleField;
import robocode.battleview.BattleView;
import robocode.control.BattleSpecification;
import robocode.dialog.RobotButton;
import robocode.manager.BattleManager;
import robocode.manager.FpsManager;
import robocode.manager.RobocodeManager;
import robocode.peer.BulletPeer;
import robocode.peer.BulletPeerVector;
import robocode.peer.ContestantPeer;
import robocode.peer.ContestantPeerVector;
import robocode.peer.RobotPeer;
import robocode.peer.RobotPeerVector;
import robocode.peer.TeamPeer;
import robocode.peer.robot.RobotClassManager;
import robocode.util.Utils;


/**
 * Insert the type's description here.
 * Creation date: (12/13/2000 9:38:33 PM)
 * @author: Mathew A. Nelson
 */
public class Battle implements Runnable {

	// Objects we use
	private BattleView battleView;
	private BattleField battleField;
	private BattleManager battleManager = null;
	private RobocodeManager manager = null;

	// Battle items	
	private java.lang.Thread battleThread;
	private boolean running = false;
	private boolean abortBattles = false;

	// Option related items
	private double gunCoolingRate = .1;

	// Inactivity related items
	private int inactiveTurnCount = 0;
	private double inactivityEnergy = 0.0;
	private long inactivityTime;

	// FPS related items
	private FpsManager fpsManager = null;
	private int initialDelay = 50;
	private int delay = 33;
	private int optimalFPS = 30;
	private int framesDisplayedThisSecond = 0;
	private long displayFpsTime = 0;

	// Turn skip related items	
	private int maxSkippedTurns = 30;
	private int maxSkippedTurnsWithIO = 240;
	private String nonDeterministicRobots = null;
	private boolean deterministic = true;

	// Current round items
	private int numRounds = 0;
	private int roundNum = 0;
	private int currentTime = 0;
	private int endTimer = 0;
	private int stopTime = 0;
	private int activeRobots = 0;
	private RobotPeerVector deathEvents = new RobotPeerVector();

	// Objects in the battle
	private RobotPeerVector robots;
	private ContestantPeerVector contestants;
	private BulletPeerVector bullets;

	// Results related items
	private boolean exitOnComplete = false;
	private boolean showResultsDialog = false;

	// Results for RobocodeEngine controller
	private BattleSpecification battleSpecification = null;

	// Robot loading related items
	private java.lang.Thread unsafeLoadRobotsThread = null;
	private Object unsafeLoaderMonitor = new Object();
	private boolean unsafeLoaderThreadRunning = false;
	private boolean robotsLoaded = false;

	// Pause related items
	boolean wasPaused = false;

	/**
	 * Battle constructor
	 */
	public Battle(
			BattleView battleView,
			BattleField battleField,
			RobocodeManager manager) {
		super();

		this.battleView = battleView;
		this.battleField = battleField;
		this.manager = manager;
		this.battleManager = manager.getBattleManager();
		battleView.setBattle(this);
		robots = new RobotPeerVector();
		bullets = new BulletPeerVector();
		contestants = new ContestantPeerVector();
		fpsManager = new FpsManager(this);
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
				log("Null pointer exception in battle.initialize");
				e.printStackTrace();
				throw e;
			}
		}

		// battleActiveTime = System.currentTimeMillis();
		// battleMonitorThread = new Thread(this);
		// battleMonitorThread.start();

		deterministic = true;
		nonDeterministicRobots = null;

		setRoundNum(0);
		while (!abortBattles && getRoundNum() < getNumRounds()) {
			// battleActiveTime = System.currentTimeMillis();
			battleView.setTitle("Robocode: Starting Round " + (roundNum + 1) + " of " + numRounds);
			try {
				setupRound();
				battleManager.setBattleRunning(true);
				battleView.setTitle("Robocode: Round " + (roundNum + 1) + " of " + numRounds);
				runRound();
				battleManager.setBattleRunning(false);
				cleanupRound();
			} catch (NullPointerException e) {
				if (!abortBattles) {
					log("Null pointer exception running a battle");
					throw e;
				} else {
					log("Warning:  Null pointer exception while aborting battle.");
				}
			} catch (Exception e) {
				log("Exception running a battle: " + e);
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
			} else {
				manager.getWindowManager().showResultsDialog(this);
			}
			battleView.setTitle("Robocode");
			battleView.setPaintMode(BattleView.PAINTROBOCODELOGO);
			battleView.repaint();
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

	/**
	 * Insert the method's description here.
	 * Creation date: (12/23/2000 6:00:49 AM)
	 * @param bullet robocode.peer.BulletPeer
	 */
	public void addBullet(BulletPeer bullet) {
		bullets.add(bullet);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 12:17:31 PM)
	 * @param robot robocode.peer.robot.RobotClassManager
	 */
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
			if ((robots.elementAt(i)).getRobotClassManager().getClassNameManager().getFullClassNameWithVersion().equals(
					robotPeer.getRobotClassManager().getClassNameManager().getFullClassNameWithVersion())) {
				if (count == 0) {
					if (!robots.elementAt(i).isDuplicate()) {
						robots.elementAt(i).setDuplicate(0);
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

	public ContestantPeerVector getContestants() {
		return contestants;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/28/2000 1:52:24 PM)
	 */
	public void cleanup() {

		/*
		 for (int i = 0; i < robotGraphics.size(); i++)
		 {
		 ((RobotGraphics)robotGraphics.remove(0)).finalize();
		 
		 }
		 */
		
		/*
		 for (int i = 0; i < robots.size(); i++)
		 {
		 PrivateRobot r = (PrivateRobot)robots.elementAt(i);
		 if (r.robotDialog != null)
		 {
		 r.robotDialog.dispose();
		 r.robotDialog = null;
		 }
		 }
		 */
		robots.clear();

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/29/2000 9:21:26 AM)
	 */
	public void cleanupRound() {

		log("Round " + (roundNum + 1) + " cleaning up.");

		RobotPeer r;

		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);
			r.getRobotThreadManager().waitForStop();
			r.getRobotStatistics().generateTotals();
		}

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/16/2001 2:14:31 PM)
	 * @param r robocode.JSafeRobot
	 */
	public void generateDeathEvents(RobotPeer r) {
		deathEvents.add(r);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 2:44:31 PM)
	 * @return robocode.BattleField
	 */
	public BattleField getBattleField() {
		return battleField;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/21/2000 12:39:04 PM)
	 * @return java.lang.Thread
	 */
	public java.lang.Thread getBattleThread() {
		return battleThread;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/15/2001 12:45:22 PM)
	 * @return java.util.Vector
	 */
	public BulletPeerVector getBullets() {
		return bullets;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/15/2001 12:47:29 PM)
	 * @return int
	 */
	public int getCurrentTime() {
		return currentTime;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/16/2001 1:59:55 PM)
	 * @return int
	 */
	public double getGunCoolingRate() {
		return gunCoolingRate;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/22/2001 10:50:39 AM)
	 * @return int
	 */
	public long getInactivityTime() {
		return inactivityTime;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/1/2001 5:25:01 PM)
	 * @return int
	 */
	// public int getMaxWaitCount() {
	// return maxWaitCount;
	// }

	/**
	 * Insert the method's description here.
	 * Creation date: (10/23/2001 9:54:39 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getNonDeterministicRobots() {
		return nonDeterministicRobots;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/16/2001 1:49:17 PM)
	 * @return int
	 */
	public int getNumRounds() {
		return numRounds;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/22/2001 10:58:20 AM)
	 * @return int
	 */
	public int getOptimalFPS() {
		return optimalFPS;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/15/2001 12:45:03 PM)
	 * @return java.util.Vector
	 */
	public RobotPeerVector getRobots() {
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
		battleView.setDisplayOptions();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/29/2000 9:13:50 AM)
	 */
	public void initialize() {
		setOptions();
		manager.getImageManager().resetColorIndex();

		// log("Starting loader thread.");
		ThreadGroup unsafeThreadGroup = new ThreadGroup("Robot Loader Group");

		unsafeThreadGroup.setDaemon(true);
		unsafeThreadGroup.setMaxPriority(Thread.NORM_PRIORITY);
		unsafeLoadRobotsThread = new Thread(unsafeThreadGroup, this);
		unsafeLoadRobotsThread.setName("Robot Loader");
		unsafeLoadRobotsThread.setDaemon(true);
		manager.getThreadManager().setRobotLoaderThread(unsafeLoadRobotsThread);
		unsafeLoadRobotsThread.start();

		RobotPeer r;

		battleView.setPaintMode(BattleView.PAINTROBOCODELOGO);
		manager.getWindowManager().getRobocodeFrame().clearRobotButtons();

		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);
			r.preInitialize();
			manager.getWindowManager().getRobocodeFrame().addRobotButton(
					new RobotButton(manager.getRobotDialogManager(), r));
		}
		manager.getWindowManager().getRobocodeFrame().validate();

		battleView.setPaintMode(BattleView.PAINTBATTLE);
		battleView.draw(false);
		// Pre-load robot classes without security...
		// loadClass WILL NOT LINK the class, so static "cheats" will not work.
		// in the safe robot loader the class is linked.
		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);
			// RobocodeClassLoader cl = new RobocodeClassLoader(getClass().getClassLoader(),r.getRobotClassManager().getRobotProperties(),r);
			// r.getRobotClassManager().setRobotClassLoader(cl);
			try {

				// getClass().getClassLoader().loadClass(r.robotClass);
				String className = r.getRobotClassManager().getFullClassName();
				Class c = r.getRobotClassManager().getRobotClassLoader().loadRobotClass(className, true);

				/* String expectedClassName = robocode.getRobotDatabase().getProperty(className);
				 if (!expectedClassName.equals(c.getSuperclass().getName()))
				 {
				 log("Updating robot database: " + className + " is now of class: " + c.getSuperclass().getName());
				 robocode.getRobotDatabase().setProperty(className,c.getSuperclass().getName());
				 robocode.saveRobotDatabase();
				 }
				 */
				r.getRobotClassManager().setRobotClass(c);
				r.getRobotFileSystemManager().initializeQuota();
				// for (int j = 0; j < r.getRobotClass().getInnerClasses().size(); j++)
				// {
				// log("Loading inner class " + (String)r.getRobotClass().getInnerClasses().elementAt(j));
				// robotClassLoader.loadRobotClass((String)r.getRobotClass().getInnerClasses().elementAt(j));
				// }
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
				battleView.draw(true);

			} catch (Throwable e) {
				r.out.println("SYSTEM: Could not load " + r.getName() + " : " + e);
				e.printStackTrace(r.out);
				// if (e instanceof Exception) {
				// ((Exception)e).printStackTrace(r.out);
				// }
			}
		}
		abortBattles = false;

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/28/2001 4:36:23 PM)
	 * @return boolean
	 */
	public boolean isDeterministic() {
		return deterministic;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/23/2001 4:07:25 PM)
	 * @return boolean
	 */
	public boolean isExitOnComplete() {
		return exitOnComplete;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/21/2000 2:36:41 PM)
	 * @return boolean
	 */
	public synchronized boolean isRobotsLoaded() {
		return robotsLoaded;
	}

	private void log(String s) {
		Utils.log(s);
		System.err.flush();
	}

	private void log(String s, boolean newline) {
		Utils.log(s, newline);
	}

	private void log(Throwable e) {
		Utils.log(e);
	}

	private void log(String s, Throwable e) {
		Utils.log(s, e);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/21/2000 12:05:59 PM)
	 */
	public void printSystemThreads() {

		Thread systemThreads[] = new Thread[256];

		battleThread.getThreadGroup().enumerate(systemThreads, false);
		log("Threads: ------------------------");
		for (int i = 0; i < systemThreads.length; i++) {
			if (systemThreads[i] != null) {
				log(systemThreads[i].getName());
			}
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/15/2000 12:17:31 PM)
	 * @param bullet robocode.peer.BulletPeer
	 */
	public void removeBullet(BulletPeer bullet) {

		bullets.remove(bullet);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/4/2001 3:12:25 AM)
	 */
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

	/**
	 * Insert the method's description here.
	 * Creation date: (12/29/2000 9:24:41 AM)
	 */
	public void runRound() {
		log("Let the games begin!");

		fpsManager.initialize();
		boolean battleOver = false;

		endTimer = 0;
		stopTime = 0;

		RobotPeer r;

		currentTime = 0;
		delay = initialDelay;
		inactiveTurnCount = 0;

		// lastscore = 0;
		// nextscore = 0;

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
				try {
					battleView.draw(false);
				} catch (NullPointerException e) {
					log(e);
				}
			}

			// Robot time!
			wakeupRobots();

			fpsManager.update();

			// log("to delay");
			// Delay to match framerate
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {}

		}
		battleView.setPaintMode(BattleView.PAINTROBOCODELOGO);
		bullets.clear();
		// System.gc();
	}

	private boolean shouldPause() {
		if (battleManager.isPaused() && abortBattles == false) {
			if (!wasPaused) {
				if (roundNum < numRounds) {
					battleView.setTitle("Robocode: Round " + (roundNum + 1) + " of " + numRounds + " (paused)");
				} else {
					battleView.setTitle("Robocode (paused)");
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
			return true;
		}
		if (wasPaused) {
			fpsManager.reset();
			if (roundNum < numRounds) {
				battleView.setTitle("Robocode: Round " + (roundNum + 1) + " of " + numRounds);
			} else {
				battleView.setTitle("Robocode");
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
		int count;

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
							log("Wait for " + r + " interrupted.");
						}
					}
				}
				if (r.isSleeping() || !r.isRunning()) {
					r.setSkippedTurns(0);
				} else {
					r.setSkippedTurns(r.getSkippedTurns() + 1);
					// log(r.getName() + " has skipped " + r.getSkippedTurns() + " turns.");
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
					if (
							(!r.isIORobot() && (r.getSkippedTurns() > maxSkippedTurns))
							|| (r.isIORobot() && (r.getSkippedTurns() > maxSkippedTurnsWithIO))) {
						r.out.println(
								"SYSTEM: " + r.getName() + " has not performed any actions in a reasonable amount of time.");
						r.out.println("SYSTEM: No score will be generated.");
						r.getRobotStatistics().setNoScoring(true);
						r.getRobotThreadManager().forceStop();
					}
				}
			} // if isrunning
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

			(bullets.elementAt(i)).update();
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
					r.getEventManager().add(new RobotDeathEvent(((RobotPeer) deathEvents.elementAt(j)).getName()));
					if (r.getTeamPeer() == null || r.getTeamPeer() != deathEvents.elementAt(j).getTeamPeer()) {
						r.getRobotStatistics().scoreSurvival();
					}
				}
			}
		}
		// Compute scores for dead robots
		for (int i = 0; i < deathEvents.size(); i++) {
			r = deathEvents.elementAt(i);

			if (r.getTeamPeer() == null) {
				r.getRobotStatistics().scoreDeath(getActiveContestantCount(r));
			} else {
				boolean teammatesalive = false;

				for (int j = 0; j < robots.size(); j++) {
					if (robots.elementAt(j).getTeamPeer() == r.getTeamPeer() && (!robots.elementAt(j).isDead())) {
						teammatesalive = true;
						break;
					}
				}
				if (!teammatesalive) {
					r.getRobotStatistics().scoreDeath(getActiveContestantCount(r));
				}
				// else
				// log(r.getName() + " still has teammates alive, not scoring at time" + getCurrentTime());
			}
			// if (r.getTeamPeer() == null)
			// r.getRobotStatistics().scoreDeath(getActiveRobots() - deathEvents.size());
			// else
			// {
			// }
		}
	}

	private void performScans() {

		RobotPeer r;

		// log("scans");
		// Perform scans, handle messages
		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);

			if (!r.isDead()) {
				if (r.getScan() == true) {
					// log("enter scan");
					System.err.flush();

					r.scan();
					// log("exit scan");
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
					r = robots.elementAt(i);
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
					r = robots.elementAt(i);
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
			ContestantPeer c = contestants.elementAt(i);

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

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/2000 2:44:31 PM)
	 * @param newBattleField robocode.BattleField
	 */
	public void setBattleField(BattleField newBattleField) {
		battleField = newBattleField;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/21/2000 12:39:04 PM)
	 * @param newBattleThread java.lang.Thread
	 */
	public void setBattleThread(java.lang.Thread newBattleThread) {
		battleThread = newBattleThread;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/15/2001 12:47:29 PM)
	 * @param newCurrentTime int
	 */
	public void setCurrentTime(int newCurrentTime) {
		currentTime = newCurrentTime;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (9/28/2001 4:36:23 PM)
	 * @param newDeterministic boolean
	 */
	void setDeterministic(boolean newDeterministic) {
		deterministic = newDeterministic;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/23/2001 4:07:25 PM)
	 * @param newExitOnComplete boolean
	 */
	public void setExitOnComplete(boolean newExitOnComplete) {
		exitOnComplete = newExitOnComplete;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/4/2001 1:21:50 AM)
	 */
	public void setFPS(int frames, long time) {

		int fps = ((int) ((double) frames * (1000.0 / (double) time)));

		if (fps != optimalFPS) {
			int diff = fps - optimalFPS;
			// log("FPS is " + fps + " but want " + optimalFPS);
			int diffDelay = (int) ((1000.0 / optimalFPS) - (1000.0 / fps));

			if (diffDelay > 0) {
				diffDelay--;
			}
			delay += diffDelay;
			if (delay < 0) {
				delay = 0;
				// log("Delay at minimum: " + delay);
			}
			// else
			// log("Battle adjusting delay " + diffDelay + " to " + delay);
		}
		if (battleView.isDisplayFps()) {
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
		if (manager.getWindowManager().getRobocodeFrame().isVisible() == false
				|| manager.getWindowManager().getRobocodeFrame().isIconified() == true) {
			delay = 0;
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/16/2001 1:59:55 PM)
	 * @param newFireDelay int
	 */
	public void setGunCoolingRate(double newGunCoolingRate) {
		gunCoolingRate = newGunCoolingRate;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/22/2001 10:50:39 AM)
	 * @param newInactivityTime int
	 */
	public void setInactivityTime(long newInactivityTime) {
		inactivityTime = newInactivityTime;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/16/2001 1:49:17 PM)
	 * @param numRounds int
	 */
	public void setNumRounds(int numRounds) {
		this.numRounds = numRounds;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/22/2001 10:58:20 AM)
	 * @param newOptimalFPS int
	 */
	public void setOptimalFPS(int newOptimalFPS) {
		optimalFPS = newOptimalFPS;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/18/2001 1:41:00 PM)
	 * @param rules java.util.Properties
	 */
	public void setProperties(BattleProperties battleProperties) {
		try {
			setNumRounds(battleProperties.getNumRounds());
			setGunCoolingRate(battleProperties.getGunCoolingRate());
			setInactivityTime(battleProperties.getInactivityTime());
		} catch (Exception e) {
			log("Exception setting battle properties", e);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/21/2000 2:36:41 PM)
	 * @param newRobotsLoaded boolean
	 */
	public synchronized void setRobotsLoaded(boolean newRobotsLoaded) {
		robotsLoaded = newRobotsLoaded;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/29/2000 9:21:26 AM)
	 */
	public void setupRound() {

		// battleActiveTime = System.currentTimeMillis();
		log("----------------------");
		log("Round " + (roundNum + 1) + " initializing..", false);
		currentTime = 0;

		setRobotsLoaded(false);
		while (!isUnsafeLoaderThreadRunning()) {
			// log("waiting for loader to start.");
			battleView.repaint();
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

		battleView.setPaintMode(BattleView.PAINTBATTLE);
		battleView.draw(true);

		// log("notifying loader");
		synchronized (unsafeLoaderMonitor) {
			unsafeLoaderMonitor.notify();
		}
		while (!isRobotsLoaded()) {
			// battleActiveTime = System.currentTimeMillis();
			// log("waiting for loader");
			battleView.draw(false);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}

		// battleActiveTime = System.currentTimeMillis();

		// log("Placing Robots");
		/* moved to unsafeloadrobots and initialize.
		 for (int i = 0; i < robots.size(); i++)
		 {
		 r = (RobotPeer)robots.elementAt(i);
		 double  x=0,y=0,heading=0;
		 for (int j = 0; j < 1000; j++)
		 {
		 x = r.getWidth() + Math.random() * (battleField.getWidth() - 2 * r.getWidth());
		 y = r.getHeight() + Math.random() * (battleField.getHeight() - 2 * r.getHeight());
		 heading = 2 * Math.PI * Math.random();
		 r.initialize(x,y,heading);
		 if (validSpot(r) == true) break;
		 }
		 }
		 */
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
		// log("Turning on robots.",false);
		for (int i = 0; i < robots.size(); i++) {
			r = (RobotPeer) robots.elementAt(i);
			manager.getThreadManager().addThreadGroup(r.getRobotThreadManager().getThreadGroup(), r);
			int waitTime = Math.min(300 * manager.getCpuManager().getCpuConstant(), 10000);

			synchronized (r) {
				try {
					log(".", false);
					r.getRobotThreadManager().start();
					// Wait for the robot to go to sleep (take action)
					r.wait(waitTime);

					/* for (int count = 0; r.isSleeping() == false && count < 300 * manager.getCpuManager().getCpuConstant(); count++)
					 {
					 r.wait(1);
					 if (count % 100 == 0)
					 log("Still waiting for " + r.getName() + " after" +  (System.currentTimeMillis() - start) + " ms");
					 }
					 */
				} catch (InterruptedException e) {
					log("Wait for " + r + " interrupted.");
				}
			}
			battleView.draw(false);
			if (!r.isSleeping()) {
				log("\n" + r.getName() + " still has not started after " + waitTime + " ms... giving up.");
			}
		}
		log("");

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/28/2000 2:19:52 PM)
	 */
	public void stop() {
		stop(false);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/28/2000 2:19:52 PM)
	 */
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
				battleView.setPaintMode(BattleView.PAINTROBOCODELOGO);
				battleView.repaint();
				battleView = null;
			}
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/21/2000 2:33:22 PM)
	 */
	public void unsafeLoadRobots() {

		while (true) {
			// log("Loader waiting.");
			synchronized (unsafeLoaderMonitor) {
				try {
					setUnsafeLoaderThreadRunning(true);
					unsafeLoaderMonitor.wait(180000);
				} catch (InterruptedException e) {}
			}
			// log("Loader awake.");
			if (getRoundNum() >= getNumRounds() || abortBattles == true) {
				// log("Robot loader thread terminating.");
				return;
			}
			// log("Loading robots");
			RobotPeer r;

			for (int i = 0; i < robots.size(); i++) {
				r = (RobotPeer) robots.elementAt(i);
				r.setRobot(null);
				Class robotClass = null;

				try {
					manager.getThreadManager().setLoadingRobot(r);
					robotClass = r.getRobotClassManager().getRobotClass();
					if (robotClass == null) {
						// log("SYSTEM:  Skipping robot: " + r.getName());
						r.out.println("SYSTEM: Skipping robot: " + r.getName());
						continue;
					}
					// log("Loading with classloader: " + robotClass.getClassLoader());
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
					battleView.draw(false);
				}
				// else
				// r.initialize(r.getX(),r.getY(),r.getHeading());
				// else initialize did it for us...

			} // for
			manager.getThreadManager().setLoadingRobot(null);
			// robocode.getThreadManager().setRobotLoaderThread(null);	
			setRobotsLoaded(true);
		}

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/23/2000 12:32:34 PM)
	 * @return boolean
	 */
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
	 * @param activeRobots The activeRobots to set
	 */
	private synchronized void setActiveRobots(int activeRobots) {
		this.activeRobots = activeRobots;
	}

	/**
	 * Gets the roundNum.
	 * @return Returns a int
	 */
	public int getRoundNum() {
		return roundNum;
	}

	/**
	 * Sets the roundNum.
	 * @param roundNum The roundNum to set
	 */
	public void setRoundNum(int roundNum) {
		this.roundNum = roundNum;
	}

	/**
	 * Gets the unsafeLoaderThreadRunning.
	 * @return Returns a boolean
	 */
	public synchronized boolean isUnsafeLoaderThreadRunning() {
		return unsafeLoaderThreadRunning;
	}

	/**
	 * Sets the unsafeLoaderThreadRunning.
	 * @param unsafeLoaderThreadRunning The unsafeLoaderThreadRunning to set
	 */
	public synchronized void setUnsafeLoaderThreadRunning(boolean unsafeLoaderThreadRunning) {
		this.unsafeLoaderThreadRunning = unsafeLoaderThreadRunning;
	}

	/**
	 * Gets the battleSpecification.
	 * @return Returns a BattleSpecification
	 */
	public BattleSpecification getBattleSpecification() {
		return battleSpecification;
	}

	/**
	 * Sets the battleSpecification.
	 * @param battleSpecification The battleSpecification to set
	 */
	public void setBattleSpecification(BattleSpecification battleSpecification) {
		this.battleSpecification = battleSpecification;
	}

	/**
	 * Gets the manager.
	 * @return Returns a RobocodeManager
	 */
	public RobocodeManager getManager() {
		return manager;
	}

}
