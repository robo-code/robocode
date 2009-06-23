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

import robocode.IExtensionApi;

import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.battle.peer.RobotPeer;

/**
 * This interface will provide winning and losing conditions, scoring, and other rules
 * 
 * @author Joshua Galecki (original)
 *
 */
public interface ICustomRules {
	
	/**
	 * This method is called at the beginning of every round
	 * @param robots
	 * @param robjects
	 */
	void startRound(List<RobotPeer> robots, List<RobjectPeer> robjects);
	
	boolean isGameOver(int activeRobots, List<RobotPeer> robots, List<RobjectPeer> robjects);

	void updateTurn(List<RobotPeer> robots, List<RobjectPeer> robjects);

	void robotKill(RobotPeer robot);	
	
	void robotIsDead(RobotPeer robot);
	
	int computeActiveRobots(List<RobotPeer> robots);
	
	IContestantStatistics getEmptyStatistics();
	
	IExtensionApi getExtensionApi();
}
