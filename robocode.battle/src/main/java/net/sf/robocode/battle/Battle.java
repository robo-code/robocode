/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
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
 *     - decomposed RobotPeer from RobotProxy, now sending messages beteen them
 *******************************************************************************/
package net.sf.robocode.battle;


import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.battle.peer.BulletPeer;
import net.sf.robocode.battle.peer.ContestantPeer;
import net.sf.robocode.battle.peer.RobotPeer;
import net.sf.robocode.battle.peer.TeamPeer;
import net.sf.robocode.battle.snapshot.TurnSnapshot;
import net.sf.robocode.host.ICpuManager;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRobotRepositoryItem;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.settings.ISettingsManager;
import robocode.*;
import robocode.control.RandomFactory;
import robocode.control.RobotResults;
import robocode.control.RobotSpecification;
import robocode.control.events.*;
import robocode.control.events.RoundEndedEvent;
import robocode.control.snapshot.BulletState;
import robocode.control.snapshot.ITurnSnapshot;

import java.util.*;
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
public final class Battle extends BaseBattle {

	private static final int DEBUG_TURN_WAIT_MILLIS = 10 * 60 * 1000; // 10 seconds

	private final IHostManager hostManager;
	private final long cpuConstant;

	// Inactivity related items
	private int inactiveTurnCount;
	private double inactivityEnergy;

	// Turn skip related items
	private boolean parallelOn;
	private long millisWait;
	private int microWait;

	// Objects in the battle
	private int robotsCount;
	private List<RobotPeer> robots = new ArrayList<RobotPeer>();
	private List<ContestantPeer> contestants = new ArrayList<ContestantPeer>();
	private final List<BulletPeer> bullets = new CopyOnWriteArrayList<BulletPeer>();
	private int activeRobots;

	// Death events
	private final List<RobotPeer> deathRobots = new CopyOnWriteArrayList<RobotPeer>();

	// Flag specifying if debugging is enabled thru the debug command line option
	private final boolean isDebugging;

	// Initial robot start positions (if any)
	private double[][] initialRobotPositions;

	public Battle(ISettingsManager properties, IBattleManager battleManager, IHostManager hostManager, ICpuManager cpuManager, BattleEventDispatcher eventDispatcher) {
		super(properties, battleManager, eventDispatcher);
		isDebugging = System.getProperty("debug", "false").equals("true");
		this.hostManager = hostManager;
		this.cpuConstant = cpuManager.getCpuConstant();
	}

	public void setup(RobotSpecification[] battlingRobotsList, BattleProperties battleProperties, boolean paused) {
		isPaused = paused;
		battleRules = HiddenAccess.createRules(battleProperties.getBattlefieldWidth(),
				battleProperties.getBattlefieldHeight(), battleProperties.getNumRounds(), battleProperties.getGunCoolingRate(),
				battleProperties.getInactivityTime());
		robotsCount = battlingRobotsList.length;
		computeInitialPositions(battleProperties.getInitialPositions());
		createPeers(battlingRobotsList);
	}

