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
 *     - Ported to Java 5.0
 *     - Bugfixed sounds that were cut off after first battle
 *     - Changed initialize() to use loadClass() instead of loadRobotClass() if
 *       security is turned off
 *     - Access to managers is now static
 *     - Code cleanup
 *     Luis Crespo
 *     - Added sound features using the playSounds() method
 *     - Added debug step feature
 *     - Added isRunning()
 *******************************************************************************/
package robocode.battle;


import java.util.Vector;
import static java.lang.Math.*;

import robocode.*;
import robocode.battlefield.BattleField;
import robocode.battleview.BattleView;
import robocode.control.BattleSpecification;
import robocode.dialog.RobotButton;
import robocode.manager.*;
import robocode.peer.*;
import robocode.peer.robot.RobotClassManager;
import robocode.security.RobocodeClassLoader;
import robocode.sound.SoundManager;
import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class Battle implements Runnable {

	// Objects we use
	private BattleView battleView;
	private BattleField battleField;

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
	private int currentTime;
	private int endTimer;
	private int stopTime;
	private int activeRobots;
	private Vector<RobotPeer> deathEvents = new Vector<RobotPeer>();

	// Objects in the battle
	private Vector<RobotPeer> robots; 
	private Vector<ContestantPeer> contestants;
	private Vector<BulletPeer> bullets; 

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

	// Sound manager
	private SoundManager soundManager;

	/**
	 * Battle constructor
	 */
	public Battle(BattleField battleField) {
		super();

		if (RobocodeManager.isGUIEnabled()) {
			this.battleView = WindowManager.getRobocodeFrame().getBattleView();
			this.battleView.setBattle(this);
		}
		this.battleField = battleField;
		this.soundManager = new SoundManager();
		this.robots = new Vector<RobotPeer>();
		this.bullets = new Vector<BulletPeer>(); 
		this.contestants = new Vector<ContestantPeer>();
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

		boolean soundInitialized = false;
		
		if (RobocodeManager.isSoundEnabled()) {
			soundManager.init();
			soundInitialized = true;
		}

		setRoundNum(0);
		while (!abortBattles && getRoundNum() < getNumRounds()) {
			if (battleView != null) {
				battleView.setTitle("Robocode: Starting Round " + (roundNum + 1) + " of " + numRounds);
			}
			try {
				setupRound();
				BattleManager.setBattleRunning(true);
				if (battleView != null) {
					battleView.setTitle("Robocode: Round " + (roundNum + 1) + " of " + numRounds);
				}
				runRound();
				BattleManager.setBattleRunning(false);
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

		for (RobotPeer r : robots) {
			r.out.close();
			r.getRobotThreadManager().cleanup();
		}
		unsafeLoadRobotsThread.interrupt();
		if (!abortBattles || showResultsDialog) {
			showResultsDialog = false;
			if (exitOnComplete) {
				BattleManager.printResultsData(this);
			} else if (RobocodeManager.getListener() != null) {
				if (!abortBattles) {
					BattleManager.sendResultsToListener(this, RobocodeManager.getListener());
				} else {
					RobocodeManager.getListener().battleAborted(battleSpecification);
				}
			} else if (RobocodeManager.isGUIEnabled() && RobocodeProperties.getOptionsCommonShowResults()) {
				WindowManager.showResultsDialog(this);
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
			if (RobocodeManager.getListener() != null) {
				RobocodeManager.getListener().battleAborted(battleSpecification);
			}
		}

		if (soundInitialized) {
			soundManager.dispose();
		}

		running = false;
	}

	public void addBullet(BulletPeer bullet) {
		bullets.add(bullet);
	}

	public void addRobot(RobotClassManager robotClassManager) {

		RobotPeer robotPeer = new RobotPeer(robotClassManager);
		TeamPeer teamManager = robotClassManager.getTeamManager();

		if (teamManager != null) {
			teamManager.add(robotPeer);
			addContestant(teamManager);
		} else {
			addContestant(robotPeer);
		}
		robotPeer.setBattle(this);
		robotPeer.setBattleField(battleField);
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

	public Vector<ContestantPeer> getContestants() {
		return contestants;
	}

	public void cleanup() {
		robots.clear();
	}

	public void cleanupRound() {
		Utils.log("Round " + (roundNum + 1) + " cleaning up.");

		for (RobotPeer r : robots) {
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

	public Vector<BulletPeer> getBullets() { 
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

	public Vector<RobotPeer> getRobots() {
		return robots;
	}

	public RobotPeer getRobotByName(String name) {
		for (RobotPeer r : robots) {
			if (r.getName().equals(name)) {
				return r;
			}
		}
		return null;
	}

	public void setOptions() {
		setDesiredTPS(RobocodeProperties.getOptionsBattleDesiredTPS());
		if (battleView != null) {
			battleView.setDisplayOptions();
		}
	}

	public void setDesiredTPS(int desiredTPS) {
		this.desiredTPS = desiredTPS;
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
		ThreadManager.setRobotLoaderThread(unsafeLoadRobotsThread);
		unsafeLoadRobotsThread.start();

		if (battleView != null) {
			battleView.setPaintMode(BattleView.PAINTROBOCODELOGO);
		}
		if (RobocodeManager.isGUIEnabled()) {
			WindowManager.getRobocodeFrame().clearRobotButtons();
		}

		for (RobotPeer r : robots) {
			r.preInitialize();
			if (RobocodeManager.isGUIEnabled()) {
				WindowManager.getRobocodeFrame().addRobotButton(new RobotButton(r));
			}
		}
		if (RobocodeManager.isGUIEnabled()) {
			WindowManager.getRobocodeFrame().validate();
		}

		if (battleView != null) {
			battleView.setPaintMode(BattleView.PAINTBATTLE);
			battleView.update();
		}
		// Pre-load robot classes without security...
		// loadClass WILL NOT LINK the class, so static "cheats" will not work.
		// in the safe robot loader the class is linked.
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

				Class<?>[] interfaces = c.getInterfaces();

				for (Class<?> i : interfaces) {
					if (i.getName().equals("robocode.Droid")) {
						r.setDroid(true);
					}
				}
				double x = 0, y = 0, heading = 0;

				for (int j = 0; j < 1000; j++) {
					x = RobotPeer.WIDTH + random() * (battleField.getWidth() - 2 * RobotPeer.WIDTH);
					y = RobotPeer.HEIGHT + random() * (battleField.getHeight() - 2 * RobotPeer.HEIGHT);
					heading = 2 * PI * random();
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

	public boolean isRobotsLoaded() {
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
		boolean battleOver = false;

		endTimer = 0;
		stopTime = 0;

		currentTime = 0;
		inactiveTurnCount = 0;

		int turnsThisSec = 0;
		int framesThisSec = 0;

		long frameStartTime;
		int currentFrameMillis = 0;

		int totalRobotMillisThisSec = 0;
		int totalFrameMillisThisSec = 0;
		int totalTurnMillisThisSec;

		float estFrameTimeThisSec;
		float estimatedFPS = 0;

		int estimatedTurnMillisThisSec;

		int delay = 0;
		
		boolean resetThisSec = true;

		BattleManager.startNewRound();

		while (!battleOver) {
			if (shouldPause() && !BattleManager.shouldStep()) {
				resetThisSec = true;
				continue;
			}

			long turnStartTime = System.currentTimeMillis();
			
			if (resetThisSec) {
				resetThisSec = false;

				startTimeThisSec = turnStartTime;

				turnsThisSec = 0;
				framesThisSec = 0;

				totalRobotMillisThisSec = 0;
				totalFrameMillisThisSec = 0;
			}

			flushOldEvents();

			currentTime++;
			turnsThisSec++;

			moveBullets();

			boolean zap = (inactiveTurnCount > inactivityTime);

			// Move all bots
			for (RobotPeer r : robots) {
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

			// Repaint frame

			// Store the start time before the frame update
			frameStartTime = System.currentTimeMillis();

			if (battleView != null) {
				
				// Update the battle view if the frame has not been painted yet this second
				// or if it's time to paint the next frame
				if ((totalFrameMillisThisSec == 0)
						|| (frameStartTime - startTimeThisSec) > (framesThisSec * 1000f / estimatedFPS)) {
					battleView.update();

					framesThisSec++;
				}
				if (!WindowManager.getRobocodeFrame().isIconified()) {
					playSounds();
				}
			}

			// Calculate the time spend on the frame update
			currentFrameMillis = (int) (System.currentTimeMillis() - frameStartTime);

			// Calculate the total time spend on frame updates this second
			totalFrameMillisThisSec += currentFrameMillis;

			// Robot time!
			wakeupRobots();

			// Calculate the total time used for the robots only this second
			totalRobotMillisThisSec += (int) (System.currentTimeMillis() - turnStartTime) - currentFrameMillis;

			// Calculate the total turn time this second
			totalTurnMillisThisSec = totalRobotMillisThisSec + totalFrameMillisThisSec;

			// Estimate the time remaining this second to spend on frame updates
			estFrameTimeThisSec = max(0, 1000f - desiredTPS * (float) totalTurnMillisThisSec / turnsThisSec);

			// Estimate the possible FPS based on the estimated frame time 
			estimatedFPS = max(1, framesThisSec * estFrameTimeThisSec / totalFrameMillisThisSec);

			// Estimate the time that will be used on the total turn this second
			estimatedTurnMillisThisSec = desiredTPS * totalTurnMillisThisSec / turnsThisSec;

			// Calculate delay needed for keeping the desired TPS (Turns Per Second)
			if (RobocodeManager.isGUIEnabled() && WindowManager.getRobocodeFrame().isVisible()
					&& !WindowManager.getRobocodeFrame().isIconified()) {
				delay = (estimatedTurnMillisThisSec >= 1000) ? 0 : (1000 - estimatedTurnMillisThisSec) / desiredTPS;
			} else {
				delay = 0;
			}

			// Set flag for if the second has passed
			resetThisSec = ((desiredTPS - turnsThisSec == 0)
					|| ((System.currentTimeMillis() - startTimeThisSec) >= 1000));

			// Delay to match desired TPS
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {}

			if (resetThisSec && battleView != null) {
				StringBuffer titleBuf = new StringBuffer("Robocode: Round ");

				titleBuf.append(roundNum + 1).append(" of ").append(numRounds);

				boolean dispTps = battleView.isDisplayTPS();
				boolean dispFps = battleView.isDisplayFPS();

				if (dispTps | dispFps) {
					titleBuf.append(" (");
					
					if (dispTps) {
						titleBuf.append(turnsThisSec).append(" TPS");
					}
					if (dispTps & dispFps) {
						titleBuf.append(", ");
					}
					if (dispFps) {
						titleBuf.append(framesThisSec).append(" FPS");
					}
					titleBuf.append(')');
				}
				battleView.setTitle(titleBuf.toString());
			}
		}
		if (battleView != null) {
			battleView.setPaintMode(BattleView.PAINTROBOCODELOGO);
		}
		bullets.clear();
	}
	
	private boolean shouldPause() {
		if (BattleManager.isPaused() && abortBattles == false) {
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
			if (battleView != null) {
				if (roundNum < numRounds) {
					battleView.setTitle("Robocode: Round " + (roundNum + 1) + " of " + numRounds);
				} else {
					battleView.setTitle("Robocode");
				}
			}
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
		for (RobotPeer r : robots) {
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
							r.wait(CpuManager.getCpuConstant());
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
		// New turn:  Flush any old events.
		for (RobotPeer r : robots) {
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
				r.getRobotStatistics().scoreDeath(getActiveContestantCount(r));
			} else {
				boolean teammatesalive = false;

				for (RobotPeer tm : robots) {
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
		// Perform scans, handle messages
		for (RobotPeer r : robots) {
			if (!r.isDead()) {
				if (r.getScan() == true) {
					// Enter scan
					System.err.flush();

					r.scan();
					// Exit scan
					r.setScan(false);
				}

				if (r.getMessageManager() != null) {
					Vector<MessageEvent> messageEvents = r.getMessageManager().getMessageEvents();

					for (int j = 0; j < messageEvents.size(); j++) {
						r.getEventManager().add(messageEvents.elementAt(j));
					}
					messageEvents.clear();
				}
			}
		}
	}

	private boolean checkBattleOver() {
		boolean battleOver = false;

		// Check game over
		if (oneTeamRemaining()) {
			if (endTimer == 0) {
				boolean leaderFirsts = false;
				TeamPeer winningTeam = null;

				for (RobotPeer r : robots) {
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
				for (RobotPeer r : robots) {
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

		for (ContestantPeer c : contestants) {
			if (c instanceof RobotPeer && !((RobotPeer) c).isDead()) {
				count++;
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

		for (RobotPeer r : robots) {
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

		for (RobotPeer r : robots) {
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
		
		ThreadManager.reset();

		// Turning on robots
		for (RobotPeer r : robots) {
			ThreadManager.addThreadGroup(r.getRobotThreadManager().getThreadGroup(), r);
			int waitTime = min(300 * CpuManager.getCpuConstant(), 10000);

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
					unsafeLoaderMonitor.wait();
				} catch (InterruptedException e) {}
			}
			// Loader awake
			if (getRoundNum() >= getNumRounds() || abortBattles == true) {
				// Robot loader thread terminating
				return;
			}
			// Loading robots
			for (RobotPeer r : robots) {
				r.setRobot(null);
				Class<?> robotClass = null;

				try {
					ThreadManager.setLoadingRobot(r);
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
					double x = 0, y = 0, heading = 0;

					for (int j = 0; j < 1000; j++) {
						x = RobotPeer.WIDTH + random() * (battleField.getWidth() - 2 * RobotPeer.WIDTH);
						y = RobotPeer.HEIGHT + random() * (battleField.getHeight() - 2 * RobotPeer.HEIGHT);
						heading = 2 * PI * random();
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
			ThreadManager.setLoadingRobot(null);
			setRobotsLoaded(true);
		}
	}

	public boolean validSpot(RobotPeer robot) {
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
	public int getActiveRobots() {
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
	public boolean isUnsafeLoaderThreadRunning() {
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
	 * Plays sounds.
	 */
	private void playSounds() {
		if (RobocodeManager.isSoundEnabled()) {
			int i;
			BulletPeer bp;
			RobotPeer rp;

			for (i = 0; i < getBullets().size(); i++) {
				bp = (BulletPeer) getBullets().get(i);
				soundManager.playBulletSound(bp);
			}
			for (i = 0; i < getRobots().size(); i++) {
				rp = (RobotPeer) getRobots().get(i);
				// Robot-hit-robot events play twice: once for each robot.
				// The code could be improved in order to play only once.
				soundManager.playRobotSound(rp);
			}
		}
	}

	/**
	 * Informs on whether the battle is running or not.
	 * 
	 * @return true if the battle is running, false otherwise
	 */
	public boolean isRunning() {
		return running;
	}
}
