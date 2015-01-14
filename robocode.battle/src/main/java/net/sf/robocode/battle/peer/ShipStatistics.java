package net.sf.robocode.battle.peer;


import robocode.BattleResults;
import robocode.NavalBattleResults;

/**
 * Statistics class for the Ships. 
 * Basically the same as RobotStatistics with extra variables
 * that decide the score based on mine damage, as well.
 * @author Thales B.V. / Thomas Hakkers
 *
 */
public class ShipStatistics extends RobotStatistics{
	private double totalMineDamageScore;
	private double totalMineKillBonus;
	private double mineDamageScore;
	private double mineKillBonus;
	
	ShipStatistics(RobotPeer robotPeer, int numberOfRobots) {
		super(robotPeer, numberOfRobots);
	}
	
	/**
	 * Sets all the scores back to 0
	 */
	protected void resetScores() {
		super.resetScores();
		mineDamageScore = 0;
		mineKillBonus = 0;
	}
	
	/**
	 * Generates the total score by adding up all total scores per catagory.
	 */
	public void generateTotals() {
		super.generateTotals();
		totalMineDamageScore += mineDamageScore;
		totalMineKillBonus += mineKillBonus;
		//NavalRobocode doesn't use sentries anyway.
		totalScore += totalMineDamageScore + totalMineKillBonus;
	}
	
	/**
	 * Returns the score the ship currently has.
	 */
	public double getCurrentScore() {
		return super.getCurrentScore() + mineDamageScore + mineKillBonus;
	}
	
	/**
	 * Called by {@link MinePeer} to score a kill with a mine.
	 * @param robot The ship that got hit by the mine
	 * @return The bonus the owner of this object will receive.
	 */
	double scoreMineKill(String robot) {
		if (isActive()) {
			double bonus = getRobotDamage(robot) * 0.20;
			mineKillBonus += bonus;
			return bonus;
		}
		return 0;
	}
	
	public BattleResults getFinalResults() {
		return new NavalBattleResults(getRobotPeer().getTeamName(), getRank(), totalScore, getTotalSurvivalScore(), getTotalLastSurvivorBonus(),
				getTotalBulletDamageScore(), getTotalBulletKillBonus(), getTotalRammingDamageScore(), getTotalRammingKillBonus(),
				totalMineDamageScore, totalMineKillBonus,
				getTotalFirsts(),getTotalSeconds(), getTotalThirds());
	}

	/**
	 * Called by {@link MinePeer} when someone runs into a mine.
	 * @param robot The ship that got hit by the mine
	 * @return The score the owner of this object will receive.
	 */
	public void scoreMineDamage(String robot, double damage) {
		if (isActive()) {
			incrementRobotDamage(robot, damage);
			mineDamageScore += damage;
		}
	}

}
