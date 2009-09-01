/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.battle;


import robocode.Event;

import java.util.List;

import net.sf.robocode.battle.peer.ContestantPeer;
import net.sf.robocode.battle.peer.RobotPeer;
import net.sf.robocode.battle.peer.TeamPeer;


/**
 * @author Pavel Savara (original)
 */
public interface IRobotPeerBattle extends ContestantPeer {
	void setSGPaintEnabled(boolean enabled);

	// TODO: remove, hopefully
	// RobotStatistics getRobotStatistics();

	TeamPeer getTeamPeer();

	void publishStatus(long currentTurn);

	void addEvent(Event event);

	void setPaintEnabled(boolean enabled);

	void kill();

	void cleanup();

	boolean isDead();

	boolean isAlive();

	boolean isRunning();

	boolean isWinner();

	boolean isTeamLeader();

	void setHalt(boolean value);

	void println(String s);

	void waitWakeup();

	void waitSleeping(long millisWait, int microWait);

	void waitForStop();

	void setWinner(boolean newWinner);

	void initializeRound(List<RobotPeer> robots, double[][] initialRobotPositions);

	void startRound(long waitTime);

	void setSkippedTurns();

	void performLoadCommands();

	void performMove(List<RobotPeer> robots, double zapEnergy);

	void performScan(List<RobotPeer> robots);
}
