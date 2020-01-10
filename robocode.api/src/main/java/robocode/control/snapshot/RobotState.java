/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.snapshot;


/**
 * Defines a robot state, which can be: active on the battlefield, hitting a wall or robot this turn, or dead.
 *
 * @author Flemming N. Larsen (original)
 *
 * @since 1.6.2
 */
public enum RobotState {

	/** The robot is active on the battlefield and has not hit the wall or a robot at this turn. */
	ACTIVE(0),

	/** The robot has hit a wall, i.e. one of the four borders, at this turn. This state only last one turn. */
	HIT_WALL(1),

	/** The robot has hit another robot at this turn. This state only last one turn. */
	HIT_ROBOT(2),

	/** The robot is dead. */
	DEAD(3);

	private final int value;

	private RobotState(int value) {
		this.value = value;
	}

	/**
	 * Returns the state as an integer value.
	 *
	 * @return an integer value representing this state.
	 *
	 * @see #toState(int)
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns a RobotState based on an integer value that represents a RobotState.
	 *
	 * @param value the integer value that represents a specific RobotState.
	 * @return a RobotState that corresponds to the specific integer value.
	 *
	 * @see #getValue()
	 *
	 * @throws IllegalArgumentException if the specified value does not correspond
	 *                                  to a RobotState and hence is invalid.
	 */
	public static RobotState toState(int value) {
		switch (value) {
		case 0:
			return ACTIVE;

		case 1:
			return HIT_WALL;

		case 2:
			return HIT_ROBOT;

		case 3:
			return DEAD;

		default:
			throw new IllegalArgumentException("unknown value");
		}
	}

	/**
	 * Checks if the robot is alive.
	 *
	 * @return {@code true} if the robot is alive; {@code false} otherwise.
	 *
	 * @see #isDead()
	 */
	public boolean isAlive() {
		return this != DEAD;
	}

	/**
	 * Checks if the robot is dead.
	 *
	 * @return {@code true} if the robot is dead; {@code false} otherwise.
	 *
	 * @see #isAlive()
	 */
	public boolean isDead() {
		return this == DEAD;
	}

	/**
	 * Checks if the robot has hit another robot.
	 *
	 * @return {@code true} if the robot has hit a robot; {@code false} otherwise.
	 *
	 * @see #isHitWall()
	 */
	public boolean isHitRobot() {
		return this == HIT_ROBOT;
	}

	/**
	 * Checks if the robot has hit the wall, i.e. one of the four borders.
	 *
	 * @return {@code true} if the robot has hit the wall; {@code false} otherwise.
	 *
	 * @see #isHitRobot()
	 */
	public boolean isHitWall() {
		return this == HIT_WALL;
	}
}
