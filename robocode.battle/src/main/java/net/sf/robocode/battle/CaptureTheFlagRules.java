package net.sf.robocode.battle;

import java.util.List;

import net.sf.robocode.battle.peer.RobotPeer;
import 

net.sf.robocode.battle.peer.TeamPeer;

public class CaptureTheFlagRules implements ICustomRules {
//not actually capture the flag rules as of yet
	
	public boolean isGameOver(int activeRobots, List<RobotPeer> robots) {
		if (activeRobots <= 1) 
		{
			return true;
		}

		boolean found = false;
		TeamPeer currentTeam = null;
		for (RobotPeer currentRobot : robots) {
			if (!currentRobot.isDead()) {
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
	
}
