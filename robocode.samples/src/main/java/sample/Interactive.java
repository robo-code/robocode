/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;


import robocode.AdvancedRobot;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import static java.awt.event.KeyEvent.*;
import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.normalRelativeAngle;


/**
 * Interactive - a sample robot that demonstrates user-controlled movement through keyboard and mouse input.
 * <p>
 * This robot allows direct control using keyboard arrows and WASD keys for movement, and mouse for aiming and firing.
 * <p>
 * Keys:
 * - W or arrow up:    Move forward
 * - S or arrow down:  Move backward
 * - D or arrow right: Turn right
 * - A or arrow left:  Turn left
 * Mouse:
 * - Moving:      Moves the aim, which the gun will follow
 * - Wheel up:    Move forward
 * - Wheel down:  Move backward
 * - Button 1:    Fire a bullet with power = 1
 * - Button 2:    Fire a bullet with power = 2
 * - Button 3:    Fire a bullet with power = 3
 * <p>
 * The bullet color corresponds to the fire power:
 * - Power = 1:   Yellow (lowest damage)
 * - Power = 2:   Orange (medium damage)
 * - Power = 3:   Red (highest damage)
 * <p>
 * Note that the robot will continue firing as long as the mouse button is
 * pressed down.
 * <p>
 * When the "Paint" button is enabled on the robot console window,
 * a red cross-hair will be displayed showing the current aim position
 * controlled by the mouse.
 *
 * @author Flemming N. Larsen (original)
 *
 * @version 1.2
 * @since 1.3.4
 */
public class Interactive extends AdvancedRobot {

	// Movement direction indicator: 1 = forward, 0 = stopped, -1 = backward
	int moveDirection;

	// Rotation direction indicator: 1 = right turn, 0 = no rotation, -1 = left turn
	int turnDirection;

	// Distance to move in pixels/units
	double moveAmount;

	// Target aim coordinates (x,y) controlled by mouse position
	int aimX, aimY;

	// Bullet fire power: 1-3 for firing (energy consumed), 0 = don't fire
	int firePower;

	// Called when the robot must run
	public void run() {

		// Set robot appearance colors:
		// body = black, gun = white, radar = red
		setColors(Color.BLACK, Color.WHITE, Color.RED);

		// Loop forever
		for (;;) {
			// Control robot movement: forward, backward or stop
			// based on moveDirection and moveAmount values
			setAhead(moveAmount * moveDirection);

			// Gradually decrease movement distance until stopped
			// This creates automatic deceleration when the mouse wheel
			// is no longer being rotated
			moveAmount = Math.max(0, moveAmount - 1);

			// Control robot rotation: right, left, or no rotation
			// at 45 degrees per turn when turning
			setTurnRight(45 * turnDirection); // degrees

			// Calculate the angle to target position and turn gun to face it
			// Uses mouse position (aimX, aimY) as the target
			double angle = normalAbsoluteAngle(Math.atan2(aimX - getX(), aimY - getY()));

			setTurnGunRightRadians(normalRelativeAngle(angle - getGunHeadingRadians()));

			// Fire weapon if the power level is set (controlled by mouse buttons)
			// No firing occurs when firePower = 0
			if (firePower > 0) {
				setFire(firePower);
			}

			// Execute all pending commands for this turn
			execute();

			// Continue to the next turn in the infinite loop
		}
	}

	// Handles keyboard key press events for robot control
	public void onKeyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case VK_UP:
		case VK_W:
			// Arrow up key: the move direction = forward (infinitely)
			moveDirection = 1;
			moveAmount = Double.POSITIVE_INFINITY;
			break;

		case VK_DOWN:
		case VK_S:
			// Arrow down key: the move direction = backward (infinitely)
			moveDirection = -1;
			moveAmount = Double.POSITIVE_INFINITY;
			break;

		case VK_RIGHT:
		case VK_D:
			// Right arrow or D key: set the turn direction to right
			turnDirection = 1;
			break;

		case VK_LEFT:
		case VK_A:
			// Left arrow or A key: set the turn direction to left
			turnDirection = -1;
			break;
		}
	}

	// Handles keyboard key release events to stop movement/rotation
	public void onKeyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case VK_UP:
		case VK_W:
		case VK_DOWN:
		case VK_S:
			// Arrow up and down keys: the move direction = stand still
			moveDirection = 0;
			moveAmount = 0;
			break;

		case VK_RIGHT:
		case VK_D:
		case VK_LEFT:
		case VK_A:
			// Arrow right and left keys: the turn direction = stop turning
			turnDirection = 0;
			break;
		}
	}

	// Handles mouse wheel rotation to control forward/backward movement
	public void onMouseWheelMoved(MouseWheelEvent e) {
		// Determine the movement direction based on wheel rotation:
		// Negative rotation = forward movement (1)
		// Positive rotation = backward movement (-1)
		moveDirection = (e.getWheelRotation() < 0) ? 1 : -1;

		// Calculate movement distance based on wheel rotation magnitude
		// Each rotation step adds 5 pixels of movement distance
		// Larger values would increase the speed sensitivity
		moveAmount += Math.abs(e.getWheelRotation()) * 5;
	}

	// Updates gun aim target position when the mouse is moved
	public void onMouseMoved(MouseEvent e) {
		// Set aim target to the current mouse cursor position
		aimX = e.getX();
		aimY = e.getY();
	}

	// Handles mouse button press events to control weapon firing
	public void onMousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			// Right mouse button: high-power shot (3 energy points)
			firePower = 3;
			setBulletColor(Color.RED);
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			// Middle mouse button: medium-power shot (2 energy points)
			firePower = 2;
			setBulletColor(Color.ORANGE);
		} else {
			// Left mouse button or any other button:
			// low-power shot (1 energy point)
			firePower = 1;
			setBulletColor(Color.YELLOW);
		}
	}

	// Stops weapon firing when mouse button is released
	public void onMouseReleased(MouseEvent e) {
		// Set the fire power to 0 to stop firing
		firePower = 0;
	}

	// Renders custom graphics when the "Paint" button is enabled
	public void onPaint(Graphics2D g) {
		// Draw a red crosshair at the current aim position
		// consisting of a circle and intersecting lines
		g.setColor(Color.RED);
		g.drawOval(aimX - 15, aimY - 15, 30, 30);
		g.drawLine(aimX, aimY - 4, aimX, aimY + 4);
		g.drawLine(aimX - 4, aimY, aimX + 4, aimY);
	}
}
