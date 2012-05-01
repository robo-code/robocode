/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Joshua Galecki
 *     - Initial API and implementation
 *******************************************************************************/

package net.sf.robocode.battle.peer;


import net.sf.robocode.battle.IContestantStatistics;


/**
 * The classic version of robot statistics
 * 
 * @author Joshua Galecki (orignal)
 */
public class ClassicRobotStatistics extends ContestantStatistics {

	private static int survivalScore = 0;
	private static int lastSurvivorBonus = 1;
	private static int bulletDamageScore = 2;
	private static int bulletKillBonus = 3;
	private static int rammingDamageScore = 4;
	private static int rammingKillBonus = 5;
	
	/**
	 * Actual constructor
	 * @param robotPeer the robot these statistics track
	 * @param robots number of robots on the battlefield
	 */
	public ClassicRobotStatistics(RobotPeer robotPeer, int robots) {
		super(robotPeer, robots);
		scoreNames.add("Survival");
		currentScores.add(0.0);
		totalScores.add(0.0);
		scoreNames.add("Last Survivor Bonus");
		currentScores.add(0.0);
		totalScores.add(0.0);
		scoreNames.add("Bullet Damage");
		currentScores.add(0.0);
		totalScores.add(0.0);
		scoreNames.add("Bullet Kill Bonus");
		currentScores.add(0.0);
		totalScores.add(0.0);
		scoreNames.add("Ramming Damage");
		currentScores.add(0.0);
		totalScores.add(0.0);
		scoreNames.add("Ramming Kill Bonus");
		currentScores.add(0.0);
		totalScores.add(0.0);
	}
	
	/**
	 * Junk constructor
	 */
	public ClassicRobotStatistics() {}

	public IContestantStatistics fakeConstructor(RobotPeer peer, int robots) {
		return new ClassicRobotStatistics(peer, robots);
	}

	public void scoreSurvival() {
		if (isActive) {
			currentScores.set(survivalScore, currentScores.get(survivalScore) + 50);
		}
	}

	public void scoreLastSurvivor() {
		if (isActive) {
			int enemyCount = robots - 1;

			if (robotPeer.getTeamPeer() != null) {
				enemyCount -= (robotPeer.getTeamPeer().size() - 1);
			}
			currentScores.set(lastSurvivorBonus, currentScores.get(lastSurvivorBonus) + 10 * enemyCount);

			if (robotPeer.getTeamPeer() == null || robotPeer.isTeamLeader()) {
				totalFirsts++;
			}
		}
	}

	public void scoreBulletDamage(String robot, double damage) {
		if (isActive) {
			incrementRobotDamage(robot, damage);
			currentScores.set(bulletDamageScore, currentScores.get(bulletDamageScore) + damage);
		}
	}

	public double scoreBulletKill(String robot) {
		if (isActive) {
			double bonus = 0;

			if (robotPeer.getTeamPeer() == null) {
				bonus = getRobotDamage(robot) * 0.20;
			} else {
				for (RobotPeer teammate : robotPeer.getTeamPeer()) {
					bonus += ((ContestantStatistics) teammate.getRobotStatistics()).getRobotDamage(robot) * 0.20;
				}
			}

			currentScores.set(bulletKillBonus, currentScores.get(bulletKillBonus) + bonus);
			return bonus;
		}
		return 0;
	}

	public void scoreRammingDamage(String robot) {
		if (isActive) {
			incrementRobotDamage(robot, robocode.Rules.ROBOT_HIT_DAMAGE);
			currentScores.set(rammingDamageScore, currentScores.get(rammingDamageScore) + robocode.Rules.ROBOT_HIT_BONUS);
		}
	}

	public double scoreRammingKill(String robot) {
		if (isActive) {
			double bonus = 0;

			if (robotPeer.getTeamPeer() == null) {
				bonus = getRobotDamage(robot) * 0.30;
			} else {
				for (RobotPeer teammate : robotPeer.getTeamPeer()) {
					bonus += ((ContestantStatistics) teammate.getRobotStatistics()).getRobotDamage(robot) * 0.30;
				}
			}
			currentScores.set(rammingKillBonus, currentScores.get(rammingKillBonus) + bonus);
			return bonus;
		}
		return 0;
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

	@Override
	protected String getTeamName() {
		return robotPeer.getTeamName();
	}
}
