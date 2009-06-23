package net.sf.robocode.battle;

//import java.util.ArrayList;
//import java.util.List;

//import extensions.Base;
//import extensions.Flag;


import net.sf.robocode.battle.peer.RobotPeer;

import robocode.IExtensionApi;
import robocode.Robot;

public class CaptureTheFlagExtensionApi implements IExtensionApi{

	CaptureTheFlagRules rules;
	
	public CaptureTheFlagExtensionApi(CaptureTheFlagRules rules)
	{
		this.rules = rules;
	}

	public BasePeer getBase(int teamNumber) {
		return rules.getBase(teamNumber);
	}
	
	public FlagPeer getFlag(int teamNumber)
	{
		return rules.getFlag(teamNumber);
	}
	
	public int getTeamNumber(Robot robot)
	{
		for (RobotPeer robotPeer : rules.getTeam(1))
		{
			if (robot.getName() == robotPeer.getName())
			{
				return 1;
			}
		}
		return 2;
	}
	
	public boolean isFlagAtBase(int teamNumber)
	{
		FlagPeer flag = rules.getFlag(teamNumber);
		BasePeer base = rules.getBase(teamNumber);
		
		return base.getBoundaryRect().contains(flag.getBoundaryRect());
	}
}
