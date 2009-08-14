package CTF;

import net.sf.robocode.battle.peer.ContestantStatistics;
import net.sf.robocode.battle.IContestantStatistics;
import net.sf.robocode.battle.peer.RobotPeer;

public class CaptureTheFlagRobotStatistics extends ContestantStatistics{

	private CaptureTheFlagTeamStatistics parentStats = null;
	
	private static int captureScore = 0;
	private static int killBonus = 1;
	private static int deathPenalty = 2;
	
	public CaptureTheFlagRobotStatistics(RobotPeer robotPeer, int robots) {
		super(robotPeer, robots);
		scoreNames.add("Captures");
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
	public CaptureTheFlagRobotStatistics() {}

	
	public IContestantStatistics fakeConstructor(RobotPeer peer, int robots) {
		return new CaptureTheFlagRobotStatistics(peer, robots);
	}

	public void scoreBulletDamage(String robot, double damage) {
		//do nothing
	}

	public double scoreBulletKill(String robot) {
		if (isActive) {

			currentScores.set(killBonus, currentScores.get(killBonus) + 50);
			
			if (parentStats != null)
			{
				parentStats.addBulletKill(50);
			}
			
			return 50;
		}
		return 0;
	}

	public void scoreFirsts() {
		if (isActive) {
			totalFirsts++;
			parentStats.addRank(1);
		}
		// do nothing
	}

	public void scoreLastSurvivor() {
		if (isActive) {
			if (robotPeer.getTeamPeer() == null || robotPeer.isTeamLeader()) {
				totalFirsts++;
				parentStats.addRank(1);
			}
		}
	}

	public void scoreRammingDamage(String robot) {
		//do nothing
	}

	public double scoreRammingKill(String robot) {
		if (isActive) {

			currentScores.set(killBonus, currentScores.get(killBonus) + 50);

			if (parentStats != null)
			{
				parentStats.addRammingKill(50);
			}
			
			return 50;
		}
		return 0;
	}

	public void scoreRobotDeath(int enemiesRemaining) {
		switch (enemiesRemaining) {
		case 0:
			if (!robotPeer.isWinner()) {
				totalFirsts++;
				parentStats.addRank(1);
			}
			break;

		case 1:
			totalSeconds++;
			parentStats.addRank(2);
			break;

		case 2:
			totalThirds++;
			parentStats.addRank(3);
			break;
		}
		currentScores.set(deathPenalty, currentScores.get(deathPenalty) - 50);
		
		if (parentStats != null)
		{
			parentStats.addDeathPenalty(-50);
		}
	}

	public void scoreSurvival() {
		// do nothing
	}
	
	public void scoreCapture() {
		currentScores.set(captureScore, currentScores.get(captureScore) + 250);
		
		if (parentStats != null)
		{
			parentStats.addCapture(250);
		}
	}
	
	public void setParentStats(CaptureTheFlagTeamStatistics parent)
	{
		parentStats = parent;
	}

	@Override
	protected String getTeamName() {
		return robotPeer.getName();
	}
}
