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
 *     - Added missing getMessageEvents()
 *     - Updated Javadoc
 *     - The uninitializedException() method does not need a method name as input
 *       parameter anymore
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
package robocode;


import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

import robocode.robotinterfaces.*;
import robocode.robotinterfaces.peer.*;


/**
 * An advanced type of robot that supports messages between teammates.
 * <p>
 * If you have not already, you should create a {@link Robot} first.
 *
 * @see Robot
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 */
public class TeamRobot extends AdvancedRobot implements ITeamEvents {

	/**
	 * Checks if a given robot name is the name of one of your teammates.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void onScannedRobot(ScannedRobotEvent e) {
	 *       if (isTeammate(e.getName()) {
	 *           return;
	 *       }
	 *       fire(1);
	 *   }
	 * </pre>
	 * @param name the robot name to check
	 */
	public boolean isTeammate(String name) {
		if (peer != null) {
			return ((ITeamRobotPeer) peer).isTeammate(name);
		}
		uninitializedException();
		return false;
	}

	/**
	 * Returns the names of all teammates, or <code>null</code> there is no
	 * teammates.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void run() {
	 *       // Prints out all teammates
	 *       String[] teammates = getTeammates();
	 *       if (teammates != null) {
	 *           for (String member : teammates) {
	 *               out.println(member);
	 *           }
	 *       }
	 *   }
	 * </pre>
	 *
	 * @return a String array containing the names of all your team mates, or
	 *     <code>null</code> if there is no team mates. The length of the String
	 *     array is equal to the number of team mates
	 */
	public String[] getTeammates() {
		if (peer != null) {
			return ((ITeamRobotPeer) peer).getTeammates();
		}
		uninitializedException();
		return null;
	}

	/**
	 * Broadcasts a message to all team mates.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void run() {
	 *       broadcastMessage("I'm here!");
	 *   }
	 * </pre>
	 *
	 * @param message the message to broadcast to all team mates
	 */
	public void broadcastMessage(Serializable message) throws IOException {
		if (peer != null) {
			((ITeamRobotPeer) peer).broadcastMessage(message);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Sends a message to one (or more) team mates.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void run() {
	 *       sendMessage("sample.DroidBot", "I'm here!");
	 *   }
	 * </pre>
	 *
	 * @param name the name of the intended recipient of the message
	 * @param message the message to send
	 */
	public void sendMessage(String name, Serializable message) throws IOException {
		if (peer != null) {
			((ITeamRobotPeer) peer).sendMessage(name, message);
		} else {
			uninitializedException();
		}
	}

	/**
	 * Returns a vector containing all MessageEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (MessageEvent e : getMessageEvents()) {
	 *      // do something with e
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all MessageEvents currently in the robot's
	 *    queue
	 *
	 * @see #onMessageReceived
	 * @see MessageEvent
	 * 
	 * @since 1.2.6
	 */
	public Vector<MessageEvent> getMessageEvents() {
		if (peer != null) {
			return new Vector<MessageEvent>(((ITeamRobotPeer) peer).getMessageEvents());
		}
		uninitializedException();
		return null; // never called
	}

	/**
	 * This method is called when your robot receives a message from a team mate.
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
	 * @see MessageEvent
	 * @see Event
	 */
	public void onMessageReceived(MessageEvent event) {}

	/**
	 * This method is implemented by {@link TeamRobot} in order to receive team
	 * events. This method is called by the game, and should not be used in
	 * robots.
	 *
	 * @since 1.6
	 *
	 * @return listener to team events
	 */
	public final ITeamEvents getTeamEventListener() {
		return this;
	}
}
