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
 *     - Fixed potential NullPointerException in getLoadingRobotProxy()
 *     - Added getRobotClasses() and getRobotProxies() for the
 *       RobocodeSecurityManager
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.manager;


import robocode.peer.proxies.IHostedThread;

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
	private IHostedThread loadingRobot;
	private List<IHostedThread> robots = Collections.synchronizedList(new ArrayList<IHostedThread>());

	public ThreadManager() {
		super();
	}

	public void addThreadGroup(ThreadGroup g, IHostedThread robotProxy) {
		if (!groups.contains(g)) {
			groups.add(g);
			robots.add(robotProxy);
		}
	}

	public synchronized IHostedThread getLoadingRobot() {
		return loadingRobot;
	}

	public synchronized IHostedThread getLoadingRobotProxy(Thread t) {
		if (t != null && robotLoaderThread != null
				&& (t.equals(robotLoaderThread)
				|| (t.getThreadGroup() != null && t.getThreadGroup().equals(robotLoaderThread.getThreadGroup())))) {
			return loadingRobot;
		}
		return null;
	}

	public synchronized IHostedThread getLoadedOrLoadingRobotProxy(Thread t) {
		IHostedThread robotProxy = getRobotProxy(t);

		if (robotProxy == null) {
			robotProxy = getLoadingRobotProxy(t);
		}
		return robotProxy;
	}

	public IHostedThread getRobotProxy(Thread t) {
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

	public synchronized void setLoadingRobot(IHostedThread newLoadingRobotProxy) {
		if (robotLoaderThread != null && robotLoaderThread.equals(Thread.currentThread())) {
			loadingRobot = newLoadingRobotProxy;
		}
	}

	public synchronized void setRobotLoaderThread(Thread robotLoaderThread) {
		this.robotLoaderThread = robotLoaderThread;
	}

	public List<Class<?>> getRobotClasses() {
		List<Class<?>> classes = new ArrayList<Class<?>>();

		IHostedThread robotProxy;

		for (int i = robots.size() - 1; i >= 0; i--) {
			robotProxy = robots.get(i);
			if (robotProxy != null) {
				classes.add(robotProxy.getRobotClass());
			}
		}
		return classes;
	}

	public List<IHostedThread> getRobotProxies(Class<?> robotClass) {
		List<IHostedThread> robotProxies = new ArrayList<IHostedThread>();

		for (int i = robots.size() - 1; i >= 0; i--) {
			IHostedThread robotProxy = robots.get(i);

			if (robotProxy != null) {
				// NOTE: The check is on name level, as the equals() method does not work between
				// the two classes, and isAssignableFrom() does not work here either! -FNL
				if (robotProxy.getRobotClass().getName().equals(robotClass.getName())) {
					robotProxies.add(robotProxy);
				}
			}
		}

		return robotProxies;
	}
}
