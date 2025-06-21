/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;


import robocode.HitByBulletEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;


/**
 * MyFirstRobot - a sample robot that demonstrates basic movement and targeting.
 * <p>
 * Moves in a seesaw motion and spins the gun around at each end.
 *
 * @author Mathew A. Nelson (original)
 */
public class MyFirstRobot extends Robot {

	/**
	 * The main run method - Implements a seesaw movement pattern
	 */
	public void run() {

		while (true) {
			ahead(100); // Move forward 100 pixels
			turnGunRight(360); // Scan for enemies by spinning gun 360 degrees
			back(100); // Move backward 100 pixels
			turnGunRight(360); // Scan for enemies by spinning gun 360 degrees
		}
	}

	/**
	 * Called when our robot's radar detects another robot
	 * Fires a bullet with power 1 at the detected robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		fire(1);
	}

	/**
	 * Called when our robot is hit by a bullet
	 * Responds by turning perpendicular to the bullet's path
	 * to potentially avoid future shots from the same direction
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		turnLeft(90 - e.getBearing());
	}
}												

