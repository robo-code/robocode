/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.peer.robot;

import java.io.*;
import java.util.*;
import robocode.*;
import robocode.io.*;
import robocode.peer.*;
import robocode.util.Utils;

public class RobotMessageManager {
	
	//TeamMessageSizer sizer = new TeamMessageSizer();
 	//ObjectOutputStream out = null;
 	RobotPeer robotPeer = null;
 	Vector messageEvents = new Vector();
 	
 	
 	ObjectOutputStream out;
 	ObjectInputStream in;
 	
 	public RobotMessageManager(RobotPeer robotPeer)
 	{
 		this.robotPeer = robotPeer;
 		try {
 			BufferedPipedOutputStream bufOut = new BufferedPipedOutputStream(32768,false,false);
 			out = new ObjectOutputStream(bufOut);
 			in = new RobocodeObjectInputStream(bufOut.getInputStream(),robotPeer.getRobotClassManager().getRobotClassLoader());
 		} catch (IOException e) {
 			robotPeer.out.println("Unable to initialize team message service.");
 		}
 	}
	
	public void sendMessage(String name, Serializable o) throws IOException
	{
		if (robotPeer.getRobotClassManager().getTeamManager() == null)
		{
			throw new IOException("Unable to send message, you are not on a team.");
		}
		
		TeamPeer teamPeer = robotPeer.getRobotClassManager().getTeamManager();
		for (int i = 0; i < teamPeer.size(); i++)
		{
			RobotPeer receiver = teamPeer.elementAt(i);
			if (!receiver.isDead())
			{
				if (name == null ||
					(receiver.getName().length() >= name.length() && receiver.getName().substring(0,name.length()).equals(name))
				    ||
					(receiver.getNonVersionedName().length() >= name.length() && receiver.getNonVersionedName().substring(0,name.length()).equals(name))
				)
				{
					if (name == null && receiver == robotPeer)
						continue;
					synchronized(receiver.getMessageManager().out)
					{
						receiver.getMessageManager().out.writeObject(o);
						try {
						} catch (Exception e) {
							System.out.println(e);
						}
						try {
							receiver.getMessageManager().addMessage(robotPeer.getName(),(Serializable)receiver.getMessageManager().in.readObject());
						} catch (ClassNotFoundException e) {
							System.out.println("Unable to send: " + e);
						}
					}
				}
			}
			//	throw new IOException("Teammate: " + receiver.getName() + " is dead.");
		}
		// Note:  Does nothing, simply throws IOException if too many bytes.
	}
	
	public synchronized void addMessage(String sender, Serializable o) {
		if (!robotPeer.isDead())
		{
			messageEvents.add(new MessageEvent(sender,o));
			//System.out.println("I have a new message at time: " + robotPeer.getTime());
		}
//		else
//			throw new RobotException(robotPeer.getName() + " is dead.");
	}
	
	public Vector getMessageEvents() {
		return messageEvents;
	}
	
	public void clearMessageEvents() {
		messageEvents.clear();
	}
	
	private void log(String s) {
		Utils.log(s);
	}
	
}

