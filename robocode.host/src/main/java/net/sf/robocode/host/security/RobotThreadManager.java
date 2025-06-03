/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;


import net.sf.robocode.host.IHostedThread;
import net.sf.robocode.host.IThreadManager;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.RobocodeProperties;
import robocode.exception.RobotException;

import static net.sf.robocode.io.Logger.logError;
import static net.sf.robocode.io.Logger.logMessage;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 */
public class RobotThreadManager {

	private final IHostedThread robotProxy;
	private Thread runThread;
	private ThreadGroup runThreadGroup;
	private boolean awtInitialized = false;

	public RobotThreadManager(IHostedThread robotProxy) {
		this.robotProxy = robotProxy;
		createThreadGroup();
	}

	public void cleanup() {
		try {
			if (runThread == null || !runThread.isAlive()) {
				if (!discardAWT()) {
					runThreadGroup.destroy();
				}
			} else {
				Logger.logWarning("Could not destroy " + runThread.getName());
			}
		} catch (Exception e) {
			Logger.logError("Could not destroy " + runThreadGroup.getName(), e);
		}
	}

	public void initAWT() {
		if (!awtInitialized) {
			awtInitialized = true;
			// Nothing to do as AppContext is no longer used
			// Java 24 has removed this internal API
		}
	}

	private boolean discardAWT() {
		boolean res = false;

		if (awtInitialized) {
			// Nothing to do as AppContext is no longer used
			// Java 24 has removed this internal API
			awtInitialized = false;
			res = true;
		}
		return res;
	}

	public void checkRunThread() {
		if (Thread.currentThread() != runThread) {
			throw new RobotException("You cannot take action in this thread!");
		}
	}

	public void start(IThreadManager threadManager) {
		try {
			threadManager.addThreadGroup(runThreadGroup, robotProxy);

			runThread = new Thread(runThreadGroup, robotProxy, robotProxy.getStatics().getName());
			runThread.setDaemon(true);
			runThread.setPriority(Thread.NORM_PRIORITY - 1);
			runThread.setContextClassLoader(this.robotProxy.getRobotClassloader());
			runThread.start();
		} catch (Exception e) {
			logError("Exception starting thread", e);
		}
	}

	/**
	 * @return true as a peaceful stop
	 */
	public boolean waitForStop() {
		boolean isAlive = false;

		if (runThread != null && runThread.isAlive()) {
			runThread.interrupt();
			waitForStop(runThread);
			isAlive = runThread.isAlive();
		}

		Thread[] threads = new Thread[100];

		runThreadGroup.enumerate(threads);

		for (Thread thread : threads) {
			if (thread != null && thread != runThread && thread.isAlive()) {
				thread.interrupt();
				waitForStop(thread);
				isAlive |= thread.isAlive();
			}
		}

		if (isAlive) {
			if (RobocodeProperties.isSecurityOn()) {
				logError("Robot " + robotProxy.getStatics().getName() + " is not stopping.  Forcing a stop.");

				// Force the robot to stop
				return forceStop();
			} else {
				logError(
						"Robot " + robotProxy.getStatics().getName()
						+ " is still running.  Not stopping it because security is off.");
			}
		}

		return true;
	}

	/**
	 * @return true as a peaceful stop
	 */
	public boolean forceStop() {
		int res = stopSteps(runThread);

		Thread[] threads = new Thread[100];

		runThreadGroup.enumerate(threads);

		for (Thread thread : threads) {
			if (thread != null && thread != runThread && thread.isAlive()) {
				res += stopSteps(thread);
			}
		}
		if (res > 0) {
			robotProxy.println("SYSTEM: This robot has been stopped.  No score will be generated.");

			// recycle thread group
			createThreadGroup();
		}
		runThread = null;
		return res == 0;
	}

	/**
	 * @param t thread to stop
	 * @return 0 as a peaceful stop
	 */
	private int stopSteps(Thread t) {
		if (t != null && t.isAlive()) {
			interrupt(t);
			if (t.isAlive()) {
				stop(t);
			}
			if (t.isAlive()) {
				// t.suspend();
				logError("Unable to stop thread: " + runThread.getName());
			} else {
				logMessage(robotProxy.getStatics().getName() + " has been stopped.");
			}
			return 1;
		}
		return 0;
	}

	@SuppressWarnings("deprecation")
	private void stop(Thread t) {
		if (t != null) {
			// noinspection deprecation
			t.stop();
			try {
				t.join(1500);
			} catch (InterruptedException e) {
				// Immediately reasserts the exception by interrupting the caller thread itself
				Thread.currentThread().interrupt();
			}
		}
	}

	private void interrupt(Thread t) {
		if (t != null) {
			try {
				t.setPriority(Thread.MIN_PRIORITY);
			} catch (NullPointerException e) {
				logError("Sometimes this occurs in the Java core?!", e);
			}
			t.interrupt();
			try {
				t.join(500);
			} catch (InterruptedException e) {
				// Immediately reasserts the exception by interrupting the caller thread itself
				Thread.currentThread().interrupt();
			}
		}
	}

	private void waitForStop(Thread thread) {
		for (int j = 0; j < 100 && thread.isAlive(); j++) {
			if (j == 50) {
				logMessage(
						"Waiting for robot " + robotProxy.getStatics().getName() + " to stop thread " + thread.getName());
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// Immediately reasserts the exception by interrupting the caller thread itself
				Thread.currentThread().interrupt();
				break; // We are in a loop
			}
		}
	}

	private void createThreadGroup() {
		runThreadGroup = new ThreadGroup(robotProxy.getStatics().getName());

		// bit lower than battle have
		runThreadGroup.setMaxPriority(Thread.NORM_PRIORITY - 1);
	}
}
