/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;


import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

import java.awt.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;


/**
 * TrackFire - a sample robot that demonstrates target tracking and firing.
 * <p>
 * Sits still while tracking and firing at the nearest robot it detects.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class TrackFire extends Robot {

	/**
	 * The main run method. Sets colors and continuously rotates the gun.
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.pink);
		setGunColor(Color.pink);
		setRadarColor(Color.pink);
		setScanColor(Color.pink);
		setBulletColor(Color.pink);

		// Main control loop
		while (true) {
			turnGunRight(10); // Rotates gun and scans automatically
		}
	}

	/**
	 * Handles robot detection events. Calculates gun bearing and fires when appropriate.
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Calculate the exact location of the robot
		double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

		// Fire when the gun is closely aligned with the target
		if (Math.abs(bearingFromGun) <= 3) {
			turnGunRight(bearingFromGun);
			// Check gun heat before firing to avoid wasting a turn
			// when the gun is not ready to fire
			if (getGunHeat() == 0) {
				fire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
			}
		} else {
			// Set the gun to turn toward the target
			// Note: This rotation will take effect on the next execution cycle
			turnGunRight(bearingFromGun);
		}
		// Force another scan when the gun is directly facing the target
		// This is only needed if the gun/radar has stopped turning
		// because otherwise scanning happens automatically
		if (bearingFromGun == 0) {
			scan();
		}
	}

	/**
	 * Celebrates winning by spinning in place.
	 */
	public void onWin(WinEvent e) {
		// Perform victory celebration by spinning in place
		turnRight(36000);
	}
}				