	private void createPeers(RobotSpecification[] battlingRobotsList) {
		// create teams
		Hashtable<String, Integer> countedNames = new Hashtable<String, Integer>();
		List<String> teams = new ArrayList<String>();
		List<String> teamDuplicates = new ArrayList<String>();
		List<Integer> robotDuplicates = new ArrayList<Integer>();

		// count duplicate robots, enumerate teams, enumerate team members
		for (RobotSpecification specification : battlingRobotsList) {
			final String name = ((IRobotRepositoryItem) HiddenAccess.getFileSpecification(specification)).getUniqueFullClassNameWithVersion();

			if (countedNames.containsKey(name)) {
				int value = countedNames.get(name);

				countedNames.put(name, value == 1 ? 3 : value + 1);
			} else {
				countedNames.put(name, 1);
			}

			String teamFullName = HiddenAccess.getRobotTeamName(specification);

			if (teamFullName != null) {
				if (!teams.contains(teamFullName)) {
					teams.add(teamFullName);
					String teamName = teamFullName.substring(0, teamFullName.length() - 6);

					if (countedNames.containsKey(teamName)) {
						int value = countedNames.get(teamName);

						countedNames.put(teamName, value == 1 ? 3 : value + 1);
					} else {
						countedNames.put(teamName, 1);
					}
				}
			}
		}

		Hashtable<String, List<String>> teamMembers = new Hashtable<String, List<String>>();

		// name teams
		for (int i = teams.size() - 1; i >= 0; i--) {
			String teamFullName = teams.get(i);
			String name = teamFullName.substring(0, teamFullName.length() - 6);
			Integer order = countedNames.get(name);
			String newTeamName = name;

			if (order > 1) {
				newTeamName = name + " (" + (order - 1) + ")";
			}
			teamDuplicates.add(0, newTeamName);
			teamMembers.put(teamFullName, new ArrayList<String>());
			countedNames.put(name, order - 1);
		}

		// name robots
		for (int i = battlingRobotsList.length - 1; i >= 0; i--) {
			RobotSpecification specification = battlingRobotsList[i];
			String name = ((IRobotRepositoryItem) HiddenAccess.getFileSpecification(specification)).getUniqueFullClassNameWithVersion();
			Integer order = countedNames.get(name);
			int duplicate = -1;

			String newName = name;

			if (order > 1) {
				duplicate = (order - 2);
				newName = name + " (" + (order - 1) + ")";
			}
			countedNames.put(name, (order - 1));
			robotDuplicates.add(0, duplicate);

			String teamFullName = HiddenAccess.getRobotTeamName(specification);

			if (teamFullName != null) {
				List<String> members = teamMembers.get(teamFullName);

				members.add(newName);
			}
		}

		// create teams
		Hashtable<String, TeamPeer> namedTeams = new Hashtable<String, TeamPeer>();

		// create robots
		for (int i = 0; i < battlingRobotsList.length; i++) {
			RobotSpecification specification = battlingRobotsList[i];
			TeamPeer team = null;

			String teamFullName = HiddenAccess.getRobotTeamName(specification);

			int cindex = contestants.size();

			if (teamFullName != null) {
				if (!namedTeams.containsKey(teamFullName)) {
					final int teamIndex = teams.indexOf(teamFullName);
					String newTeamName = teamDuplicates.get(teamIndex);

					team = new TeamPeer(newTeamName, teamMembers.get(teamFullName), contestants.size());

					namedTeams.put(teamFullName, team);
					contestants.add(team);

				} else {
					team = namedTeams.get(teamFullName);
					if (team != null) {
						cindex = team.getContestIndex();
					}
				}
			}
			Integer duplicate = robotDuplicates.get(i);
			RobotPeer robotPeer = new RobotPeer(this, hostManager, specification, duplicate, team, robots.size(), cindex);

			robots.add(robotPeer);
			if (team == null) {
				contestants.add(robotPeer);
			}
		}
	}

	public void registerDeathRobot(RobotPeer r) {
		deathRobots.add(r);
	}

	public BattleRules getBattleRules() {
		return battleRules;
	}

	public int getRobotsCount() {
		return robotsCount;
	}

	public boolean isDebugging() {
		return isDebugging;
	}

	public void removeBullet(BulletPeer bullet) {
		bullets.remove(bullet);
	}

