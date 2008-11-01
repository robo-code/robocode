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
 *     - Fixed potential NullPointerException in getLoadingRobotPeer()
 *     - Added getRobotClasses() and getRobotPeers() for the
 *       RobocodeSecurityManager
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.manager;


import robocode.peer.RobotPeer;
import robocode.robotinterfaces.IBasicRobot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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

	public void addThreadGroup(ThreadGroup g, RobotPeer robotPeer) {
		if (!groups.contains(g)) {
			groups.add(g);
			robots.add(robotPeer);
		}
	}

	public synchronized RobotPeer getLoadingRobot() {
		return loadingRobot;
	}

	public synchronized RobotPeer getLoadingRobotPeer(Thread t) {
		if (t != null && robotLoaderThread != null
				&& (t.equals(robotLoaderThread)
				|| (t.getThreadGroup() != null && t.getThreadGroup().equals(robotLoaderThread.getThreadGroup())))) {
			return loadingRobot;
		}
		return null;
	}

	public synchronized RobotPeer getLoadedOrLoadingRobotPeer(Thread t) {
		RobotPeer robotPeer = getRobotPeer(t);

		if (robotPeer == null) {
			robotPeer = getLoadingRobotPeer(t);
		}
		return robotPeer;
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
		return robots.get(index);
	}

	public void reset() {
		groups.clear();
		robots.clear();
	}

	public synchronized void setLoadingRobot(RobotPeer newLoadingRobotPeer) {
		if (robotLoaderThread != null && robotLoaderThread.equals(Thread.currentThread())) {
			loadingRobot = newLoadingRobotPeer;
		}
	}

	public synchronized void setRobotLoaderThread(Thread robotLoaderThread) {
		this.robotLoaderThread = robotLoaderThread;
	}

	public List<Class<?>> getRobotClasses() {
		List<Class<?>> classes = new ArrayList<Class<?>>();

		RobotPeer robotPeer;
		IBasicRobot robot;

		for (int i = robots.size() - 1; i >= 0; i--) {
			robotPeer = robots.get(i);
			if (robotPeer != null) {
                classes.add(robotPeer.getRobotClassManager().getRobotClass());
			}
		}
		return classes;
	}

	public List<RobotPeer> getRobotPeers(Class<?> robotClass) {
		List<RobotPeer> robotPeers = new ArrayList<RobotPeer>();

		for (int i = robots.size() - 1; i >= 0; i--) {
			RobotPeer robotPeer = robots.get(i);

			if (robotPeer != null) {
				// NOTE: The check is on name level, as the equals() method does not work between
				// the two classes, and isAssignableFrom() does not work here either! -FNL
				if (robotPeer.getRobotClassManager().getRobotClass().getName().equals(robotClass.getName())) {
					robotPeers.add(robotPeer);
				}
			}
		}

		return robotPeers;
	}
}
