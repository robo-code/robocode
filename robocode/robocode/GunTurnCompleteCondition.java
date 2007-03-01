/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Javadoc
 *******************************************************************************/
package robocode;


/**
 * A prebuilt condition you can use that indicates your gun has finished
 * turning.
 *
 * @see Condition
 *
 * @author Mathew A. Nelson (original)
 */
public class GunTurnCompleteCondition extends Condition {
	private AdvancedRobot robot = null;

	/**
	 * Creates a new GunTurnCompleteCondition with default priority.
	 * The default priority is 80.
	 *
	 * @param robot your robot, which must be a {@link AdvancedRobot}
	 */
	public GunTurnCompleteCondition(AdvancedRobot robot) {
		super();
		this.robot = robot;
	}

	/**
	 * Creates a new GunTurnCompleteCondition with a specific priority.
	 * A condition priority is a value from 0 - 99. The higher value, the
	 * higher priority. The default priority is 80.
	 *
	 * @param robot your robot, which must be a {@link AdvancedRobot}
	 * @param priority the priority of this condition
	 * 
	 * @see Condition#setPriority
	 */
	public GunTurnCompleteCondition(AdvancedRobot robot, int priority) {
		super();
		this.robot = robot;
		this.priority = priority;
	}

	/**
	 * Tests if the gun has stopped turning.
	 *
	 * @return {@code true} if the gun has stopped turning; {@code false}
	 *    otherwise
	 */
	@Override
	public boolean test() {
		return (robot.getGunTurnRemaining() == 0);
	}
}
