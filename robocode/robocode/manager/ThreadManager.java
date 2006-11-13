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
 *     Flemming N. Larsen
 *     - Changed to have static access for all methods
 *     - Code cleanup
 *******************************************************************************/
package robocode.manager;


import robocode.peer.RobotPeer;
import java.util.Vector;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class ThreadManager {
	
	private static Vector<ThreadGroup> groups = new Vector<ThreadGroup>();
	private static Thread robotLoaderThread;
	private static RobotPeer loadingRobot;
	private static Vector<RobotPeer> robots = new Vector<RobotPeer>(); 

	public static void addThreadGroup(ThreadGroup g, RobotPeer r) {
		if (!groups.contains(g)) {
			groups.add(g);
			robots.add(r);
		}
	}

	public static RobotPeer getLoadingRobot() {
		return loadingRobot;
	}

	public static RobotPeer getLoadingRobotPeer(Thread t) {
		if (robotLoaderThread != null
				&& (t.equals(robotLoaderThread) || t.getThreadGroup().equals(robotLoaderThread.getThreadGroup()))) {
			return loadingRobot;
		}
		return null;
	}

	public static RobotPeer getRobotPeer(Thread t) {
		ThreadGroup g = t.getThreadGroup();

		if (g == null) {
			return null;
		}
		int index = groups.indexOf(g);

		if (index == -1) {
			return null;
		}
		return robots.elementAt(index);
	}

	public static void reset() {
		groups.clear();
		robots.clear();
	}

	public static synchronized void setLoadingRobot(RobotPeer newLoadingRobot) {
		if (robotLoaderThread != null && robotLoaderThread.equals(Thread.currentThread())) {
			loadingRobot = newLoadingRobot;
		}
	}

	public static synchronized void setRobotLoaderThread(Thread thread) {
		robotLoaderThread = thread;
	}
}
