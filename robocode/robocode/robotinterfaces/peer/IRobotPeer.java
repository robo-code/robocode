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
public interface IRobotPeer extends  IRobotPeerGetters {
	//asynchronous actions
	Bullet setFire(double power);

	//blocking actions
	void tick();
	void move(double distance);
	void turnChassis(double radians);
	void turnAndMoveChassis(double distance, double radians);
	void turnGun(double radians);

	//fast setters
	void setBodyColor(Color color);
	void setGunColor(Color color);
	void setRadarColor(Color color);
	void setBulletColor(Color color);
	void setScanColor(Color color);

	//counters
	void getCall();
	void setCall();
}

