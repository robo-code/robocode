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

import robocode.Robject;

import net.sf.robocode.battle.peer.RobotPeer;
import net.sf.robocode.battle.peer.TeamPeer;

/**
 * This class contains the rules for a Capture the Flag game
 * 
 * @author Joshua Galecki (original)
 *
 */
public class CaptureTheFlagRules extends CustomRules {
	//not actually capture the flag rules as of yet
		
		public boolean isGameOver(int activeRobots, List<RobotPeer> robots, 
				List<Robject> robjects) {
			if (activeRobots <= 1) {
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
			
			/*
			 * for (Robject robject : robjects)
			{
				if (robject.getType().equals("flag"))
				{
					Flag flag = (Flag)robject;
					return flag.isHeld();				
				}
			}
			return false;
			 */
		}	
	}
