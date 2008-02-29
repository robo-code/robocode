/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * This is private interface. You should build any external component (or robot)
 * based on it's current methods because it will change in the future.
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.robotinterfaces.peer;


import robocode.Bullet;

import java.awt.*;


/**
 * @author Pavel Savara (original)
 */
public interface IBasicRobotPeer {

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

	// Robot calls below
	double getVelocity();
	double getRadarHeading();
	double getGunCoolingRate();
	String getName();
	long getTime();

	// AdvancedRobot calls below
	double getRadarTurnRemaining();
	double getDistanceRemaining();
	double getTurnRemaining();
	boolean isAdjustGunForBodyTurn();
	boolean isAdjustRadarForGunTurn();
	boolean isAdjustRadarForBodyTurn();

	// asynchronous actions
	Bullet setFire(double power);

	// blocking actions
	void tick();
	void move(double distance);
	void turnChassis(double radians);
	void turnGun(double radians);

	// fast setters
	void setBodyColor(Color color);
	void setGunColor(Color color);
	void setRadarColor(Color color);
	void setBulletColor(Color color);
	void setScanColor(Color color);

	// counters
	void getCall();
	void setCall();
}

