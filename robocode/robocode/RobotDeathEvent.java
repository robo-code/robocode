/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Javadocs
 *******************************************************************************/
package robocode;


import robocode.peer.RobotStatics;
import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;


/**
 * This event is sent to {@link Robot#onRobotDeath(RobotDeathEvent) onRobotDeath()}
 * when another robot (not your robot) dies.
 *
 * @author Mathew A. Nelson (original)
 */
public final class RobotDeathEvent extends Event {
	private final static int DEFAULT_PRIORITY = 70;

	private final String robotName;

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
	 * @return the name of the robot that died
	 * @deprecated Use {@link #getName()} instead.
	 */
	@Deprecated
	public String getRobotName() {
		return robotName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final int getDefaultPriority() {
		return DEFAULT_PRIORITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void dispatch(IBasicRobot robot, RobotStatics statics, Graphics2D graphics) {
		IBasicEvents listener = robot.getBasicEventListener();

		if (listener != null) {
			listener.onRobotDeath(this);
		}
	}
}
