/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *******************************************************************************/
package robocode;


/**
 * A prebuilt condition you can use that indicates your robot has finished rotating.
 * @see robocode.Condition
 *
 * @author Mathew A. Nelson
 */
public class TurnCompleteCondition extends Condition {
	private AdvancedRobot robot = null;

	/**
	 * Creates a new TurnCompleteCondition with default priority.
	 */
	public TurnCompleteCondition(AdvancedRobot r) {
		super();
		this.robot = r;
	}

	/**
	 * Creates a new TurnCompleteCondition with the specified priority.
	 */
	public TurnCompleteCondition(AdvancedRobot r, int priority) {
		super();
		this.robot = r;
		this.priority = priority;
	}

	/**
	 * Tests if the robot has finished turning.
	 */
	public boolean test() {
		return (robot.getTurnRemaining() == 0);
	}
}
