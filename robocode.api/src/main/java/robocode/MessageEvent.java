/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import net.sf.robocode.peer.IRobotStatics;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.ITeamEvents;
import robocode.robotinterfaces.ITeamRobot;

import java.awt.*;
import java.io.Serializable;


/**
 * A MessageEvent is sent to {@link TeamRobot#onMessageReceived(MessageEvent)
 * onMessageReceived()} when a teammate sends a message to your robot.
 * You can use the information contained in this event to determine what to do.
 *
 * @author Mathew A. Nelson (original)
 */
public final class MessageEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 75;

	private final String sender;
	private final Serializable message;

	/**
	 * Called by the game to create a new MessageEvent.
	 *
	 * @param sender  the name of the sending robot
	 * @param message the message for your robot
	 */
	public MessageEvent(String sender, Serializable message) {
		this.sender = sender;
		this.message = message;
	}

	/**
	 * Returns the name of the sending robot.
	 *
	 * @return the name of the sending robot
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * Returns the message itself.
	 *
	 * @return the message
	 */
	public Serializable getMessage() {
		return message;
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
		if (statics.isTeamRobot()) {
			ITeamEvents listener = ((ITeamRobot) robot).getTeamEventListener();

			if (listener != null) {
				listener.onMessageReceived(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		throw new Error("Serialization of event type not supported");
	}
}
