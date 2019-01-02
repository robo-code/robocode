/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.robotinterfaces;


import robocode.MessageEvent;


/**
 * An event interface for receiving robot team events with an
 * {@link ITeamRobot}.
 *
 * @see ITeamRobot
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6
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
	 * @param event the message event sent by the game
	 * @see robocode.MessageEvent
	 * @see robocode.Event
	 */
	void onMessageReceived(MessageEvent event);
}
