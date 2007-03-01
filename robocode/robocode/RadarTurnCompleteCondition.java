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
 * A prebuilt condition you can use that indicates your radar has finished
 * turning.
 *
 * @see Condition
 *
 * @author Mathew A. Nelson (original)
 */
public class RadarTurnCompleteCondition extends Condition {
	private AdvancedRobot robot;

	/**
	 * Creates a new RadarTurnCompleteCondition with default priority.
	 * The default priority is 80.
	 *
	 * @param robot your robot, which must be a {@link AdvancedRobot}
	 */
	public RadarTurnCompleteCondition(AdvancedRobot robot) {
		super();
		this.robot = robot;
	}

	/**
	 * Creates a new RadarTurnCompleteCondition with the specified priority.
	 * A condition priority is a value from 0 - 99. The higher value, the
	 * higher priority. The default priority is 80.
	 *
	 * @param robot your robot, which must be a {@link AdvancedRobot}
	 * @param priority the priority of this condition
	 * 
	 * @see Condition#setPriority
	 */
	public RadarTurnCompleteCondition(AdvancedRobot robot, int priority) {
		super();
		this.robot = robot;
		this.priority = priority;
	}

	/**
	 * Tests if the radar has stopped turning.
	 *
	 * @return {@code true} if the radar has stopped turning; {@code false}
	 *    otherwise
	 */
	@Override
	public boolean test() {
		return (robot.getRadarTurnRemaining() == 0);
	}
}
