/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.peer.robot;


import java.io.Serializable;


/**
 * @author Pavel Savara (original)
 */
public class TeamMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	public TeamMessage(String sender, String recipient, byte[] message) {
		this.sender = sender;
		this.recipient = recipient;
		this.message = message;

	}

	public String sender;
	public String recipient;
	public byte[] message;
}
