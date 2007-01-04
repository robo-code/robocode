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
 *     - Ported to Java 5.0
 *     - Code cleanup
 *******************************************************************************/
package robocode.peer.robot;


import java.io.*;
import java.util.*;
import robocode.*;
import robocode.io.*;
import robocode.peer.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class RobotMessageManager {
	
	private RobotPeer robotPeer;
	private Vector<MessageEvent> messageEvents = new Vector<MessageEvent>(); 
 	
	private ObjectOutputStream out;
	private ObjectInputStream in;
 	
	public RobotMessageManager(RobotPeer robotPeer) {
		this.robotPeer = robotPeer;
		try {
			BufferedPipedOutputStream bufOut = new BufferedPipedOutputStream(32768, false, false);

			out = new ObjectOutputStream(bufOut);
			in = new RobocodeObjectInputStream(bufOut.getInputStream(),
					robotPeer.getRobotClassManager().getRobotClassLoader());
		} catch (IOException e) {
			robotPeer.out.println("Unable to initialize team message service.");
		}
	}
	
	public void sendMessage(String name, Serializable o) throws IOException {
		if (robotPeer.getRobotClassManager().getTeamManager() == null) {
			throw new IOException("Unable to send message, you are not on a team.");
		}
		
		TeamPeer teamPeer = robotPeer.getRobotClassManager().getTeamManager();

		for (RobotPeer receiver : teamPeer) {
			if (receiver.isAlive()) {
				if (name == null
						|| (receiver.getName().length() >= name.length()
								&& receiver.getName().substring(0, name.length()).equals(name))
								|| (receiver.getNonVersionedName().length() >= name.length()
										&& receiver.getNonVersionedName().substring(0, name.length()).equals(name))) {
					if (name == null && receiver == robotPeer) {
						continue;
					}
					RobotMessageManager robotMsgMan = receiver.getMessageManager();

					synchronized (robotMsgMan.out) {
						robotMsgMan.out.writeObject(o);
						try {
							robotMsgMan.addMessage(robotPeer.getName(), (Serializable) robotMsgMan.in.readObject());
						} catch (ClassNotFoundException e) {
							System.out.println("Unable to send: " + e);
						}
					}
				}
			}
		}
		// Note:  Does nothing, simply throws IOException if too many bytes.
	}
	
	public void addMessage(String sender, Serializable o) {
		if (robotPeer.isAlive()) {
			messageEvents.add(new MessageEvent(sender, o));
		}
	}
	
	public Vector<MessageEvent> getMessageEvents() {
		return messageEvents;
	}
	
	public void clearMessageEvents() {
		messageEvents.clear();
	}
}
