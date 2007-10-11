/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Luis Crespo
 *     - Added getCurrentScore()
 *     Flemming N. Larsen
 *     - Bugfix: scoreDeath() incremented totalFirsts even if the robot was
 *       already a winner, where scoreWinner() has already been called previously
 *     - Added constructor that takes an additonal RobotResults that must be
 *       copied into this object and added the getResults() in order to support
 *       the replay feature
 *     - Changed the survivalScore and totalSurvivalScore fields to be integers
 *     - Renamed method names and removed unused methods
 *     - Ordered all methods more naturally
 *     - Added methods for getting current scores
 *     - Optimizations
 *     - Removed damage parameter from the scoreRammingDamage() method, as the
 *       damage is constant and defined by Rules.ROBOT_HIT_DAMAGE and the score
 *       of hitting a robot is defined by Rules.ROBOT_HIT_BONUS
 *     Titus Chen
 *     - Bugfix: Initial getResults() method only factored in the most recent
 *       round
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Nathaniel Troutman
 *     - Added cleanup() method for cleaning up references to internal classes
 *       to prevent circular references causing memory leaks
 *******************************************************************************/
package robocode.peer.robot;


import java.util.List;

import robocode.control.RobotResults;
import robocode.peer.RobotPeer;
import robocode.peer.TeamPeer;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Titus Chen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 */
public class RobotStatistics implements robocode.peer.ContestantStatistics {

	private RobotPeer robotPeer;
	private TeamPeer teamPeer;

	private List<RobotPeer> robots;

	private boolean isActive;

	private double survivalScore;
	private double lastSurvivorBonus;
	private double bulletDamageScore;
	private double bulletKillBonus;
	private double rammingDamageScore;
	private double rammingKillBonus;

	private double robotDamage[];

	private double totalScore;
	private double totalSurvivalScore;
	private double totalLastSurvivorBonus;
	private double totalBulletDamageScore;
	private double totalBulletKillBonus;
	private double totalRammingDamageScore;
	private double totalRammingKillBonus;

	private int totalFirsts;
	private int totalSeconds;
	private int totalThirds;

	public RobotStatistics(RobotPeer robotPeer) {
		super();
		this.robotPeer = robotPeer;
		this.teamPeer = robotPeer.getTeamPeer();
	}

	public RobotStatistics(RobotPeer robotPeer, RobotResults results) {
		this(robotPeer);

		totalScore = results.getScore();
		totalSurvivalScore = results.getSurvival();
		totalLastSurvivorBonus = results.getLastSurvivorBonus();
		totalBulletDamageScore = results.getBulletDamage();
		totalBulletKillBonus = results.getBulletDamageBonus();
		totalRammingDamageScore = results.getRamDamage();
		totalRammingKillBonus = results.getRamDamageBonus();
		totalFirsts = results.getFirsts();
		totalSeconds = results.getSeconds();
		totalThirds = results.getThirds();
	}

	public void initialize() {
		robots = robotPeer.getBattle().getRobots();

		resetScores();

		robotDamage = null;

		isActive = true;
	}

	private void resetScores() {
		survivalScore = 0;
		lastSurvivorBonus = 0;
		bulletDamageScore = 0;
		bulletKillBonus = 0;
		rammingDamageScore = 0;
		rammingKillBonus = 0;
	}

	public void generateTotals() {
		totalSurvivalScore += survivalScore;
		totalLastSurvivorBonus += lastSurvivorBonus;
		totalBulletDamageScore += bulletDamageScore;
		totalBulletKillBonus += bulletKillBonus;
		totalRammingDamageScore += rammingDamageScore;
		totalRammingKillBonus += rammingKillBonus;

		totalScore = totalBulletDamageScore + totalRammingDamageScore + totalSurvivalScore + totalRammingKillBonus
				+ totalBulletKillBonus + totalLastSurvivorBonus;
	}

	public double getTotalScore() {
		return totalScore;
	}

	public double getTotalSurvivalScore() {
		return totalSurvivalScore;
	}

	public double getTotalLastSurvivorBonus() {
		return totalLastSurvivorBonus;
	}

	public double getTotalBulletDamageScore() {
		return totalBulletDamageScore;
	}

	public double getTotalBulletKillBonus() {
		return totalBulletKillBonus;
	}

	public double getTotalRammingDamageScore() {
		return totalRammingDamageScore;
	}

	public double getTotalRammingKillBonus() {
		return totalRammingKillBonus;
	}

	public int getTotalFirsts() {
		return totalFirsts;
	}

