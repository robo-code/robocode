/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;


import robocode.DeathEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

import java.awt.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;


/**
 * Corners - a sample robot that demonstrates defensive corner positioning and adaptive strategy.
 * <p>
 * This robot moves to a corner, then rotates its gun back and forth scanning for enemies.
 * If it performs poorly in a round, it will try a different corner in the next round.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Corners extends Robot {
	int others; // Tracks the initial number of opponents
	static int corner = 0; // Current corner angle (static to persist between rounds)
	boolean stopWhenSeeRobot = false; // Flag to control movement behavior when enemies are detected

	/**
	 * Main robot behavior. Sets colors, moves to the corner, and continuously scans for enemies.
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.red);
		setGunColor(Color.black);
		setRadarColor(Color.yellow);
		setBulletColor(Color.green);
		setScanColor(Color.green);

		// Save # of other bots
		others = getOthers();

		// Move to a corner
		goCorner();

		// Initialize gun turn speed to 3
		int gunIncrement = 3;

		// Spin gun back and forth
		while (true) {
			for (int i = 0; i < 30; i++) {
				turnGunLeft(gunIncrement);
			}
			gunIncrement *= -1;
		}
	}

	/**
	 * Navigates to a corner of the battlefield using a simple but inefficient approach.
	 * This method could be optimized for better performance.
	 */
	public void goCorner() {
		// We don't want to stop when we're just turning...
		stopWhenSeeRobot = false;
		// turn to face the wall to the "right" of our desired corner.
		turnRight(normalRelativeAngleDegrees(corner - getHeading()));
		// Ok, now we don't want to crash into any robot in our way...
		stopWhenSeeRobot = true;
		// Move to that wall
		ahead(5000);
		// Turn to face the corner
		turnLeft(90);
		// Move to the corner
		ahead(5000);
		// Turn gun to starting point
		turnGunLeft(90);
	}

	/**
	 * Handles robot detection events. Either stops and fires or just fires
	 * depending on the current movement strategy.
	 *
	 * @param e The scanned robot event containing target information
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Should we stop, or just fire?
		if (stopWhenSeeRobot) {
			// Stop everything!  You can safely call stop multiple times.
			stop();
			// Call our custom firing method
			smartFire(e.getDistance());
			// Actively scan for additional robots
			// NOTE: If scan() detects a robot, the current onScannedRobot event
			// will be interrupted, and a new event will be triggered
			scan();
			// The code below only executes if no additional robots were detected
			// to Resume the previous movement pattern
			resume();
		} else {
			smartFire(e.getDistance());
		}
	}

	/**
	 * Adjusts fire power based on distance to target and current energy level.
	 * Uses higher fire power at closer ranges for maximum damage.
	 *
	 * @param robotDistance the distance to the target robot in pixels
	 */
	public void smartFire(double robotDistance) {
		if (robotDistance > 200 || getEnergy() < 15) {
			fire(1); // Low power for long distance or when energy is low
		} else if (robotDistance > 50) {
			fire(2); // Medium power for moderate distance
		} else {
			fire(3); // High power for close range
		}
	}

	/**
	 * Evaluates performance after robot destruction and adjusts strategy.
	 * Changes corner position if performance was poor in the current round.
	 *
	 * @param e The death event
	 */
	public void onDeath(DeathEvent e) {
		// Safety check: avoids division by zero, though 'others' should never be 0
		if (others == 0) {
			return;
		}

		// If 75% of the robots are still alive when we die, we'll switch corners.
		if (getOthers() / (double) others >= .75) {
			corner += 90; // Rotate 90 degrees to try the next corner
			if (corner == 270) {
				corner = -90; // Reset to the first corner after completing the full rotation
			}
			out.println("I died and did poorly... switching corner to " + corner);
		} else {
			out.println("I died but did well.  I will still use corner " + corner);
		}
	}
}
