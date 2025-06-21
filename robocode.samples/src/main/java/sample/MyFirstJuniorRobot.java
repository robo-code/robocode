/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;


import robocode.JuniorRobot;


/**
 * MyFirstJuniorRobot - a sample robot that demonstrates basic movement patterns and targeting.
 * <p>
 * Moves in a seesaw motion and spins the gun around at each end
 * when it cannot see any enemy robot. When the robot sees an enemy
 * robot, it will immediately turn the gun and fire at it.
 *
 * @author Flemming N. Larsen (original)
 */
public class MyFirstJuniorRobot extends JuniorRobot {

	/**
	 * The main run method - Implements a seesaw movement pattern
	 */
	public void run() {
		// Set robot colors (body, gun, radar)
		setColors(green, black, blue);

		// Seesaw movement pattern - repeats forever
		while (true) {
			ahead(100); // Move forward 100 pixels
			turnGunRight(360); // Scan for enemies by spinning gun 360 degrees
			back(100); // Move backward 100 pixels
			turnGunRight(360); // Scan for enemies by spinning gun 360 degrees
		}
	}

	/**
	 * Called when our robot's radar detects another robot
	 * Responds by aiming the gun at the detected robot and firing
	 */
	public void onScannedRobot() {
		// Aim the gun directly at the detected robot
		turnGunTo(scannedAngle);

		// Fire with power level 1 (low power)
		fire(1);
	}

	/**
	 * Called when our robot is hit by a bullet
	 * Responds by moving perpendicular to the bullet's path
	 * to potentially avoid future shots from the same direction
	 */
	public void onHitByBullet() {
		// Move forward 100 pixels while turning left perpendicular to the bullet's path
		turnAheadLeft(100, 90 - hitByBulletBearing);
	}
}