	public int getTotalSeconds() {
		return totalSeconds;
	}

	public int getTotalThirds() {
		return totalThirds;
	}

	public double getCurrentScore() {
		return bulletDamageScore + rammingDamageScore + survivalScore + rammingKillBonus + bulletKillBonus
				+ lastSurvivorBonus;
	}

	public double getCurrentSurvivalScore() {
		return survivalScore;
	}

	public double getCurrentBulletDamageScore() {
		return bulletDamageScore;
	}

	public double getCurrentBulletKillBonus() {
		return bulletKillBonus;
	}

	public double getCurrentRammingDamageScore() {
		return rammingDamageScore;
	}

	public double getCurrentRammingKillBonus() {
		return rammingKillBonus;
	}

	public void scoreSurvival() {
		if (isActive) {
			survivalScore += 50;
		}
	}

	public void scoreLastSurvivor() {
		if (isActive) {
			int enemyCount = robots.size() - 1;

			if (teamPeer != null) {
				enemyCount -= (teamPeer.size() - 1);
			}
			lastSurvivorBonus += 10 * enemyCount;

			if (teamPeer == null || robotPeer.isTeamLeader()) {
				totalFirsts++;
			}
		}
	}

	public void scoreBulletDamage(int robot, double damage) {
		if (isTeammate(robot)) {
			return;
		}
		if (isActive) {
			getRobotDamage()[robot] += damage;
			bulletDamageScore += damage;
		}
	}

	public void scoreBulletKill(int robot) {
		if (isTeammate(robot)) {
			return;
		}

		if (isActive) {
			double bonus = 0;

			if (teamPeer == null) {
				bonus = getRobotDamage()[robot] * .2;
			} else {
				for (RobotPeer teammate : teamPeer) {
					bonus += teammate.getRobotStatistics().getRobotDamage()[robot] * .2;
				}
			}

			bulletKillBonus += bonus;

			robotPeer.out.println(
					"SYSTEM: Bonus for killing " + (robots.get(robot)).getName() + ": " + (int) (bonus + .5));
		}
	}

	public void scoreRammingDamage(int robot) {
		if (isActive && !isTeammate(robot)) {
			getRobotDamage()[robot] += robocode.Rules.ROBOT_HIT_DAMAGE;
			rammingDamageScore += robocode.Rules.ROBOT_HIT_BONUS;
		}
	}

	public void scoreRammingKill(int robot) {
		if (isActive && !isTeammate(robot)) {
			double bonus = 0;

			if (teamPeer == null) {
				bonus = getRobotDamage()[robot] * .3;
			} else {
				for (RobotPeer teammate : teamPeer) {
					bonus += teammate.getRobotStatistics().getRobotDamage()[robot] * .3;
				}
			}
			rammingKillBonus += bonus;

			robotPeer.out.println(
					"SYSTEM: Ram bonus for killing " + (robots.get(robot)).getName() + ": " + (int) (bonus + .5));
		}
	}

	public void scoreRobotDeath(int enemiesRemaining) {
		switch (enemiesRemaining) {
		case 0:
			if (!robotPeer.isWinner()) {
				totalFirsts++;
			}
			break;

		case 1:
			totalSeconds++;
			break;

		case 2:
			totalThirds++;
			break;
		}
	}

	public void scoreFirsts() {
		if (isActive) {
			totalFirsts++;
		}
	}

	public void setInactive() {
		resetScores();
		isActive = false;
	}

	public RobotResults getResults(int rank) {
		if (robotPeer.getBattle().isRunning()) {
			return new RobotResults(null, rank, totalScore + getCurrentScore(), totalSurvivalScore + survivalScore,
					totalLastSurvivorBonus, totalBulletDamageScore + bulletDamageScore, totalBulletKillBonus + bulletKillBonus,
					totalRammingDamageScore + rammingDamageScore, totalRammingKillBonus + rammingKillBonus, totalFirsts,
					totalSeconds, totalThirds);
		}
		return new RobotResults(null, rank, totalScore, totalSurvivalScore, totalLastSurvivorBonus,
				totalBulletDamageScore, totalBulletKillBonus, totalRammingDamageScore, totalRammingKillBonus, totalFirsts,
				totalSeconds, totalThirds);
	}

	private double[] getRobotDamage() {
		if (robotDamage == null) {
			robotDamage = new double[robots.size()];
		}
		return robotDamage;
	}

	private boolean isTeammate(int robot) {
		return (teamPeer != null && teamPeer == robots.get(robot).getTeamPeer());
	}

	public void cleanup() {// Do nothing, for now
	}
}
