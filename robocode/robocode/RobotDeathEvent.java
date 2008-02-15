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
 * This event is sent to {@link Robot#onRobotDeath onRobotDeath} when another
 * robot (not your robot) dies.
 *
 * @author Mathew A. Nelson (original)
 */
public class RobotDeathEvent extends Event {
	private String robotName;

	/**
	 * Called by the game to create a new RobotDeathEvent.
	 * 
	 * @param robotName the name of the robot that died
	 */
	public RobotDeathEvent(String robotName) {
		super();
		this.robotName = robotName;
	}

	/**
	 * Returns the name of the robot that died.
	 *
	 * @return the name of the robot that died
	 */
	public String getName() {
		return robotName;
	}

	/**
	 * @deprecated Use {@link #getName()} instead.
	 */
	@Deprecated
	public String getRobotName() {
		return robotName;
	}
}
