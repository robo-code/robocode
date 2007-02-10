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
 *******************************************************************************/
package robocode;


/**
 * A prebuilt condition you can use that indicates your robot has finished moving.
 *
 * @see robocode.Condition
 *
 * @author Mathew A. Nelson (original)
 */
public class MoveCompleteCondition extends Condition {
	private AdvancedRobot robot = null;

	/**
	 * Creates a new MoveCompleteCondition with default priority.
	 */
	public MoveCompleteCondition(AdvancedRobot r) {
		super();
		this.robot = r;
	}

	/**
	 * Creates a new MoveCompleteCondition with the specified priority.
	 */
	public MoveCompleteCondition(AdvancedRobot r, int priority) {
		super();
		this.robot = r;
		this.priority = priority;
	}

	/**
	 * Tests if the robot has stopped moving.
	 */
	@Override
	public boolean test() {
		return (robot.getDistanceRemaining() == 0);
	}
}
