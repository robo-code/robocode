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
 *     Titus Chen
 *     - Bugfix: Initial getResults() method only factored in the most recent
 *       round
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.peer.robot;


import robocode.control.RobotResults;

import robocode.peer.RobotPeer;
import robocode.peer.TeamPeer;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Luis Crespo (contributor)
 * @author Titus Chen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobotStatistics implements robocode.peer.ContestantStatistics {
	private boolean noScoring;
	private double bulletDamageScore;
	private double rammingDamageScore;
	private int survivalScore;
	private double winnerScore;
	private double totalWinnerScore;
	private double killedEnemyRammingScore;
	private double killedEnemyBulletScore;
	private double totalScore;
	private double totalBulletDamageScore;
	private double totalBulletDamageDealt;
	private double totalBulletDamageReceived;
	private double totalRammingDamageScore;
	private int totalSurvivalScore;
	private double totalKilledEnemyRammingScore;
	private double totalKilledEnemyBulletScore;
	private RobotPeer robotPeer;
	private TeamPeer teamPeer;
	private double rammingDamageDealt;
	private double totalRammingDamageDealt;
	private double bulletDamageDealt;
	private double bulletDamageReceived;
	private double rammingDamageReceived;
	private double totalRammingDamageReceived;

	private int totalFirsts;
	private int totalSeconds;
	private int totalThirds;

	private double robotDamage[] = null;

	public RobotStatistics(RobotPeer robotPeer) {
		super();
		this.robotPeer = robotPeer;
		this.teamPeer = robotPeer.getTeamPeer();
	}

	public RobotStatistics(RobotPeer robotPeer, RobotResults results) {
		this(robotPeer);

		totalScore = results.getScore();
		totalSurvivalScore = results.getSurvival();
		totalWinnerScore = results.getLastSurvivorBonus();
		totalBulletDamageScore = results.getBulletDamage();
		totalKilledEnemyBulletScore = results.getBulletDamageBonus();
		totalRammingDamageScore = results.getRamDamage();
		totalKilledEnemyRammingScore = results.getRamDamageBonus();
		totalFirsts = results.getFirsts();
		totalSeconds = results.getSeconds();
		totalThirds = results.getThirds();
	}

	public void damagedByBullet(double damage) {
		bulletDamageReceived += damage;
	}

	public void damagedByRamming(double damage) {
		rammingDamageReceived += damage;
	}

	public void generateTotals() {
		totalBulletDamageScore += bulletDamageScore;
		totalRammingDamageScore += rammingDamageScore;
		totalSurvivalScore += survivalScore;
		totalKilledEnemyBulletScore += killedEnemyBulletScore;
		totalKilledEnemyRammingScore += killedEnemyRammingScore;
		totalWinnerScore += winnerScore;
		totalBulletDamageDealt += bulletDamageDealt;
		totalBulletDamageReceived += bulletDamageReceived;
		totalRammingDamageDealt += rammingDamageDealt;
		totalRammingDamageReceived += rammingDamageReceived;
		totalScore = totalBulletDamageScore + totalRammingDamageScore + totalSurvivalScore
				+ totalKilledEnemyRammingScore + totalKilledEnemyBulletScore + totalWinnerScore;
	}

	public RobotResults getResults(int rank) {
		if (robotPeer.getBattle().isRunning()) {
			return new RobotResults(null, rank, totalScore + getCurrentScore(), totalSurvivalScore + survivalScore,
					totalWinnerScore + winnerScore, totalBulletDamageScore + bulletDamageScore,
					totalKilledEnemyBulletScore + killedEnemyBulletScore, totalRammingDamageScore + rammingDamageScore,
					totalKilledEnemyRammingScore + killedEnemyRammingScore, totalFirsts, totalSeconds, totalThirds);
		}
		return new RobotResults(null, rank, totalScore, totalSurvivalScore, totalWinnerScore, totalBulletDamageScore,
				totalKilledEnemyBulletScore, totalRammingDamageScore, totalKilledEnemyRammingScore, totalFirsts, totalSeconds,
				totalThirds);
	}
	
	private double[] getRobotDamage() {
		if (robotDamage == null) {
			robotDamage = new double[robotPeer.getBattle().getRobots().size()];
		}
		return robotDamage;
	}

	public double getTotalBulletDamageDealt() {
		return totalBulletDamageDealt;
	}

	public double getTotalBulletDamageReceived() {
		return totalBulletDamageReceived;
	}

	public double getTotalBulletDamageScore() {
		return totalBulletDamageScore;
	}

	public int getTotalFirsts() {
		return totalFirsts;
	}

	public double getTotalKilledEnemyBulletScore() {
		return totalKilledEnemyBulletScore;
	}

	public double getTotalKilledEnemyRammingScore() {
		return totalKilledEnemyRammingScore;
	}

	public double getTotalRammingDamageDealt() {
		return totalRammingDamageDealt;
	}

	public double getTotalRammingDamageReceived() {
		return totalRammingDamageReceived;
	}

	public double getTotalRammingDamageScore() {
		return totalRammingDamageScore;
	}

	public double getTotalScore() {
		return totalScore;
	}

	public int getTotalSeconds() {
		return totalSeconds;
	}

	public double getTotalSurvivalScore() {
		return totalSurvivalScore;
	}

	public int getTotalThirds() {
		return totalThirds;
	}

	public double getTotalWinnerScore() {
		return totalWinnerScore;
	}

	public void initializeRound() {
		bulletDamageScore = 0;
		rammingDamageScore = 0;
		killedEnemyRammingScore = 0;
		killedEnemyBulletScore = 0;
		survivalScore = 0;
		winnerScore = 0;
		bulletDamageDealt = 0;
		bulletDamageReceived = 0;
		rammingDamageDealt = 0;
		rammingDamageReceived = 0;
	
		noScoring = false;

		robotDamage = null;
	}

	private boolean isTeammate(int robot) {
		return (teamPeer != null && teamPeer == ((RobotPeer) robotPeer.getBattle().getRobots().get(robot)).getTeamPeer());
	}

	public void scoreBulletDamage(int robot, double damage) {
		if (isTeammate(robot)) {
			return;
		}
		if (!noScoring) {
			getRobotDamage()[robot] += damage;
			bulletDamageScore += damage;
		}
		bulletDamageDealt += damage;
	}

	public void scoreDeath(int enemiesRemaining) {
		if (enemiesRemaining == 0 && !robotPeer.isWinner()) {
			totalFirsts++;
		}
		if (enemiesRemaining == 1) {
			totalSeconds++;
		} else if (enemiesRemaining == 2) {
			totalThirds++;
		}
	}

	public void scoreKilledEnemyBullet(int robot) {
		if (isTeammate(robot)) {
			return;
		}

		if (!noScoring) {
			double bonus = 0;

			if (teamPeer == null) {
				bonus = getRobotDamage()[robot] * .2;
			} else {
				for (RobotPeer teammate : teamPeer) {
					bonus += teammate.getRobotStatistics().getRobotDamage()[robot] * .2;
				}
			}
				
			robotPeer.out.println(
					"SYSTEM: Bonus for killing " + ((RobotPeer) robotPeer.getBattle().getRobots().get(robot)).getName() + ": "
					+ (int) (bonus + .5));
			killedEnemyBulletScore += bonus;
		}
	}

	public void scoreKilledEnemyRamming(int robot) {
		if (isTeammate(robot)) {
			return;
		}
		if (!noScoring) {
			double bonus = 0;

			if (teamPeer == null) {
				bonus = getRobotDamage()[robot] * .3;
			} else {
				for (RobotPeer teammate : teamPeer) {
					bonus += teammate.getRobotStatistics().getRobotDamage()[robot] * .3;
				}
			}
			robotPeer.out.println(
					"SYSTEM: Ram bonus for killing " + ((RobotPeer) robotPeer.getBattle().getRobots().get(robot)).getName()
					+ ": " + (int) (bonus + .5));
			killedEnemyRammingScore += bonus;
		}
	}

	public void scoreRammingDamage(int robot, double damage) {
		if (isTeammate(robot)) {
			return;
		}
		if (!noScoring) {
			getRobotDamage()[robot] += damage;
			rammingDamageScore += 2.0 * damage;
		}
		rammingDamageDealt += damage;
	}

	public void scoreSurvival() {
		if (!noScoring) {
			survivalScore += 50;
		}
	}

	public void scoreWinner() {
		if (!noScoring) {
			int enemyCount = robotPeer.getBattle().getRobots().size() - 1;

			if (teamPeer != null) {
				enemyCount -= (teamPeer.size() - 1);
			}
			winnerScore += 10 * enemyCount;
			if (teamPeer != null && !robotPeer.isTeamLeader()) {
				return;
			}
			totalFirsts++;
		}
	}

	public void scoreFirsts() {
		if (!noScoring) {
			totalFirsts++;
		}
	}

	public void setNoScoring(boolean noScoring) {
		this.noScoring = noScoring;
		if (noScoring) {
			bulletDamageScore = 0;
			rammingDamageScore = 0;
			killedEnemyRammingScore = 0;
			killedEnemyBulletScore = 0;
			survivalScore = 0;
			winnerScore = 0;
			bulletDamageDealt = 0;
			bulletDamageReceived = 0;
			rammingDamageDealt = 0;
			rammingDamageReceived = 0;
		}
	}

	public double getCurrentScore() {
		return bulletDamageScore + rammingDamageScore + survivalScore + killedEnemyRammingScore + killedEnemyBulletScore
				+ winnerScore;
	}
}
