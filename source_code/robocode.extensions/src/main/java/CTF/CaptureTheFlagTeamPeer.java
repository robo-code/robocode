package CTF;


import java.util.List;

import net.sf.robocode.battle.IContestantStatistics;
import net.sf.robocode.battle.peer.ContestantPeer;
import net.sf.robocode.battle.peer.RobotPeer;
import net.sf.robocode.battle.peer.TeamPeer;

public class CaptureTheFlagTeamPeer extends TeamPeer {
	
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private RobotPeer teamLeader;
	private final IContestantStatistics teamStatistics;

	public CaptureTheFlagTeamPeer(String name, List<String> memberNames, int contestIndex) {
		super(name, memberNames, contestIndex);
		
		this.teamStatistics = new CaptureTheFlagTeamStatistics(name);
	}

	public int compareTo(ContestantPeer cp) {
		double myScore = teamStatistics.getCombinedScore();
		double hisScore = cp.getStatistics().getCombinedScore();

		if (teamLeader != null && teamLeader.getRobotStatistics().isInRound()) {
			myScore += teamStatistics.getCurrentScore();
			hisScore += cp.getStatistics().getCurrentScore();
		}
		if (myScore < hisScore) {
			return -1;
		}
		if (myScore > hisScore) {
			return 1;
		}
		return 0;
	}

	public IContestantStatistics getStatistics() {
		return teamStatistics;
	}

	@Override
	public boolean add(RobotPeer robot)
	{
		((CaptureTheFlagTeamStatistics)teamStatistics).add(robot.getStatistics());
		return super.add(robot);
	}
	
	public void initialize()
	{
		for (RobotPeer robot : this)
		{
			robot.getStatistics().initialize();
		}
	}
}
