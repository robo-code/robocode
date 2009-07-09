package net.sf.robocode.battle;

import net.sf.robocode.battle.peer.ContestantStatistics;
import net.sf.robocode.battle.peer.RobotPeer;

public class CaptureTheFlagStatistics extends ContestantStatistics{

	private static int captureScore = 0;
	private static int bulletDamageScore = 1;
	private static int rammingDamageScore = 2;
	private static int killBonus = 3;
	private static int deathPenalty = 4;
	
	public CaptureTheFlagStatistics(RobotPeer robotPeer, int robots) {
		super(robotPeer, robots);
		scoreNames.add("Captures");
		currentScores.add(0.0);
		totalScores.add(0.0);
		scoreNames.add("Bullet Damage");
		currentScores.add(0.0);
		totalScores.add(0.0);
		scoreNames.add("Ramming Damage");
		currentScores.add(0.0);
		totalScores.add(0.0);
		scoreNames.add("Kills");
		currentScores.add(0.0);
		totalScores.add(0.0);
		scoreNames.add("Deaths");
		currentScores.add(0.0);
		totalScores.add(0.0);
	}
	
	/**
	 * Junk constructor
	 */
	public CaptureTheFlagStatistics() {}

	
	public IContestantStatistics fakeConstructor(RobotPeer peer, int robots) {
		return new CaptureTheFlagStatistics(peer, robots);
	}

	public void scoreBulletDamage(String robot, double damage) {
		if (isActive) {
			incrementRobotDamage(robot, damage / 5);
			currentScores.set(bulletDamageScore, currentScores.get(bulletDamageScore) + damage);
		}
	}

	public double scoreBulletKill(String robot) {
		if (isActive) {
//			double bonus = 0;
//
//			if (robotPeer.getTeamPeer() == null) {
//				bonus = getRobotDamage()[robot] * .2;
//			} else {
//				for (RobotPeer teammate : robotPeer.getTeamPeer()) {
//					bonus += teammate.getRobotStatistics().getRobotDamage()[robot] * .2;
//				}
//			}

			currentScores.set(killBonus, currentScores.get(killBonus) + 50);
			return 50;
		}
		return 0;
	}

	public void scoreFirsts() {
		// do nothing
	}

	public void scoreLastSurvivor() {
		// do nothing
	}

	public void scoreRammingDamage(String robot) {
		if (isActive) {
			incrementRobotDamage(robot, 1);
			currentScores.set(rammingDamageScore, currentScores.get(rammingDamageScore) + 1);
		}
	}

	public double scoreRammingKill(String robot) {
		if (isActive) {
			currentScores.set(killBonus, currentScores.get(killBonus) + 50);
			return 50;
		}
		return 0;
	}

	public void scoreRobotDeath(int enemiesRemaining) {
		currentScores.set(deathPenalty, currentScores.get(killBonus) - 50);
	}

	public void scoreSurvival() {
		// do nothing
	}
	
	public void scoreCapture() {
		currentScores.set(captureScore, currentScores.get(captureScore) + 250);
	}

}
