/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode;


/**
 * This event is sent to {@link robocode.Robot#onRobotDeath onRobotDeath}
 * when another robot (not you) dies.
 */
public class RobotDeathEvent extends Event {
	private java.lang.String robotName;

	/**
	 * Called by the game to create a new RobotDeathEvent.
	 */
	public RobotDeathEvent(String robotName) {
		super();
		this.robotName = robotName;
	}

	/**
	 * Returns the name of the robot that died.
	 * @return the name of the robot that died.
	 */
	public java.lang.String getName() {
		return robotName;
	}

	/**
	 * @deprecated use getName
	 */
	public java.lang.String getRobotName() {
		return robotName;
	}
}
