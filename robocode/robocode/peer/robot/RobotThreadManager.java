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
 *     - Updated to use methods from the Logger, which replaces logger methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Moved the stopThread() method from the RobocodeDeprecated class into
 *       this class
 *     - Bugfix: The waitForStop() was using 'runThreadGroup.activeCount > 0'
 *       instead of runThread.isAlive() causing some robots to be forced to stop.
 *       In the same time this method was simplified up updated for faster CPU's   
 *******************************************************************************/
package robocode.peer.robot;


import robocode.exception.RobotException;
import robocode.io.Logger;
import static robocode.io.Logger.logError;
import static robocode.io.Logger.logMessage;
import robocode.manager.IThreadManager;
import robocode.peer.proxies.IHostedThread;
import robocode.security.RobocodeSecurityManager;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobotThreadManager {
	private IHostedThread robotProxy;
	private Thread runThread;
	private ThreadGroup runThreadGroup;
	private boolean awtForThreadGroup =false;

	public RobotThreadManager(IHostedThread robotProxy) {
		this.robotProxy = robotProxy;
		runThreadGroup = new ThreadGroup(robotProxy.getStatics().getName());
		runThreadGroup.setMaxPriority(Thread.NORM_PRIORITY);
	}

	public void initAWT() {
		if (!awtForThreadGroup) {
			RobocodeSecurityManager.createNewAppContext();
			awtForThreadGroup = true;
		}
	}

	public boolean forceStop() {
		int res = stopSteps(runThread);

		Thread[] threads = new Thread[10];

		runThreadGroup.enumerate(threads);

		for (Thread thread : threads) {
			if (thread != runThread) {
				res += stopSteps(thread);
			}
		}
		if (res > 0) {
			robotProxy.println("SYSTEM: This robot has been stopped.  No score will be generated.");
		}
		return res == 0;
	}

	private int stopSteps(Thread t) {
		if (t != null && t.isAlive()) {
			interrupt(t);
			if (t.isAlive()) {
				stop(t);
			}
			if (t.isAlive()) {
				// noinspection deprecation
				t.suspend();
				logError("Warning!  Unable to stop thread: " + runThread.getName() + ", at least suspended the thread");
			} else {
				logMessage(robotProxy.getStatics().getName() + " has been stopped.");
			}
			return 1;
		}
		return 0;
	}

	private void stop(Thread t) {
		// noinspection deprecation
		t.stop();
		try {
			t.join(500);
		} catch (InterruptedException e) {
			// Immediately reasserts the exception by interrupting the caller thread itself
			Thread.currentThread().interrupt();
		}
	}

	private void interrupt(Thread t) {
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

	public void cleanup() {
		try {
			if (!runThread.isAlive()) {
				runThreadGroup.destroy();
			} else {
				Logger.logError("Warning, could not destroy " + runThread.getName());
			}
		} catch (Exception e) {
			Logger.logError("Warning, could not destroy " + runThreadGroup.getName(), e);
		}
	}

	public void start(IThreadManager threadManager) {
		try {
			threadManager.addThreadGroup(runThreadGroup, robotProxy);
			runThread = new Thread(runThreadGroup, robotProxy, robotProxy.getStatics().getName());
			runThread.setDaemon(true);
			runThread.setPriority(Thread.NORM_PRIORITY);
			runThread.start();
		} catch (Exception e) {
			logError("Exception starting thread: " + e);
		}
	}

	public boolean waitForStop() {
		if (runThread == null) {
			return true;
		}

		runThread.interrupt();

		for (int j = 0; j < 100 && runThread.isAlive(); j++) {
			if (j == 50) {
				logError("Waiting for robot " + robotProxy.getStatics().getName() + " to stop");
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// Immediately reasserts the exception by interrupting the caller thread itself
				Thread.currentThread().interrupt();
				break; // We are in a loop
			}
		}

		if (runThread.isAlive()) {
			if (!System.getProperty("NOSECURITY", "false").equals("true")) {
				logError("Robot " + robotProxy.getStatics().getName() + " is not stopping.  Forcing a stop.");
				return forceStop();
			} else {
				logError(
						"Robot " + robotProxy.getStatics().getName()
						+ " is still running.  Not stopping it because security is off.");
			}
		}
		return true;
	}

	public void checkRunThread() {
		if (Thread.currentThread() != runThread) {
			throw new RobotException("You cannot take action in this thread!");
		}
	}
}
