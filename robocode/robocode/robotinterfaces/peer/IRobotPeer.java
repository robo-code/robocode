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

import robocode.*;
import robocode.Event;
import robocode.peer.robot.IEventManager;
import robocode.peer.robot.RobotStatistics;
import robocode.peer.ContestantPeer;
import robocode.peer.TeamPeer;

import java.awt.*;
import java.util.*;
import java.io.File;
import java.io.Serializable;
import java.io.IOException;

/**
 * @author Pavel Savara (original)
 */
public interface IRobotPeer extends ContestantPeer {
	void setJuniorFire(double power);
	double getJuniorFirePower();

	IEventManager getEventManager();
	void setInterruptible(boolean interruptable);

	void sendMessage(String name, Serializable message) throws IOException;
	void broadcastMessage(Serializable message) throws IOException;

	String[] getTeammates();
	boolean isTeammate(String name);

	RobotStatistics getRobotStatistics();
	TeamPeer getTeamPeer();

	//void death();
	void move(double distance);
	void tick();
	void stop(boolean overwrite);
	void resume();
	void turnChassis(double radians);
	void turnGun(double radians);
	void turnRadar(double radians);
	void turnAndMoveChassis(double distance, double radians);
	void scanReset();

	void setTurnChassis(double radians);
	void setTurnGun(double radians);
	void setTurnRadar(double radians);
	void setMove(double distance);
	void setResume();
	void setStop(boolean overwrite);
	Bullet setFire(double power);
	void waitFor(Condition condition);

	File getDataDirectory();
	File getDataFile(String filename);
	long getDataQuotaAvailable();

	void getCall();
	void setCall();

	void setAdjustGunForBodyTurn(boolean newAdjustGunForBodyTurn);
	void setAdjustRadarForGunTurn(boolean newAdjustRadarForGunTurn);
	void setAdjustRadarForBodyTurn(boolean newAdjustRadarForBodyTurn);
	boolean isAdjustGunForBodyTurn();
	boolean isAdjustRadarForGunTurn();
	boolean isAdjustRadarForBodyTurn();

	void setMaxTurnRate(double newTurnRate);
	void setMaxVelocity(double newVelocity);

	void setBodyColor(Color color);
	void setGunColor(Color color);
	void setRadarColor(Color color);
	void setBulletColor(Color color);
	void setScanColor(Color color);


	double getBattleFieldHeight();
	double getBattleFieldWidth();
	double getX();
	double getY();
	double getHeading();
	double getGunHeading();
	double getRadarHeading();
	double getGunCoolingRate();
	double getGunHeat();
	double getVelocity();
	double getEnergy();
	double getGunTurnRemaining();
	double getRadarTurnRemaining();
	double getTurnRemaining();
	double getDistanceRemaining();
	int getNumRounds();
	int getRoundNum();
	long getTime();
	int getOthers();
	String getName();
}
