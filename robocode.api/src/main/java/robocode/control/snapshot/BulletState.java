/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.snapshot;


/**
 * Defines a bullet state, which can be: just fired, moving somewhere, hitting a victim,
 * hitting another bullet, hitting the wall, exploded, or inactive.
 *
 * @author Flemming N. Larsen (original)
 *
 * @since 1.6.2
 */
public enum BulletState {

	/** The bullet has just been fired this turn and hence just been created. This state only last one turn. */
	FIRED(0),

	/** The bullet is now moving across the battlefield, but has not hit anything yet. */
	MOVING(1),

	/** The bullet has hit a robot victim. */
	HIT_VICTIM(2),

	/** The bullet has hit another bullet. */
	HIT_BULLET(3),

	/** The bullet has hit the wall, i.e. one of the four borders of the battlefield. */
	HIT_WALL(4),

	/** The bullet currently represents a robot explosion, i.e. a robot death. */
	EXPLODED(5),

	/** The bullet is currently inactive. Hence, it is not active or visible on the battlefield. */
	INACTIVE(6);

	private final int value;

	private BulletState(int value) {
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
	 * Returns a BulletState based on an integer value that represents a BulletState.
	 *
	 * @param value the integer value that represents a specific BulletState.
	 * @return a BulletState that corresponds to the specific integer value.
	 *
	 * @see #getValue()
	 *
	 * @throws IllegalArgumentException if the specified value does not correspond
	 *                                  to a BulletState and hence is invalid.
	 */
	public static BulletState toState(int value) {
		switch (value) {
		case 0:
			return FIRED;

		case 1:
			return MOVING;

		case 2:
			return HIT_VICTIM;

		case 3:
			return HIT_BULLET;

		case 4:
			return HIT_WALL;

		case 5:
			return EXPLODED;

		case 6:
			return INACTIVE;

		default:
			throw new IllegalArgumentException("unknown value");
		}
	}
	
	public boolean isActive() {
		return this == FIRED || this == MOVING;
	}
}