	public void addBullet(BulletPeer bullet) {
		bullets.add(bullet);
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

	/**
	 * Gets the activeRobots.
	 *
	 * @return Returns a int
	 */
	public int getActiveRobots() {
		return activeRobots;
	}

	@Override
	public void cleanup() {

		if (contestants != null) {
			contestants.clear();
			contestants = null;
		}

		if (robots != null) {
			robots.clear();
			robots = null;
		}

		super.cleanup();

		battleManager = null;

		// Request garbage collecting
		for (int i = 4; i >= 0; i--) { // Make sure it is run
			System.gc();
		}
	}

	@Override
	protected void initializeBattle() {
		super.initializeBattle();

		parallelOn = System.getProperty("PARALLEL", "false").equals("true");
		if (parallelOn) {
			// how could robots share CPUs ?
			double parallelConstant = robots.size() / Runtime.getRuntime().availableProcessors();

			// four CPUs can't run two single threaded robot faster than two CPUs
			if (parallelConstant < 1) {
				parallelConstant = 1;
			}
			final long waitTime = (long) (cpuConstant * parallelConstant);

			millisWait = waitTime / 1000000;
			microWait = (int) (waitTime % 1000000);
		} else {
			millisWait = cpuConstant / 1000000;
			microWait = (int) (cpuConstant % 1000000);
		}
		if (microWait == 0) {
			microWait = 1;
		}
	}

	@Override
	protected void finalizeBattle() {
		eventDispatcher.onBattleFinished(new BattleFinishedEvent(isAborted()));

		if (!isAborted()) {
			eventDispatcher.onBattleCompleted(new BattleCompletedEvent(battleRules, computeBattleResults()));
		}

		for (RobotPeer robotPeer : robots) {
			robotPeer.cleanup();
		}
		hostManager.resetThreadManager();

		super.finalizeBattle();
	}

	@Override
	protected void preloadRound() {
		super.preloadRound();

		// At this point the unsafe loader thread will now set itself to wait for a notify

		for (RobotPeer robotPeer : robots) {
			robotPeer.initializeRound(robots, initialRobotPositions);
			robotPeer.println("=========================");
			robotPeer.println("Round " + (getRoundNum() + 1) + " of " + getNumRounds());
			robotPeer.println("=========================");
		}

		if (getRoundNum() == 0) {
			eventDispatcher.onBattleStarted(new BattleStartedEvent(battleRules, robots.size(), false));
			if (isPaused()) {
				eventDispatcher.onBattlePaused(new BattlePausedEvent());
			}
		}

		computeActiveRobots();

		hostManager.resetThreadManager();
	}

	@Override
	protected void initializeRound() {
		super.initializeRound();

		inactiveTurnCount = 0;

		// Start robots

		long waitMillis;
		int waitNanos;

		if (isDebugging) {
			waitMillis = DEBUG_TURN_WAIT_MILLIS;
			waitNanos = 0;
		} else {
			long waitTime = Math.min(300 * cpuConstant, 10000000000L);

			waitMillis = waitTime / 1000000;
			waitNanos = (int) (waitTime % 1000000);
		}

		for (RobotPeer robotPeer : getRobotsAtRandom()) {
			robotPeer.startRound(waitMillis, waitNanos);
		}

		Logger.logMessage(""); // puts in a new-line in the log message

		final ITurnSnapshot snapshot = new TurnSnapshot(this, robots, bullets, false);

		eventDispatcher.onRoundStarted(new RoundStartedEvent(snapshot, getRoundNum()));
	}

	@Override
	protected void finalizeRound() {
		super.finalizeRound();

		for (RobotPeer robotPeer : robots) {
			robotPeer.waitForStop();
			robotPeer.getRobotStatistics().generateTotals();
		}

		bullets.clear();

		eventDispatcher.onRoundEnded(new RoundEndedEvent(getRoundNum(), currentTime, totalTurns));
	}

	@Override
	protected void initializeTurn() {
		super.initializeTurn();

		eventDispatcher.onTurnStarted(new TurnStartedEvent());
	}

	@Override
	protected void runTurn() {
		super.runTurn();

		loadCommands();

		updateBullets();

		updateRobots();

		handleDeadRobots();

		if (isAborted() || oneTeamRemaining()) {
			shutdownTurn();
		}

		inactiveTurnCount++;

		computeActiveRobots();

		publishStatuses();

		// Robot time!
		wakeupRobots();
	}

	@Override
	protected void shutdownTurn() {
		if (getEndTimer() == 0) {
			if (isAborted()) {
				for (RobotPeer robotPeer : getRobotsAtRandom()) {
					if (!robotPeer.isDead()) {
						robotPeer.println("SYSTEM: game aborted.");
					}
				}
			} else if (oneTeamRemaining()) {
				boolean leaderFirsts = false;
				TeamPeer winningTeam = null;

				final robocode.RoundEndedEvent roundEndedEvent = new robocode.RoundEndedEvent(getRoundNum(), currentTime,
						totalTurns); 

				for (RobotPeer robotPeer : getRobotsAtRandom()) {
					robotPeer.addEvent(roundEndedEvent);
					if (!robotPeer.isDead()) {
						if (!robotPeer.isWinner()) {
							robotPeer.getRobotStatistics().scoreLastSurvivor();
							robotPeer.setWinner(true);
							robotPeer.println("SYSTEM: " + robotPeer.getName() + " wins the round.");
							robotPeer.addEvent(new WinEvent());
							if (robotPeer.getTeamPeer() != null) {
								if (robotPeer.isTeamLeader()) {
									leaderFirsts = true;
								} else {
									winningTeam = robotPeer.getTeamPeer();
								}
							}
						}
					}
				}
				if (!leaderFirsts && winningTeam != null) {
					winningTeam.getTeamLeader().getRobotStatistics().scoreFirsts();
				}
			}
		}

		if (getEndTimer() == 1 && (isAborted() || isLastRound())) {

			List<RobotPeer> orderedRobots = new ArrayList<RobotPeer>(robots);

			Collections.sort(orderedRobots);
			Collections.reverse(orderedRobots);

			for (int rank = 0; rank < robots.size(); rank++) {
				RobotPeer robotPeer = orderedRobots.get(rank);

				robotPeer.getStatistics().setRank(rank + 1);
				BattleResults resultsForRobot = robotPeer.getStatistics().getFinalResults();

				robotPeer.addEvent(new BattleEndedEvent(isAborted(), resultsForRobot));
			}
		}

		if (getEndTimer() > 4 * 30) {
			for (RobotPeer robotPeer : robots) {
				robotPeer.setHalt(true);
			}
		}

		super.shutdownTurn();
	}

	@Override
	protected void finalizeTurn() {
		eventDispatcher.onTurnEnded(new TurnEndedEvent(new TurnSnapshot(this, robots, bullets, true)));

		super.finalizeTurn();
	}

	private BattleResults[] computeBattleResults() {
		ArrayList<BattleResults> results = new ArrayList<BattleResults>();

		List<ContestantPeer> orderedContestants = new ArrayList<ContestantPeer>(contestants);

		Collections.sort(orderedContestants);
		Collections.reverse(orderedContestants);

		// noinspection ForLoopReplaceableByForEach
		for (int i = 0; i < contestants.size(); i++) {
			results.add(null);
		}
		for (int rank = 0; rank < contestants.size(); rank++) {
			RobotSpecification robotSpec = null;
			ContestantPeer contestant = orderedContestants.get(rank);

			contestant.getStatistics().setRank(rank + 1);
			BattleResults battleResults = contestant.getStatistics().getFinalResults();

			if (contestant instanceof RobotPeer) {
				robotSpec = ((RobotPeer) contestant).getRobotSpecification();
			} else if (contestant instanceof TeamPeer) {
				robotSpec = ((TeamPeer) contestant).getTeamLeader().getRobotSpecification();
			}

			results.set(contestant.getContestIndex(), new RobotResults(robotSpec, battleResults));
		}

		return results.toArray(new BattleResults[results.size()]);
	}

	/**
	 * Returns a list of all robots in random order. This method is used to gain fair play in Robocode,
	 * so that a robot placed before another robot in the list will not gain any benefit when the game
	 * checks if a robot has won, is dead, etc.
	 * This method was introduced as two equal robots like sample.RamFire got different scores even
	 * though the code was exactly the same.
	 *
	 * @return a list of robot peers.
	 */
	private List<RobotPeer> getRobotsAtRandom() {
		List<RobotPeer> shuffledList = new ArrayList<RobotPeer>(robots);

		Collections.shuffle(shuffledList, RandomFactory.getRandom());
		return shuffledList;
	}

	/**
	 * Returns a list of all bullets in random order. This method is used to gain fair play in Robocode.
	 *
	 * @return a list of bullet peers.
	 */
	private List<BulletPeer> getBulletsAtRandom() {
		List<BulletPeer> shuffledList = new ArrayList<BulletPeer>(bullets);

		Collections.shuffle(shuffledList, RandomFactory.getRandom());
		return shuffledList;
	}

	/**
	 * Returns a list of all death robots in random order. This method is used to gain fair play in Robocode.
	 *
	 * @return a list of robot peers.
	 */
	private List<RobotPeer> getDeathRobotsAtRandom() {
		List<RobotPeer> shuffledList = new ArrayList<RobotPeer>(deathRobots);

		Collections.shuffle(shuffledList, RandomFactory.getRandom());
		return shuffledList;
	}

	private void loadCommands() {
		// this will load commands, including bullets from last turn 
		for (RobotPeer robotPeer : robots) {
			robotPeer.performLoadCommands();
		}
	}

	private void updateBullets() {
		for (BulletPeer bullet : getBulletsAtRandom()) {
			bullet.update(getRobotsAtRandom(), getBulletsAtRandom());
			if (bullet.getState() == BulletState.INACTIVE) {
				bullets.remove(bullet);
			}
		}
	}

	private void updateRobots() {
		boolean zap = (inactiveTurnCount > battleRules.getInactivityTime());

		final double zapEnergy = isAborted() ? 5 : zap ? .1 : 0;

		// Move all bots
		for (RobotPeer robotPeer : getRobotsAtRandom()) {
			robotPeer.performMove(getRobotsAtRandom(), zapEnergy);
		}

		// Scan after moved all
		for (RobotPeer robotPeer : getRobotsAtRandom()) {
			robotPeer.performScan(getRobotsAtRandom());
		}
	}

	private void handleDeadRobots() {

		for (RobotPeer deadRobot : getDeathRobotsAtRandom()) {
			// Compute scores for dead robots
			if (deadRobot.getTeamPeer() == null) {
				deadRobot.getRobotStatistics().scoreRobotDeath(getActiveContestantCount(deadRobot));
			} else {
				boolean teammatesalive = false;

				for (RobotPeer tm : robots) {
					if (tm.getTeamPeer() == deadRobot.getTeamPeer() && (!tm.isDead())) {
						teammatesalive = true;
						break;
					}
				}
				if (!teammatesalive) {
					deadRobot.getRobotStatistics().scoreRobotDeath(getActiveContestantCount(deadRobot));
				}
			}

			// Publish death to live robots
			for (RobotPeer robotPeer : getRobotsAtRandom()) {
				if (!robotPeer.isDead()) {
					robotPeer.addEvent(new RobotDeathEvent(deadRobot.getName()));

					if (robotPeer.getTeamPeer() == null || robotPeer.getTeamPeer() != deadRobot.getTeamPeer()) {
						robotPeer.getRobotStatistics().scoreSurvival();
					}
				}
			}
		}

		deathRobots.clear();
	}

	private void publishStatuses() {
		for (RobotPeer robotPeer : robots) {
			robotPeer.publishStatus(currentTime);
		}
	}

	private void computeActiveRobots() {
		int ar = 0;

		// Compute active robots
		for (RobotPeer robotPeer : robots) {
			if (!robotPeer.isDead()) {
				ar++;
			}
		}
		this.activeRobots = ar;
	}

	private void wakeupRobots() {
		// Wake up all robot threads
		final List<RobotPeer> robotsAtRandom = getRobotsAtRandom();

		if (parallelOn) {
			wakeupParallel(robotsAtRandom);
		} else {
			wakeupSerial(robotsAtRandom);
		}
	}

	private void wakeupSerial(List<RobotPeer> robotsAtRandom) {
		for (RobotPeer robotPeer : robotsAtRandom) {
			if (robotPeer.isRunning()) {
				// This call blocks until the
				// robot's thread actually wakes up.
				robotPeer.waitWakeup();

				if (robotPeer.isAlive()) {
					if (isDebugging || robotPeer.isPaintEnabled() || robotPeer.isPaintRecorded()) {
						robotPeer.waitSleeping(DEBUG_TURN_WAIT_MILLIS, 1);
					} else if (currentTime == 1) {
						robotPeer.waitSleeping(millisWait * 10, 1);
					} else {
						robotPeer.waitSleeping(millisWait, microWait);
					}
					robotPeer.setSkippedTurns();
				}
			}
		}
	}

	private void wakeupParallel(List<RobotPeer> robotsAtRandom) {
		for (RobotPeer robotPeer : robotsAtRandom) {
			if (robotPeer.isRunning()) {
				robotPeer.waitWakeup();
			}
		}
		for (RobotPeer robotPeer : robotsAtRandom) {
			if (robotPeer.isRunning() && robotPeer.isAlive()) {
				if (isDebugging || robotPeer.isPaintEnabled() || robotPeer.isPaintRecorded()) {
					robotPeer.waitSleeping(DEBUG_TURN_WAIT_MILLIS, 1);
				} else if (currentTime == 1) {
					robotPeer.waitSleeping(millisWait * 10, 1);
				} else {
					robotPeer.waitSleeping(millisWait, microWait);
				}
			}
		}
		for (RobotPeer robotPeer : robotsAtRandom) {
			if (robotPeer.isAlive()) {
				robotPeer.setSkippedTurns();
			}
		}
	}

	private int getActiveContestantCount(RobotPeer peer) {
		int count = 0;

		for (ContestantPeer c : contestants) {
			if (c instanceof RobotPeer && !((RobotPeer) c).isDead()) {
				count++;
			} else if (c instanceof TeamPeer && c != peer.getTeamPeer()) {
				for (RobotPeer robotPeer : (TeamPeer) c) {
					if (!robotPeer.isDead()) {
						count++;
						break;
					}
				}
			}
		}
		return count;
	}

	private void computeInitialPositions(String initialPositions) {
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

			final Random random = RandomFactory.getRandom();

			x = RobotPeer.WIDTH + random.nextDouble() * (battleRules.getBattlefieldWidth() - 2 * RobotPeer.WIDTH);
			y = RobotPeer.HEIGHT + random.nextDouble() * (battleRules.getBattlefieldHeight() - 2 * RobotPeer.HEIGHT);
			heading = 2 * Math.PI * random.nextDouble();

			int len = coords.length;

			if (len >= 1) {
				// noinspection EmptyCatchBlock
				try {
					x = Double.parseDouble(coords[0].replaceAll("[\\D]", ""));
				} catch (NumberFormatException e) {}

				if (len >= 2) {
					// noinspection EmptyCatchBlock
					try {
						y = Double.parseDouble(coords[1].replaceAll("[\\D]", ""));
					} catch (NumberFormatException e) {}

					if (len >= 3) {
						// noinspection EmptyCatchBlock
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
					currentTeam = currentRobot.getTeamPeer();
				} else {
					if (currentTeam == null && currentRobot.getTeamPeer() == null) {
						return false;
					}
					if (currentTeam != currentRobot.getTeamPeer()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	// --------------------------------------------------------------------------
	// Processing and maintaining robot and battle controls
	// --------------------------------------------------------------------------

	public void killRobot(int robotIndex) {
		sendCommand(new KillRobotCommand(robotIndex));
	}

	public void setPaintEnabled(int robotIndex, boolean enable) {
		sendCommand(new EnableRobotPaintCommand(robotIndex, enable));
	}

	public void setAllPaintRecorded(boolean enable) {
		sendCommand(new AllRobotsPaintRecordCommand(enable));
	}

	public void setSGPaintEnabled(int robotIndex, boolean enable) {
		sendCommand(new EnableRobotSGPaintCommand(robotIndex, enable));
	}

	public void sendInteractiveEvent(Event e) {
		sendCommand(new SendInteractiveEventCommand(e));
	}

	private class KillRobotCommand extends RobotCommand {
		KillRobotCommand(int robotIndex) {
			super(robotIndex);
		}

		public void execute() {
			robots.get(robotIndex).kill();
		}
	}


	private class AllRobotsPaintRecordCommand extends Command {
		final boolean enablePaintRecord;

		AllRobotsPaintRecordCommand(boolean enablePaintRecord) {
			this.enablePaintRecord = enablePaintRecord;
		}

		public void execute() {
			for (RobotPeer robot : robots) {
				robot.setPaintRecorded(enablePaintRecord);
			}
		}
	}


	private class EnableRobotPaintCommand extends RobotCommand {
		final boolean enablePaint;

		EnableRobotPaintCommand(int robotIndex, boolean enablePaint) {
			super(robotIndex);
			this.enablePaint = enablePaint;
		}

		public void execute() {
			robots.get(robotIndex).setPaintEnabled(enablePaint);
		}
	}


	private class EnableRobotSGPaintCommand extends RobotCommand {
		final boolean enableSGPaint;

		EnableRobotSGPaintCommand(int robotIndex, boolean enableSGPaint) {
			super(robotIndex);
			this.enableSGPaint = enableSGPaint;
		}

		public void execute() {
			robots.get(robotIndex).setSGPaintEnabled(enableSGPaint);
		}
	}


	private class SendInteractiveEventCommand extends Command {
		public final Event event;

		SendInteractiveEventCommand(Event event) {
			this.event = event;
		}

		public void execute() {
			for (RobotPeer robotPeer : robots) {
				if (robotPeer.isInteractiveRobot()) {
					robotPeer.addEvent(event);
				}
			}
		}
	}
}
