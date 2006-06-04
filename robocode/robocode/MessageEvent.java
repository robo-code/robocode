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


import java.io.*;


/**
 * A MessageEvent is sent to {@link robocode.TeamRobot#onMessageReceived} when a teammate sends you a message.
 * You can use the information contained in this event to determine what to do.
 *
 * @author Mathew A. Nelson
 */
public class MessageEvent extends Event {
	private String sender;
	private Serializable message;
	
	/**
	 * Called by the game to create a new MessageEvent.
	 */
	public MessageEvent(String sender, Serializable message) {
		this.sender = sender;
		this.message = message;
	}
	
	/**
	 * Returns the name of the sending robot
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
