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
package sample;


import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.HitByBulletEvent;


/**
 * @author Pavel Savara (original)
 */
public class DebugProperties extends Robot {

	/**
	 * Demonstration of debug properties
	 */
	public void run() {

		//noinspection InfiniteLoopStatement
		while (true) {
			ahead(100); // Move ahead 100
			turnGunRight(360); // Spin gun around
			back(100); // Move back 100
			turnGunRight(360); // Spin gun around
		}
	}

	/**
	 * When we see a robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		putDebugProperty("lastScannedRobot", e.getName() + " at " + e.getBearing() + " degrees");
	}

	/**
	 * When we were hit
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		putDebugProperty("lastHitBy", e.getName() + " with power of bullet " + e.getPower());
	}
}
