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
 *     - Code cleanup
 *     - Ported to Java 5.0
 *     - Removed potential NullPointerException in sendMessage()
 *     - Changed sendMessage() so it does not throw an IOException anymore when
 *       the robot is not in a team battle
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.peer.robot;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import robocode.MessageEvent;
import robocode.io.BufferedPipedOutputStream;
import robocode.io.RobocodeObjectInputStream;
import robocode.peer.RobotPeer;
import robocode.peer.TeamPeer;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobotMessageManager {

	private RobotPeer robotPeer;
	private List<MessageEvent> messageEvents = Collections.synchronizedList(new ArrayList<MessageEvent>());

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
		TeamPeer teamPeer = robotPeer.getRobotClassManager().getTeamManager();

		if (teamPeer == null) {
			return;
		}

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

					if (robotMsgMan != null) {
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
		}
		// Note:  Does nothing, simply throws IOException if too many bytes.
	}

	public void addMessage(String sender, Serializable o) {
		if (robotPeer.isAlive()) {
			messageEvents.add(new MessageEvent(sender, o));
		}
	}

	public List<MessageEvent> getMessageEvents() {
		return messageEvents;
	}

	public void clearMessageEvents() {
		messageEvents.clear();
	}
}
