/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;


import robocode.AdvancedRobot;
import robocode.util.Utils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashSet;
import java.util.Set;

import static java.awt.event.KeyEvent.*;
import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.normalRelativeAngle;


/**
 * Interactive_v2 - a sample robot that demonstrates absolute directional movement control using keyboard and mouse input.
 * <p>
 * This robot is controlled using the arrow keys (or WASD) and mouse input.
 * <p>
 * Keys:
 * - W or arrow up:    Move up (north)
 * - S or arrow down:  Move down (south)
 * - D or arrow right: Move right (east)
 * - A or arrow left:  Move left (west)
 * Mouse:
 * - Moving:      Moves the aim point that the gun will follow
 * - Button 1:    Fire a bullet with power = 1
 * - Button 2:    Fire a bullet with power = 2
 * - Button 3:    Fire a bullet with power = 3
 * <p>
 * The bullet color corresponds to its power level:
 * - Power = 1:   Yellow
 * - Power = 2:   Orange
 * - Power = 3:   Red
 * <p>
 * Note that the robot will continue firing as long as a mouse button is
 * pressed down.
 * <p>
 * By enabling the "Paint" button on the robot console window for this robot,
 * a red crosshair will be displayed at the current aim point (controlled by the
 * mouse).
 *
 * @author Flemming N. Larsen (original)
 * @author Tuan Anh Nguyen (contributor)
 *
 * @version 2.0
 *
 * @since 1.7.2.2
 */
public class Interactive_v2 extends AdvancedRobot {

	// The coordinate of the aim (x,y)
	int aimX, aimY;

	// Fire power, where 0 = don't fire
	int firePower;

	// Absolute directions on the screen
	private enum Direction {
		UP,
		DOWN,
		LEFT,
		RIGHT
	}

	// Current move directions
	private final Set<Direction> directions = new HashSet<Direction>();

	// Called when the robot must run
	public void run() {

		// Sets the colors of the robot
		// body = black, gun = white, radar = red
		setColors(Color.BLACK, Color.WHITE, Color.RED);

		// Loop forever
		for (;;) {
			// Move the robot compared to the distance to move
			setAhead(distanceToMove());

			// Turn the body to it points in the correct direction
			setTurnRight(angleToTurnInDegrees());

			// Turns the gun toward the current aim coordinate (x,y) controlled by
			// the current mouse coordinate
			double angle = normalAbsoluteAngle(Math.atan2(aimX - getX(), aimY - getY()));

			setTurnGunRightRadians(normalRelativeAngle(angle - getGunHeadingRadians()));

			// Fire the gun with the specified fire power, unless the fire power = 0
			if (firePower > 0) {
				setFire(firePower);
			}

			// Execute all pending set-statements
			execute();

			// The next turn is processed in this loop
		}
	}

	// Called when a key has been pressed
	public void onKeyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case VK_UP:
		case VK_W:
			directions.add(Direction.UP);
			break;

		case VK_DOWN:
		case VK_S:
			directions.add(Direction.DOWN);
			break;

		case VK_RIGHT:
		case VK_D:
			directions.add(Direction.RIGHT);
			break;

		case VK_LEFT:
		case VK_A:
			directions.add(Direction.LEFT);
			break;
		}
	}

	// Called when a key has been released (after being pressed)
	public void onKeyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case VK_UP:
		case VK_W:
			directions.remove(Direction.UP);
			break;

		case VK_DOWN:
		case VK_S:
			directions.remove(Direction.DOWN);
			break;

		case VK_RIGHT:
		case VK_D:
			directions.remove(Direction.RIGHT);
			break;

		case VK_LEFT:
		case VK_A:
			directions.remove(Direction.LEFT);
			break;
		}
	}

	// Called when the mouse wheel is rotated
	public void onMouseWheelMoved(MouseWheelEvent e) {// Do nothing
	}

	// Called when the mouse has been moved
	public void onMouseMoved(MouseEvent e) {
		// Set the aim coordinate = the mouse pointer coordinate
		aimX = e.getX();
		aimY = e.getY();
	}

	// Called when a mouse button has been pressed
	public void onMousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			// Button 3: fire power = 3 energy points, bullet color = red
			firePower = 3;
			setBulletColor(Color.RED);
		} else if (e.getButton() == MouseEvent.BUTTON2) {
			// Button 2: fire power = 2 energy points, bullet color = orange
			firePower = 2;
			setBulletColor(Color.ORANGE);
		} else {
			// Button 1 or unknown button:
			// fire power = 1 energy points, bullet color = yellow
			firePower = 1;
			setBulletColor(Color.YELLOW);
		}
	}

	// Called when a mouse button has been released (after being pressed)
	public void onMouseReleased(MouseEvent e) {
		// Fire power = 0, which means "don't fire"
		firePower = 0;
	}

	// Called to paint graphics for this robot.
	// "Paint" button on the robot console window for this robot must be
	// enabled to see the paintings.
	public void onPaint(Graphics2D g) {
		// Draw a red cross-hair with the center at the current aim
		// coordinate (x,y)
		g.setColor(Color.RED);
		g.drawOval(aimX - 15, aimY - 15, 30, 30);
		g.drawLine(aimX, aimY - 4, aimX, aimY + 4);
		g.drawLine(aimX - 4, aimY, aimX + 4, aimY);
	}

	// Returns the angle to turn, which is the delta between the desired
	// direction and the current heading of the robot
	private double angleToTurnInDegrees() {
		if (directions.isEmpty()) {
			return 0;
		}
		return Utils.normalRelativeAngleDegrees(desiredDirection() - getHeading());
	}

	// Returns the distance to move
	private double distanceToMove() {
		// If no keys are pressed, we should not move at all
		if (directions.isEmpty()) {
			return 0;
		}
		// If the robot has more than 45 degrees to turn, move only 5 pixels
		if (Math.abs(angleToTurnInDegrees()) > 45) {
			return 5;
		}
		// Otherwise, move at full speed
		return Double.POSITIVE_INFINITY;
	}

	// Return the desired direction depending on the pending move directions.
	// When one direction key is pressed (up/W, right/D, down/S, left/A),
	//  the robot moves in cardinal directions (North, East, South, West).
	// When two direction keys are pressed simultaneously,
	//  the robot moves diagonally (Northeast, Northwest, Southeast, Southwest).
	private double desiredDirection() {
		if (directions.contains(Direction.UP)) {
			if (directions.contains(Direction.RIGHT)) {
				return 45;
			}
			if (directions.contains(Direction.LEFT)) {
				return 315;
			}
			return 0;
		}
		if (directions.contains(Direction.DOWN)) {
			if (directions.contains(Direction.RIGHT)) {
				return 135;
			}
			if (directions.contains(Direction.LEFT)) {
				return 225;
			}
			return 180;
		}
		if (directions.contains(Direction.RIGHT)) {
			return 90;
		}
		if (directions.contains(Direction.LEFT)) {
			return 270;
		}
		return 0;
	}
}
