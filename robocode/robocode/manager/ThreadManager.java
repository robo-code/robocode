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
package robocode.manager;

import robocode.peer.RobotPeer;
import java.util.Vector;
/**
 * Insert the type's description here.
 * Creation date: (9/24/2001 3:08:10 PM)
 * @author: Administrator
 */
public class ThreadManager {
	Vector groups = new Vector();
	Thread robotLoaderThread = null;
	private RobotPeer loadingRobot = null;
	Vector robots = new Vector();
	RobocodeManager manager = null;
/**
 * ThreadManager constructor comment.
 */
public ThreadManager(RobocodeManager manager) {
	super();
	this.manager = manager;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 3:17:51 PM)
 * @param g java.lang.ThreadGroup
 * @param r robocode.peer.RobotPeer
 */
public void addThreadGroup(ThreadGroup g, robocode.peer.RobotPeer r) {
	if (!groups.contains(g))
	{
		groups.add(g);
		robots.add(r);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 11:37:55 PM)
 * @return robocode.peer.RobotPeer
 */
public synchronized robocode.peer.RobotPeer getLoadingRobot() {
	return loadingRobot;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 3:19:46 PM)
 * @param t java.lang.Thread
 */
public RobotPeer getLoadingRobotPeer(Thread t) {
	if (robotLoaderThread != null && (
		t.equals(robotLoaderThread)  ||
		t.getThreadGroup().equals(robotLoaderThread.getThreadGroup())
		))
		return loadingRobot;
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 3:19:46 PM)
 * @param t java.lang.Thread
 */
public RobotPeer getRobotPeer(Thread t) {
	ThreadGroup g = t.getThreadGroup();
	if (g == null)
		return null;
	int index = groups.indexOf(g);
	if (index == -1)
		return null;
	return (RobotPeer)robots.elementAt(index);
	
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 3:17:22 PM)
 */
public void reset() {
	groups.clear();
	robots.clear();
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 11:37:55 PM)
 * @param newLoadingRobot robocode.peer.RobotPeer
 */
public synchronized void setLoadingRobot(robocode.peer.RobotPeer newLoadingRobot) {
	if (robotLoaderThread != null && robotLoaderThread.equals(Thread.currentThread()))
	{
		loadingRobot = newLoadingRobot;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2001 11:37:06 PM)
 * @param robotLoaderThread java.lang.Thread
 */
public void setRobotLoaderThread(Thread robotLoaderThread) {
	this.robotLoaderThread = robotLoaderThread;
}
}
