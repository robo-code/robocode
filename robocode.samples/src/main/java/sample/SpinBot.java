/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;


import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;


/**
 * SpinBot - a sample robot that demonstrates circular movement patterns.
 * <p>
 * This robot continuously moves in a circle while firing at maximum power when detecting enemies.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class SpinBot extends AdvancedRobot {

	/**
	 * Main run method - Sets colors and initiates a circular movement pattern
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.blue);
		setGunColor(Color.blue);
		setRadarColor(Color.black);
		setScanColor(Color.yellow);

		// Infinite loop for continuous operation
		while (true) {
			// Set extreme right turn to create circular movement
			setTurnRight(10000);
			// Limit speed to 5 pixels per turn
			setMaxVelocity(5);
			// Move forward while turning to create the circular pattern
			ahead(10000);
			// Loop repeats automatically
		}
	}

	/**
	 * Fires at maximum power when the radar detects an enemy robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		fire(3);
	}

	/**
	 * Handles robot collision events by firing at close enemies and adjusting a direction
	 * if this robot initiated the collision, helping maintain the spinning movement pattern.
	 */
	public void onHitRobot(HitRobotEvent e) {
		if (e.getBearing() > -10 && e.getBearing() < 10) {
			fire(3);
		}
		if (e.isMyFault()) {
			turnRight(10);
		}
	}
}
