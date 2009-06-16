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
import net.sf.robocode.battle.peer.TeamPeer;

/**
 * Classic rules have no extensions. Old school Robocode.
 * 
 * @author Joshua Galecki (original)
 *
 */
public class ClassicRules extends CustomRules {

	@Override
	public boolean isGameOver(int activeRobots, List<RobotPeer> robots,
			List<RobjectPeer> robjects) {
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
	}
	
	@Override
	public void startRound(List<RobotPeer> robots, List<RobjectPeer> robjects) {
		// do nothing		
	}

	@Override
	public void updateTurn(List<RobotPeer> robots, List<RobjectPeer> robjects) {
		//do nothing		
	}
}
