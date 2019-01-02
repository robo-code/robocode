/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
import net.sf.robocode.io.RobocodeProperties;
import net.sf.robocode.repository.IRobotItem;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.settings.ISettingsManager;
import robocode.*;
import robocode.control.RandomFactory;
import robocode.control.RobotResults;
import robocode.control.RobotSetup;
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
	private int nanoWait;

	// Objects in the battle
	private int robotsCount;
	private List<RobotPeer> robots = new ArrayList<RobotPeer>();
	private List<ContestantPeer> contestants = new ArrayList<ContestantPeer>();
	private final List<BulletPeer> bullets = new CopyOnWriteArrayList<BulletPeer>();

	// Robot counters
	private int activeParticipants;
	private int activeSentries;

	// Death events
	private final List<RobotPeer> deathRobots = new CopyOnWriteArrayList<RobotPeer>();

	// Initial robot setups (if any)
	private RobotSetup[] initialRobotSetups;

	public Battle(ISettingsManager properties, IBattleManager battleManager, IHostManager hostManager, ICpuManager cpuManager, BattleEventDispatcher eventDispatcher) { // NO_UCD (unused code)
		super(
				properties, battleManager, eventDispatcher);
		this.hostManager = hostManager;
		this.cpuConstant = cpuManager.getCpuConstant();
	}

	void setup(RobotSpecification[] battlingRobotsList, BattleProperties battleProps, boolean paused) {
		isPaused = paused;
		battleRules = HiddenAccess.createRules(battleProps.getBattlefieldWidth(), battleProps.getBattlefieldHeight(),
				battleProps.getNumRounds(), battleProps.getGunCoolingRate(), battleProps.getInactivityTime(),
				battleProps.getHideEnemyNames(), battleProps.getSentryBorderSize());
		robotsCount = battlingRobotsList.length;
		computeInitialPositions(battleProps.getInitialPositions());
		createPeers(battlingRobotsList);
	}

	private void createPeers(RobotSpecification[] battlingRobotsList) {
		// create teams
		Map<String, Integer> countedNames = new HashMap<String, Integer>();
		List<String> teams = new ArrayList<String>();
		List<String> teamDuplicates = new ArrayList<String>();
		List<Integer> robotDuplicates = new ArrayList<Integer>();

		// count duplicate robots, enumerate teams, enumerate team members
		for (RobotSpecification specification : battlingRobotsList) {
			final String name = ((IRobotItem) HiddenAccess.getFileSpecification(specification)).getUniqueFullClassNameWithVersion();

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

		Map<String, List<String>> teamMembers = new HashMap<String, List<String>>();

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
			String name = ((IRobotItem) HiddenAccess.getFileSpecification(specification)).getUniqueFullClassNameWithVersion();
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
		Map<String, TeamPeer> namedTeams = new HashMap<String, TeamPeer>();

		// create robots
		for (int i = 0; i < battlingRobotsList.length; i++) {
			RobotSpecification specification = battlingRobotsList[i];
			TeamPeer team = null;

			String teamFullName = HiddenAccess.getRobotTeamName(specification);

			// The team index and robot index depends on current sizes of the contestant list and robot list
			int teamIndex = contestants.size();
			int robotIndex = robots.size();

			if (teamFullName != null) {
				if (!namedTeams.containsKey(teamFullName)) {
					String newTeamName = teamDuplicates.get(teams.indexOf(teamFullName));

					team = new TeamPeer(newTeamName, teamMembers.get(teamFullName), teamIndex);

					namedTeams.put(teamFullName, team);
					contestants.add(team);

				} else {
					team = namedTeams.get(teamFullName);
					if (team != null) {
						teamIndex = team.getTeamIndex();
					}
				}
			}
			Integer duplicate = robotDuplicates.get(i);
			RobotPeer robotPeer = new RobotPeer(this, hostManager, specification, duplicate, team, robotIndex);

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
		return RobocodeProperties.isDebuggingOn();
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
	 * Returns the number of active participants.
	 *
	 * @return the number of active participants.
	 */
	public int countActiveParticipants() {
		return activeParticipants;
	}

	/**
	 * Returns the number of active sentry robots.
	 *
	 * @return the number of active sentry robots.
	 */
	public int countActiveSentries() {
		return activeSentries;
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
			nanoWait = (int) (waitTime % 1000000);
		} else {
			millisWait = cpuConstant / 1000000;
			nanoWait = (int) (cpuConstant % 1000000);
		}
		if (nanoWait == 0) {
			nanoWait = 1;
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

		computeActiveRobots(); // Used for robotPeer.initializeRound()

		// At this point the unsafe loader thread will now set itself to wait for a notify

		for (RobotPeer robotPeer : robots) {
			robotPeer.initializeRound(robots, initialRobotSetups);
			robotPeer.println("=========================");
			robotPeer.println("Round " + (getRoundNum() + 1) + " of " + getNumRounds());
			robotPeer.println("=========================");
		}

		if (getRoundNum() == 0) {
			eventDispatcher.onBattleStarted(new BattleStartedEvent(battleRules, robots.size(), false));
			if (isPaused) {
				eventDispatcher.onBattlePaused(new BattlePausedEvent());
			}
		}

		computeActiveRobots(); // Used for RoundEnded check		hostManager.resetThreadManager();
	}

	@Override
	protected void initializeRound() {
		super.initializeRound();

		inactiveTurnCount = 0;

		// Start robots

		long waitMillis;
		int waitNanos;

		if (isDebugging()) {
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
		if (endTimer == 0) {
			if (isAborted()) {
				for (RobotPeer robotPeer : getRobotsAtRandom()) {
					if (robotPeer.isAlive()) {
						robotPeer.println("SYSTEM: game aborted.");
					}
				}
			} else if (oneTeamRemaining()) {
				boolean leaderFirsts = false;
				TeamPeer winningTeam = null;

				robocode.RoundEndedEvent roundEndedEvent = new robocode.RoundEndedEvent(getRoundNum(), currentTime,
						totalTurns); 

				for (RobotPeer robotPeer : getRobotsAtRandom()) {
					robotPeer.addEvent(roundEndedEvent);
					if (robotPeer.isAlive() && !robotPeer.isWinner() && !robotPeer.isSentryRobot()) {
						robotPeer.getRobotStatistics().scoreLastSurvivor();
						robotPeer.setWinner(true);
						robotPeer.println("SYSTEM: " + robotPeer.getNameForEvent(robotPeer) + " wins the round.");
						robotPeer.addEvent(new WinEvent());
						if (robotPeer.getTeamPeer() != null) {
							if (robotPeer.isTeamLeader()) {
								leaderFirsts = true;
							} else {
								winningTeam = robotPeer.getTeamPeer();
							}
						}
					}
					// Generate totals as round has ended, but first when the last scores has been calculated
					robotPeer.getRobotStatistics().generateTotals();
				}
				if (!leaderFirsts && winningTeam != null) {
					winningTeam.getTeamLeader().getRobotStatistics().scoreFirsts();
				}
			}
			if (isAborted() || isLastRound()) {
				List<RobotPeer> orderedRobots = new ArrayList<RobotPeer>(robots);
				Collections.sort(orderedRobots);
				Collections.reverse(orderedRobots);

				for (int rank = 0; rank < robots.size(); rank++) {
					RobotPeer robotPeer = orderedRobots.get(rank);
					robotPeer.getStatistics().setRank(rank + 1);
					BattleResults battleResults = robotPeer.getStatistics().getFinalResults();
					robotPeer.addEvent(new BattleEndedEvent(isAborted(), battleResults));
				}
			}
		}

		if (endTimer > 4 * TURNS_DISPLAYED_AFTER_ENDING) {
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
		for (int i = 0; i < contestants.size(); i++) {
			results.add(null);
		}
		for (int rank = 0; rank < contestants.size(); rank++) {
			ContestantPeer contestant = contestants.get(rank);
			contestant.getStatistics().setRank(rank + 1);
			BattleResults battleResults = contestant.getStatistics().getFinalResults();

			RobotSpecification robotSpec = null;
			if (contestant instanceof RobotPeer) {
				robotSpec = ((RobotPeer) contestant).getRobotSpecification();
			} else if (contestant instanceof TeamPeer) {
				robotSpec = ((TeamPeer) contestant).getTeamLeader().getRobotSpecification();
			}
			results.set(rank, new RobotResults(robotSpec, battleResults));
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
					if (tm.getTeamPeer() == deadRobot.getTeamPeer() && tm.isAlive()) {
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
				if (robotPeer.isAlive()) {
					robotPeer.addEvent(new RobotDeathEvent(robotPeer.getNameForEvent(deadRobot)));

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
		int countActiveParticipants = 0;
		int countActiveSentries = 0;

		for (RobotPeer robot : robots) {
			if (robot.isAlive()) { // robot must be alive in order to be active
				if (robot.isSentryRobot()) {
					countActiveSentries++;
				} else {
					countActiveParticipants++;
				}
			}
		}
		this.activeParticipants = countActiveParticipants;
		this.activeSentries = countActiveSentries;
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
				// This call blocks until the robot's thread actually wakes up.
				robotPeer.waitWakeup();

				if (robotPeer.isAlive()) {
					if (isDebugging() || robotPeer.isPaintEnabled()) {
						robotPeer.waitSleeping(DEBUG_TURN_WAIT_MILLIS, 1);
					} else if (currentTime == 1) {
						robotPeer.waitSleeping(millisWait * 10, 1);
					} else {
						robotPeer.waitSleeping(millisWait, nanoWait);
					}
				}
			}
		}
	}

	private void wakeupParallel(List<RobotPeer> robotsAtRandom) {
		for (RobotPeer robotPeer : robotsAtRandom) {
			if (robotPeer.isRunning()) {
				// This call blocks until the robot's thread actually wakes up.
				robotPeer.waitWakeup();
			}
		}
		for (RobotPeer robotPeer : robotsAtRandom) {
			if (robotPeer.isRunning() && robotPeer.isAlive()) {
				if (isDebugging() || robotPeer.isPaintEnabled()) {
					robotPeer.waitSleeping(DEBUG_TURN_WAIT_MILLIS, 1);
				} else if (currentTime == 1) {
					robotPeer.waitSleeping(millisWait * 10, 1);
				} else {
					robotPeer.waitSleeping(millisWait, nanoWait);
				}
			}
		}
	}

	private int getActiveContestantCount(RobotPeer peer) {
		int count = 0;

		for (ContestantPeer c : contestants) {
			if (c instanceof RobotPeer) {
				RobotPeer robot = (RobotPeer) c;
				if (!robot.isSentryRobot() && robot.isAlive()) {
					count++;
				}
			} else if (c instanceof TeamPeer && c != peer.getTeamPeer()) {
				for (RobotPeer robot: (TeamPeer) c) {
					if (!robot.isSentryRobot() && robot.isAlive()) {
						count++;
						break;
					}
				}
			}
		}
		return count;
	}

	private void computeInitialPositions(String initialPositions) {
		initialRobotSetups = null;

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

		initialRobotSetups = new RobotSetup[positions.size()];

		String[] coords;
		double x, y, heading;

		for (int i = 0; i < positions.size(); i++) {
			coords = positions.get(i).split(",");

			Random random = RandomFactory.getRandom();

			x = RobotPeer.WIDTH + random.nextDouble() * (battleRules.getBattlefieldWidth() - 2 * RobotPeer.WIDTH);
			y = RobotPeer.HEIGHT + random.nextDouble() * (battleRules.getBattlefieldHeight() - 2 * RobotPeer.HEIGHT);
			heading = 2 * Math.PI * random.nextDouble();

			int len = coords.length;

			if (len >= 1 && coords[0].trim().length() > 0) {
				try {
					x = Double.parseDouble(coords[0].replaceAll("[^0-9.]", ""));
				} catch (NumberFormatException ignore) {// Could be the '?', which is fine
				}
				if (len >= 2 && coords[1].trim().length() > 0) {
					try {
						y = Double.parseDouble(coords[1].replaceAll("[^0-9.]", ""));
					} catch (NumberFormatException ignore) {// Could be the '?', which is fine
					}
					if (len >= 3 && coords[2].trim().length() > 0) {
						try {
							heading = Math.toRadians(Double.parseDouble(coords[2].replaceAll("[^0-9.]", "")));
						} catch (NumberFormatException ignore) {// Could be the '?', which is fine
						}
					}
				}
			}
			initialRobotSetups[i] = new RobotSetup(x, y, heading);
		}
	}

	private boolean oneTeamRemaining() {
		if (countActiveParticipants() <= 1) {
			return true;
		}

		boolean found = false;
		TeamPeer currentTeam = null;

		for (RobotPeer currentRobot : robots) {
			if (currentRobot.isAlive()) {
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

	void killRobot(int robotIndex) {
		sendCommand(new KillRobotCommand(robotIndex));
	}

	public void setPaintEnabled(int robotIndex, boolean enable) {
		sendCommand(new EnableRobotPaintCommand(robotIndex, enable));
	}

	void setSGPaintEnabled(int robotIndex, boolean enable) {
		sendCommand(new EnableRobotSGPaintCommand(robotIndex, enable));
	}

	void sendInteractiveEvent(Event e) {
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
