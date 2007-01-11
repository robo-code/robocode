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
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.manager;


import robocode.peer.RobotPeer;
import java.util.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class ThreadManager {

	private List<ThreadGroup> groups = Collections.synchronizedList(new ArrayList<ThreadGroup>());
	private Thread robotLoaderThread;
	private RobotPeer loadingRobot;
	private List<RobotPeer> robots = Collections.synchronizedList(new ArrayList<RobotPeer>()); 

	public ThreadManager() {
		super();
	}

	public void addThreadGroup(ThreadGroup g, RobotPeer r) {
		if (!groups.contains(g)) {
			groups.add(g);
			robots.add(r);
		}
	}

	public RobotPeer getLoadingRobot() {
		return loadingRobot;
	}

	public RobotPeer getLoadingRobotPeer(Thread t) {
		if (robotLoaderThread != null
				&& (t.equals(robotLoaderThread) || t.getThreadGroup().equals(robotLoaderThread.getThreadGroup()))) {
			return loadingRobot;
		}
		return null;
	}

	public RobotPeer getRobotPeer(Thread t) {
		ThreadGroup g = t.getThreadGroup();

		if (g == null) {
			return null;
		}
		int index = groups.indexOf(g);

		if (index == -1) {
			return null;
		}
		return (RobotPeer) robots.get(index);
	}

	public void reset() {
		groups.clear();
		robots.clear();
	}

	public synchronized void setLoadingRobot(RobotPeer newLoadingRobot) {
		if (robotLoaderThread != null && robotLoaderThread.equals(Thread.currentThread())) {
			loadingRobot = newLoadingRobot;
		}
	}

	public synchronized void setRobotLoaderThread(Thread robotLoaderThread) {
		this.robotLoaderThread = robotLoaderThread;
	}
}
