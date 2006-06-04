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
import robocode.peer.RobotPeer;


/**
 * An advanced type of robot that supports messages between teammates.
 * <P>If you have not already, you should create a {@link robocode.Robot Robot} first.
 *
 * @see robocode.Robot
 *
 * @author Mathew A. Nelson
 */
public class TeamRobot extends AdvancedRobot {
	
	/**
	 * Checks if a given robot name is one of your teammates.
	 * 
	 * <P>Example
	 * <PRE>
	 *   public void onScannedRobot(ScannedRobotEvent e)
	 *   {
	 *       if (isTeammate(e.getName())
	 *           return;
	 *       else
	 *           fire(1);
	 *   }
	 * </PRE>
	 * @param name The name to check
	 */
	public boolean isTeammate(String name) {
		if (peer != null) {
			peer.getCall();
			if (peer.getTeamPeer() == null) {
				return false;
			}
			return peer.getTeamPeer().contains(name);
		} else {
			uninitializedException("isTeammate");
			return false;
		}
	}
	
	/**
	 * Returns an array of your teammates names
	 * 
	 * <P>Example
	 * <PRE>
	 *   public void run()
	 *   {
	 *       String[] teammates = getTeammates();
	 *       for (int i = 0; i < teammates.length; i++)
	 *           System.out.println(teammates[i]);
	 *   }
	 * </PRE>
	 * 
	 * @return String array containing teammate names
	 */
	public String[] getTeammates() {
		if (peer != null) {
			peer.getCall();
			robocode.peer.TeamPeer teamPeer = peer.getTeamPeer();

			if (teamPeer == null) {
				return null;
			}
			String s[] = new String[teamPeer.size() - 1 ];
			int j = 0;

			for (int i = 0; i < teamPeer.size(); i++) {
				RobotPeer teammate = teamPeer.elementAt(i);

				if (teammate != peer) {
					s[j] = teamPeer.elementAt(i).getName();
					j++;
				}
				// UniqueFullClassNameWithVersion();
			}
			return s;
		} else {
			uninitializedException("getTeammates");
			return null;
		}
	}
	
	/**
	 * Broadcasts a message to all teammates
	 * 
	 * <P>Example
	 * <PRE>
	 *   public void run()
	 *   {
	 *       broadcastMessage("I'm here!");
	 *   }
	 * </PRE>
	 * 
	 * @param message The message to broadcast
	 */
	public void broadcastMessage(Serializable message) throws IOException {
		if (peer != null) {
			peer.setCall();
			if (peer.getMessageManager() == null) {
				throw new IOException("You are not on a team.");
			}
			peer.getMessageManager().sendMessage(null, message);
		} else {
			uninitializedException("broadcastMessage");
		}
	}
	
	/**
	 * Sends a message to one (or more) teammates
	 * 
	 * <P>Example
	 * <PRE>
	 *   public void run()
	 *   {
	 *       sendMessage("sample.DroidBot","I'm here!");
	 *   }
	 * </PRE>
	 * @param name The intended recipient of the message
	 * @param message The message to broadcast
	 */
	public void sendMessage(String name, Serializable message) throws IOException {
		if (peer != null) {
			peer.setCall();
			if (peer.getMessageManager() == null) {
				throw new IOException("You are not on a team.");
			}
			peer.getMessageManager().sendMessage(name, message);
		} else {
			uninitializedException("sendMessage");
		}
	}

	/**
	 * This method will be called when your robot receives a message from a teammate.
	 * You should override it in your robot if you want to be informed of this event.
	 *
	 * <P>Example
	 * <PRE>
	 *   public void onMessageReceived(MessageEvent event) {
	 *     out.println(event.getSender() + " sent me: " + event.getMessage());
	 *   }
	 * </PRE>
	 *   
	 * @param event The event set by the game
	 * @see robocode.MessageEvent
	 * @see robocode.Event
	 */
	public void onMessageReceived(MessageEvent event) {}
}
