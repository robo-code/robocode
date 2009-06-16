/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 * 		Joshua Galecki
 * 		-Initial implementation
  *******************************************************************************/


package net.sf.robocode.battle;

import java.util.List;

import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.battle.peer.RobotPeer;


/**
 * This class contains the rules for a Capture the Flag game
 * 
 * @author Joshua Galecki (original)
 *
 */
public class CaptureTheFlagRules extends CustomRules {
//not actually capture the flag rules as of yet
	
	public boolean isGameOver(int activeRobots, List<RobotPeer> robots, 
			List<RobjectPeer> robjects) {
		if (activeRobots <= 1) {
			return true;
		}

		for (RobjectPeer robject : robjects)
		{
			if (robject.getType().equals("flag"))
			{
				Flag flag = (Flag)robject;
				for (RobjectPeer otherRobject : robjects)
				{
					if (otherRobject.getType().equals("base") &&
							flag.getTeam() != otherRobject.getTeam() &&
							otherRobject.getBoundaryRect().contains(flag.getBoundaryRect()))
					{
						flag.drop();
						return true;
					}
				}			
			}
		}
		return false;
		
	}

	@Override
	public void startRound(List<RobotPeer> robots, List<RobjectPeer> robjects) {
		for (RobjectPeer robject : robjects)
		{
			robject.roundStarted();
		}
		
	}

	@Override
	public void updateTurn(List<RobotPeer> robots, List<RobjectPeer> robjects) {
		for (RobjectPeer robject : robjects)
		{
			robject.turnUpdate();
		}
	}	
}
