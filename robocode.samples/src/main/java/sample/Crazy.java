/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;


import robocode.*;

import java.awt.*;


/**
 * Crazy - a sample robot that demonstrates movement patterns.
 * <p>
 * This robot moves in a zigzag pattern while firing at enemies.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Crazy extends AdvancedRobot {
	boolean movingForward;

	/**
	 * Main robot logic that sets colors and controls a movement pattern.
	 */
	public void run() {
		// Set colors
		setBodyColor(new Color(0, 200, 0));
		setGunColor(new Color(0, 150, 50));
		setRadarColor(new Color(0, 100, 100));
		setBulletColor(new Color(255, 255, 100));
		setScanColor(new Color(255, 200, 200));

		// Loop forever
		while (true) {
			// Queue a long-distance movement (40000 is effectively infinite)
			setAhead(40000);
			movingForward = true;
			// Queue a 90-degree right turn
			setTurnRight(90);
			// Note: The "set" methods only queue actions without executing them
			// The waitFor() method below starts execution and waits for turn completion
			// This is a key concept in Robocode's non-blocking movement system
			waitFor(new TurnCompleteCondition(this));
			// Robot continues moving forward while executing the next turn
			setTurnLeft(180);
			waitFor(new TurnCompleteCondition(this));
			// Complete one full zigzag by turning the other direction
			setTurnRight(180);
			waitFor(new TurnCompleteCondition(this));
			// Return to start of loop to repeat the pattern
		}
	}

	/**
	 * Handles wall collisions by reversing direction.
	 */
	public void onHitWall(HitWallEvent e) {
		// Bounce off!
		reverseDirection();
	}

	/**
	 * Toggles movement direction between forward and backward.
	 */
	public void reverseDirection() {
		if (movingForward) {
			setBack(40000);
			movingForward = false;
		} else {
			setAhead(40000);
			movingForward = true;
		}
	}

	/**
	 * Fires a low-power bullet when an enemy robot is detected.
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		fire(1);
	}

	/**
	 * Reverses direction when this robot is responsible for a collision.
	 */
	public void onHitRobot(HitRobotEvent e) {
		// Only reverse if we initiated the collision
		if (e.isMyFault()) {
			reverseDirection();
		}
	}
}
