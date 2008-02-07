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
package robocode.robotinterfaces;

import robocode.MessageEvent;


/**
 * @author Pavel Savara (original)
 */
public interface ITeamEvents {
	/**
	 * This method is called when your robot receives a message from a teammate.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void onMessageReceived(MessageEvent event) {
	 *       out.println(event.getSender() + " sent me: " + event.getMessage());
	 *   }
	 * </pre>
	 *
	 * @param event the event sent by the game
	 *
	 * @see robocode.MessageEvent
	 * @see robocode.Event
	 */
	void onMessageReceived(MessageEvent event);

}
