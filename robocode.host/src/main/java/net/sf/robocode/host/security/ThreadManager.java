/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;


import net.sf.robocode.host.IHostedThread;
import net.sf.robocode.host.IThreadManager;
import net.sf.robocode.host.io.RobotFileOutputStream;
import net.sf.robocode.host.io.RobotFileSystemManager;
import robocode.exception.RobotException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.security.AccessController;
import java.security.PrivilegedAction;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class ThreadManager implements IThreadManager {

	private final PrintStream syserr = System.err;

	private final List<Thread> safeThreads = new CopyOnWriteArrayList<Thread>();
	private final List<ThreadGroup> safeThreadGroups = new CopyOnWriteArrayList<ThreadGroup>();
	private final List<ThreadGroup> groups = new CopyOnWriteArrayList<ThreadGroup>();
	private final List<Thread> outputStreamThreads = new CopyOnWriteArrayList<Thread>();
	private final List<IHostedThread> robots = new CopyOnWriteArrayList<IHostedThread>();

	private Thread robotLoaderThread;
	private IHostedThread loadingRobot;

	public ThreadManager() {}

	public void addSafeThread(Thread safeThread) {
		safeThreads.add(safeThread);
	}

	public void removeSafeThread(Thread safeThread) {
		safeThreads.remove(safeThread);
	}

	public void addSafeThreadGroup(ThreadGroup safeThreadGroup) {
		safeThreadGroups.add(safeThreadGroup);
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
		if (newLoadingRobotProxy == null) {
			this.robotLoaderThread = null;
			loadingRobot = null;
		} else {
			this.robotLoaderThread = Thread.currentThread();
			loadingRobot = newLoadingRobotProxy;
		}
	}

	public boolean isSafeThread() {
		return isSafeThread(Thread.currentThread());
	}

	public FileOutputStream createRobotFileStream(String fileName, boolean append) throws IOException {
		final Thread c = Thread.currentThread();

		final IHostedThread robotProxy = getRobotProxy(c);

		if (robotProxy == null) {
			syserr.println("RobotProxy is null");
			return null;
		}
		if (!robotProxy.getStatics().isAdvancedRobot()) {
			throw new RobotException("Only advanced robots could create files");
		}
		
		final File dir = robotProxy.getRobotFileSystemManager().getWritableDirectory();

		if (!dir.exists()) {
			robotProxy.println("SYSTEM: Creating a data directory for you.");
			AccessController.doPrivileged(new PrivilegedAction<Object>() {
				public Object run() {
					outputStreamThreads.add(c);
					if (!dir.exists() && !dir.mkdirs()) {
						syserr.println("Can't create dir " + dir.toString());
					}
					return null;
				}
			});
		}

		final RobotFileSystemManager fileSystemManager = robotProxy.getRobotFileSystemManager();

		File f = new File(fileName);
		long len;

		if (f.exists()) {
			len = f.length();
		} else {
			fileSystemManager.checkQuota();
			len = 0;
		}

		if (!append) {
			fileSystemManager.adjustQuota(-len);
		}

		outputStreamThreads.add(c);
		return new RobotFileOutputStream(fileName, append, fileSystemManager);
	}

	public boolean checkRobotFileStream() {
		final Thread c = Thread.currentThread();

		synchronized (outputStreamThreads) {
			if (outputStreamThreads.contains(c)) {
				outputStreamThreads.remove(c);
				return true;
			}
		}
		return false;
	}

	public boolean isSafeThread(Thread c) {
		try {
			if (safeThreads.contains(c)) {
				return true;
			}

			for (ThreadGroup tg : safeThreadGroups) {
				if (c.getThreadGroup() == tg) {
					safeThreads.add(c);
					return true;
				}
			}

			return false;
		} catch (Exception e) {
			syserr.println("Exception checking safe thread: ");
			e.printStackTrace(syserr);
			return false;
		}
	}

	public PrintStream getRobotOutputStream() {
		Thread c = Thread.currentThread();

		if (isSafeThread(c)) {
			return null;
		}

		IHostedThread robotProxy = getLoadedOrLoadingRobotProxy(c);

		return (robotProxy != null) ? robotProxy.getOut() : null;
	}
}
