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

import java.awt.*;


/**
 * RamFire - a sample robot that demonstrates aggressive ramming tactics.
 * <p>
 * This robot actively seeks out opponents, rams into them, and fires with
 * appropriate power based on the enemy's remaining energy.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RamFire extends Robot {
	int turnDirection = 1; // Clockwise or counterclockwise

	/**
	 * The main robot behavior. Continuously spins the robot to scan for enemies.
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.lightGray);
		setGunColor(Color.gray);
		setRadarColor(Color.darkGray);

		while (true) {
			turnRight(5 * turnDirection);
		}
	}

	/**
	 * Handles enemy detection. When an enemy is spotted, the robot turns toward it,
	 * moves forward to ram it, and performs another scan.
	 */
	public void onScannedRobot(ScannedRobotEvent e) {

		if (e.getBearing() >= 0) {
			turnDirection = 1;
		} else {
			turnDirection = -1;
		}

		turnRight(e.getBearing());
		ahead(e.getDistance() + 5);
		scan(); // Might want to move ahead again!
	}

	/**
	 * Handles collisions with enemy robots. When a collision occurs, the robot turns to face the enemy,
	 * fires with power appropriate to the enemy's remaining energy, and rams again for bonus points.
	 */
	public void onHitRobot(HitRobotEvent e) {
		if (e.getBearing() >= 0) {
			turnDirection = 1;
		} else {
			turnDirection = -1;
		}
		turnRight(e.getBearing());

		// Determine a shot that won't kill the robot...
		// We want to ram him instead for bonus points
		if (e.getEnergy() > 16) {
			fire(3);
		} else if (e.getEnergy() > 10) {
			fire(2);
		} else if (e.getEnergy() > 4) {
			fire(1);
		} else if (e.getEnergy() > 2) {
			fire(.5);
		} else if (e.getEnergy() > .4) {
			fire(.1);
		}
		ahead(40); // Ram him again!
	}
}
