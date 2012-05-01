package CTF;

import java.util.ArrayList;
import java.util.List;

import net.sf.robocode.battle.peer.ContestantStatistics;
import net.sf.robocode.battle.IContestantStatistics;
import net.sf.robocode.battle.peer.RobotPeer;

public class CaptureTheFlagTeamStatistics extends ContestantStatistics {

	private static int captureScore = 0;
	private static int killBonus = 1;
	private static int deathPenalty = 2;

	private CaptureTheFlagTeamStatistics parentStats = null;
	private List<CaptureTheFlagRobotStatistics> teamStats = null;
	private String teamName;
	private int highestRank;
	
	public CaptureTheFlagTeamStatistics(String teamName) {
		teamStats = new ArrayList<CaptureTheFlagRobotStatistics>();
		
		this.teamName = teamName;
		
		currentScores = new ArrayList<Double>();
		totalScores = new ArrayList<Double>();
		scoreNames = new ArrayList<String>();

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
	
	public IContestantStatistics fakeConstructor(RobotPeer peer, int robots) {
		return null;
	}

	public void add(IContestantStatistics contStats)
	{
		((CaptureTheFlagRobotStatistics)contStats).setParentStats(this);
		teamStats.add((CaptureTheFlagRobotStatistics)contStats);
	}
	
	public void addBulletKill(double points)
	{
		currentScores.set(killBonus, currentScores.get(killBonus) + points);
		
		if (parentStats != null)
		{
			parentStats.addBulletKill(points);
		}
	}
	
	public void addRammingKill(double points)
	{
		currentScores.set(killBonus, currentScores.get(killBonus) + points);

		if (parentStats != null)
		{
			parentStats.addRammingKill(points);
		}
	}

	public void addDeathPenalty(double points) 
	{
		currentScores.set(deathPenalty, currentScores.get(deathPenalty) + points);

		if (parentStats != null)
		{
			parentStats.addDeathPenalty(points);
		}
	}

	public void addCapture(double points) 
	{
		currentScores.set(captureScore, currentScores.get(captureScore) + points);

		if (parentStats != null)
		{
			parentStats.addCapture(points);
		}	
	}
	
	public void addRank(int rank)
	{
		if (rank < highestRank)
		{
			if (highestRank == 3)
			{
				totalThirds--;
			}
			else if (highestRank == 2)
			{
				totalSeconds--;
			}
			
			highestRank = rank;
			if (rank == 1)
			{
				totalFirsts++;
			}
			else if (rank == 2)
			{
				totalSeconds++;
			}
			else if (rank == 3)
			{
				totalThirds++;
			}
		}
	}
	
	public void scoreBulletDamage(String robot, double damage) {
		// do nothing
	}

	public double scoreBulletKill(String robot) {
		// do nothing
		return 0;
	}

	public void scoreFirsts() {
		// do nothing		
	}

	public void scoreLastSurvivor() {
		// do nothing		
	}

	public void scoreRammingDamage(String robot) {
		// do nothing		
	}

	public double scoreRammingKill(String robot) {
		// do nothing
		return 0;
	}

	public void scoreRobotDeath(int enemiesRemaining) {
		// do nothing		
	}

	public void scoreSurvival() {
		// do nothing		
	}

	@Override
	protected String getTeamName() {
		return teamName;
	}
	
	@Override
	public void initialize()
	{
		super.initialize();
		for (CaptureTheFlagRobotStatistics robotStats : teamStats)
		{
			robotStats.initialize();
		}
		highestRank = 4;
	}
}