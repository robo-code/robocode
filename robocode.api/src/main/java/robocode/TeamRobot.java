/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import robocode.robotinterfaces.ITeamEvents;
import robocode.robotinterfaces.ITeamRobot;
import robocode.robotinterfaces.peer.ITeamRobotPeer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;


/**
 * An advanced type of robot that supports sending messages between team
 * mates in a robot team.
 * <p>
 * If you have not done already, you should create a {@link Robot} or
 * {@link AdvancedRobot} first.
 *
 * @see JuniorRobot
 * @see Robot
 * @see AdvancedRobot
 * @see RateControlRobot
 * @see Droid
 * @see BorderSentry
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 */
public class TeamRobot extends AdvancedRobot implements ITeamRobot, ITeamEvents {

	/**
	 * Broadcasts a message to all teammates.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void run() {
	 *       broadcastMessage("I'm here!");
	 *   }
	 * </pre>
	 *
	 * @param message the message to broadcast to all teammates
	 * @throws IOException if the message could not be broadcasted to the
	 *                     teammates
	 * @see #isTeammate(String)
	 * @see #getTeammates()
	 * @see #sendMessage(String, Serializable)
	 */
	public void broadcastMessage(Serializable message) throws IOException {
		if (peer != null) {
			((ITeamRobotPeer) peer).broadcastMessage(message);
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
	 *         queue
	 * @see #onMessageReceived(MessageEvent)
	 * @see MessageEvent
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
	 * Do not call this method!
	 * <p>
	 * {@inheritDoc}
	 */
	public final ITeamEvents getTeamEventListener() {
		return this; // this robot is listening
	}

	/**
	 * Returns the names of all teammates, or {@code null} there is no
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
	 * @return a String array containing the names of all your teammates, or
	 *         {@code null} if there is no teammates. The length of the String array
	 *         is equal to the number of teammates.
	 * @see #isTeammate(String)
	 * @see #broadcastMessage(Serializable)
	 * @see #sendMessage(String, Serializable)
	 */
	public String[] getTeammates() {
		if (peer != null) {
			return ((ITeamRobotPeer) peer).getTeammates();
		}
		uninitializedException();
		return null;
	}

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
	 *
	 * @param name the robot name to check
	 * @return {@code true} if the specified name belongs to one of your
	 *         teammates; {@code false} otherwise.
	 * @see #getTeammates()
	 * @see #broadcastMessage(Serializable)
	 * @see #sendMessage(String, Serializable)
	 */
	public boolean isTeammate(String name) {
		if (peer != null) {
			return ((ITeamRobotPeer) peer).isTeammate(name);
		}
		uninitializedException();
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void onMessageReceived(MessageEvent event) {}

	/**
	 * Sends a message to one (or more) teammates.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void run() {
	 *       sendMessage("sample.DroidBot", "I'm here!");
	 *   }
	 * </pre>
	 *
	 * @param name	the name of the intended recipient of the message
	 * @param message the message to send
	 * @throws IOException if the message could not be sent
	 * @see #isTeammate(String)
	 * @see #getTeammates()
	 * @see #broadcastMessage(Serializable)
	 */
	public void sendMessage(String name, Serializable message) throws IOException {
		if (peer != null) {
			((ITeamRobotPeer) peer).sendMessage(name, message);
		} else {
			uninitializedException();
		}
	}
}
