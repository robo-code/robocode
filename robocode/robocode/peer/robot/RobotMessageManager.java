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
 *     - Code cleanup
 *     - Ported to Java 5.0
 *     - Removed potential NullPointerException in sendMessage()
 *     - Changed sendMessage() so it does not throw an IOException anymore when
 *       the robot is not in a team battle
 *     - Changed the list of messageEvents to use the CopyOnWriteArrayList that
 *       is fully thread-safe 
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronizet List and HashMap
 *******************************************************************************/
package robocode.peer.robot;


import robocode.MessageEvent;
import robocode.io.BufferedPipedOutputStream;
import robocode.io.RobocodeObjectInputStream;
import robocode.peer.RobotPeer;
import robocode.peer.TeamPeer;
import robocode.peer.views.IRobotRunnableView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobotMessageManager {

	private RobotPeer robotPeer;
    private IRobotRunnableView robotView; 
    private List<MessageEvent> messageEvents = new CopyOnWriteArrayList<MessageEvent>();

	private ObjectOutputStream out;
	private ObjectInputStream in;

    public RobotMessageManager(RobotPeer robotPeer) {
		this.robotPeer = robotPeer;
        robotView=robotPeer.getRobotRunnableView();
        try {
			BufferedPipedOutputStream bufOut = new BufferedPipedOutputStream(32768, false);

			out = new ObjectOutputStream(bufOut);
			in = new RobocodeObjectInputStream(bufOut.getInputStream(),
					robotPeer.getRobotClassManager().getRobotClassLoader());
		} catch (IOException e) {
			robotPeer.getOut().println("Unable to initialize team message service.");
		}
	}

    public void cleanup(){
        if (in!=null)
        {
            try {
                in.close();
            } catch (IOException e) {
                //ignore now
            }
        }
        in=null;
        if (out!=null)
        {
            try {
                out.close();
            } catch (IOException e) {
                //ignore now
            }
        }
        out=null;
        robotView=null;
        robotPeer=null;
        if (messageEvents!=null){
            messageEvents.clear();
        }
        messageEvents=null;
    }

    public void sendMessage(String name, Serializable message) throws IOException {
		TeamPeer teamPeer = robotPeer.getRobotClassManager().getTeamManager();

		if (teamPeer == null) {
			return;
		}

		for (RobotPeer receiver : teamPeer) {
            if (robotView.isAlive()) {
				if (name == null
						|| (robotView.getName().length() >= name.length()
								&& robotView.getName().substring(0, name.length()).equals(name))
								|| (robotView.getNonVersionedName().length() >= name.length()
										&& robotView.getNonVersionedName().substring(0, name.length()).equals(name))) {
					if (name == null && receiver == robotPeer) {
						continue;
					}
					RobotMessageManager robotMsgMan = receiver.getRobotMessageManager();

					if (robotMsgMan != null) {
						synchronized (robotMsgMan.out) {
							robotMsgMan.out.writeObject(message);
							try {
								robotMsgMan.addMessage(robotView.getName(), (Serializable) robotMsgMan.in.readObject());
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
		if (robotView.isAlive()) {
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
