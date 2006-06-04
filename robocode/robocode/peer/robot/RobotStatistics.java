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
 *******************************************************************************/
package robocode.peer.robot;


import robocode.peer.RobotPeer;
import robocode.peer.TeamPeer;


/**
 * @author Mathew A. Nelson (original)
 */
public class RobotStatistics implements robocode.peer.ContestantStatistics {
	boolean noScoring;
	private double bulletDamageScore;
	private double rammingDamageScore;
	private double survivalScore;
	private double winnerScore;
	private double totalWinnerScore;
	private double killedEnemyRammingScore;
	private double killedEnemyBulletScore;
	private double totalScore;
	private double totalBulletDamageScore;
	private double totalBulletDamageDealt;
	private double totalBulletDamageReceived;
	private double totalRammingDamageScore;
	private double totalSurvivalScore;
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

	/**
	 * RobotStatistics constructor comment.
	 */
	public RobotStatistics(RobotPeer robotPeer) {
		super();
		this.robotPeer = robotPeer;
		this.teamPeer = robotPeer.getTeamPeer();
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

	private double[] getRobotDamage() {
		if (robotDamage == null) {
			robotDamage = new double[robotPeer.getBattle().getRobots().size()];
			for (int i = 0; i < robotPeer.getBattle().getRobots().size(); i++) {
				robotDamage[i] = 0.0;
			}
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
		if (teamPeer != null
				&& teamPeer == ((RobotPeer) robotPeer.getBattle().getRobots().elementAt(robot)).getTeamPeer()) {
			return true;
		} else {
			return false;
		}
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
		if (enemiesRemaining == 0) {
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
				for (int i = 0; i < teamPeer.size(); i++) {
					bonus += teamPeer.elementAt(i).getRobotStatistics().getRobotDamage()[robot] * .2;
				}
			}
				
			robotPeer.out.println(
					"SYSTEM: Bonus for killing " + ((RobotPeer) robotPeer.getBattle().getRobots().elementAt(robot)).getName()
					+ ": " + (int) (bonus + .5));
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
				for (int i = 0; i < teamPeer.size(); i++) {
					bonus += teamPeer.elementAt(i).getRobotStatistics().getRobotDamage()[robot] * .3;
				}
			}
			robotPeer.out.println(
					"SYSTEM: Ram bonus for killing "
							+ ((RobotPeer) robotPeer.getBattle().getRobots().elementAt(robot)).getName() + ": " + (int) (bonus + .5));
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
}
