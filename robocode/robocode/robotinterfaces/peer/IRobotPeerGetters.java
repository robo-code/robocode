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
package robocode.robotinterfaces.peer;

/**
 * @author Pavel Savara (original)
 */
public interface IRobotPeerGetters {

	// AdvancedRobot calls below
	double getRadarTurnRemaining();
	double getDistanceRemaining();
	double getTurnRemaining();
	boolean isAdjustGunForBodyTurn();
	boolean isAdjustRadarForGunTurn();
	boolean isAdjustRadarForBodyTurn();

	// Robot calls below
	double getVelocity();
	double getRadarHeading();
	double getGunCoolingRate();
	String getName();
	long getTime();

	// Junior calls below
	double getHeading();
	double getGunHeading();
	double getGunTurnRemaining();
	double getEnergy();
	double getGunHeat();
	double getBattleFieldHeight();
	double getBattleFieldWidth();
	double getX();
	double getY();
	int getOthers();
	int getNumRounds();
	int getRoundNum();

	//counters
	void getCall();
	void setCall();
}
