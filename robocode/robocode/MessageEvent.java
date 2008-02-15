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


import java.io.Serializable;


/**
 * A MessageEvent is sent to {@link TeamRobot#onMessageReceived(MessageEvent)
 * onMessageReceived} when a teammate sends a message to your robot.
 * You can use the information contained in this event to determine what to do.
 *
 * @author Mathew A. Nelson (original)
 */
public class MessageEvent extends Event {
	private String sender;
	private Serializable message;

	/**
	 * Called by the game to create a new MessageEvent.
	 * 
	 * @param sender the name of the sending robot
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
}