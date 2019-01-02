/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import net.sf.robocode.peer.IRobotStatics;
import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;


/**
 * This event is sent to {@link Robot#onStatus(StatusEvent) onStatus()} every
 * turn in a battle to provide the status of the robot.
 *
 * @author Flemming N. Larsen (original)
 *
 * @since 1.5
 */
public final class StatusEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 99;

	private final RobotStatus status;

	/**
	 * This constructor is called internally from the game in order to create
	 * a new {@link RobotStatus}.
	 *
	 * @param status the current states
	 */
	public StatusEvent(RobotStatus status) {
		super();

		this.status = status;
	}

	/**
	 * Returns the {@link RobotStatus} at the time defined by {@link Robot#getTime()}.
	 *
	 * @return the {@link RobotStatus} at the time defined by {@link Robot#getTime()}.
	 * @see #getTime()
	 */
	public RobotStatus getStatus() {
		return status;
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
	final void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
		IBasicEvents listener = robot.getBasicEventListener();

		if (listener != null) {
			listener.onStatus(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		throw new Error("Serialization of this type is not supported");
	}
}
