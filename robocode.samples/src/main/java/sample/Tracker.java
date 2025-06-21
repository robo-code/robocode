/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;


import robocode.HitRobotEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

import java.awt.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;


/**
 * Tracker - a sample robot that demonstrates target tracking behavior.
 * <p>
 * Locks onto a robot, follows it, and fires when close.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Tracker extends Robot {
	int count = 0; // Counts turns spent searching for a target
	double gunTurnAmt; // Amount to turn the gun when searching
	String trackName; // Name of the currently tracked robot

	/**
	 * Main robot logic: search for targets by rotating the gun
	 */
	public void run() {
		// Set colors
		setBodyColor(new Color(128, 128, 50));
		setGunColor(new Color(50, 50, 20));
		setRadarColor(new Color(200, 200, 70));
		setScanColor(Color.white);
		setBulletColor(Color.blue);

		// Prepare gun
		trackName = null; // Initialize to not tracking anyone
		setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
		gunTurnAmt = 10; // Initialize gunTurn to 10

		// Main loop
		while (true) {
			// Rotate the gun to scan for enemies
			turnGunRight(gunTurnAmt);
			// Increment the search counter
			count++;
			// After 2 turns without seeing the target, look left
			if (count > 2) {
				gunTurnAmt = -10;
			}
			// After 5 turns without seeing the target, look right
			if (count > 5) {
				gunTurnAmt = 10;
			}
			// After 11 turns without seeing the target, forget it and find another
			if (count > 11) {
				trackName = null;
			}
		}
	}

	/**
	 * Handles robot detection events - core tracking behavior
	 */
	public void onScannedRobot(ScannedRobotEvent e) {

		// Ignore if we're tracking a different robot
		if (trackName != null && !e.getName().equals(trackName)) {
			return;
		}

		// Set a new target if we don't have one
		if (trackName == null) {
			trackName = e.getName();
			out.println("Tracking " + trackName);
		}
		// Reset search counter since we found our target
		count = 0;
		// If the target is far away, move toward it
		if (e.getDistance() > 150) {
			gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));

			turnGunRight(gunTurnAmt); // For better performance, this could use setTurnGunRight
			turnRight(e.getBearing()); // with an AdvancedRobot implementation
			// which would allow non-blocking movement
			ahead(e.getDistance() - 140);
			return;
		}

		// Target is within firing range - aim and fire
		gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
		turnGunRight(gunTurnAmt);
		fire(3);

		// Maintain optimal distance - back away if too close
		if (e.getDistance() < 100) {
			if (e.getBearing() > -90 && e.getBearing() <= 90) {
				back(40);
			} else {
				ahead(40);
			}
		}
		scan();
	}

	/**
	 * Handles collisions with other robots by targeting them
	 */
	public void onHitRobot(HitRobotEvent e) {
		// Announce a new target only if different from the current target
		if (trackName != null && !trackName.equals(e.getName())) {
			out.println("Tracking " + e.getName() + " due to collision");
		}
		// Set the collided robot as our new target
		trackName = e.getName();
		// Move back to create space
		// Note: This blocks scan events until complete
		// An AdvancedRobot could use non-blocking setBack() and execute() instead
		gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
		turnGunRight(gunTurnAmt);
		fire(3);
		back(50);
	}

	/**
	 * Celebrates victory with a spinning dance
	 */
	public void onWin(WinEvent e) {
		for (int i = 0; i < 50; i++) {
			turnRight(30);
			turnLeft(30);
		}
	}
}
