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


import robocode.io.Logger;
import static robocode.io.Logger.logError;
import static robocode.io.Logger.logMessage;
import robocode.peer.RobotPeer;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class RobotThreadManager {
	private RobotPeer robotPeer;
	private Thread runThread;
	private ThreadGroup runThreadGroup;

	public RobotThreadManager(RobotPeer robotPeer) {
		this.robotPeer = robotPeer;
		runThreadGroup = new ThreadGroup(robotPeer.getName());
		runThreadGroup.setMaxPriority(Thread.NORM_PRIORITY);
	}

	public void forceStop() {
		if (runThread != null && runThread.isAlive()) {
			try {
				runThread.setPriority(Thread.MIN_PRIORITY);
			} catch (NullPointerException e) {// Work-around: Sometimes this occurs in the Java core?!
			}
			runThread.interrupt();
			try {
				runThread.join(5000);
			} catch (InterruptedException e) {
				// Immediately reasserts the exception by interrupting the caller thread itself
				Thread.currentThread().interrupt();

				e.printStackTrace();
			}
			robotPeer.setRunning(false);
			robotPeer.getRobotStatistics().setInactive();
			if (runThread.isAlive()) {
				stopThread(runThread);
			}
			try {
				runThread.join(5000);
			} catch (InterruptedException e) {
				// Immediately reasserts the exception by interrupting the caller thread itself
				Thread.currentThread().interrupt();

				e.printStackTrace();
			}
			if (runThread.isAlive()) {
				logError("Warning!  Unable to stop thread: " + runThread.getName());
			} else {
				robotPeer.getOut().println("SYSTEM: This robot has been stopped.  No score will be generated.");
				logMessage(robotPeer.getName() + " has been stopped.  No score will be generated.");
			}
		}

		Thread[] threads = new Thread[10];
		int numThreads = runThreadGroup.enumerate(threads);

		if (numThreads == 1 && threads[0] == runThread) {
			return;
		}

		if (numThreads != 0) {
			robotPeer.getRobotStatistics().setInactive();
			robotPeer.getOut().println(
					"SYSTEM:  You still have " + numThreads + " running threads.  No score will be generated.");
		}
		for (Thread thread : threads) {
			if (thread != null) {
				thread.setPriority(Thread.MIN_PRIORITY);
				stopThread(thread);
			}
		}
		for (Thread thread : threads) {
			if (thread != null) {
				try {
					thread.join(1000);
				} catch (InterruptedException e) {
					// Immediately reasserts the exception by interrupting the caller thread itself
					Thread.currentThread().interrupt();

					robotPeer.getOut().println("SYSTEM:  Thread: " + thread.getName() + " join interrupted.");
					logError("Thread: " + thread.getName() + " join interrupted.");
				}
				if (thread.isAlive()) {
					logError("Warning! Unable to stop thread: " + thread.getName());
				} else {
					robotPeer.getOut().println("SYSTEM:  Thread: " + thread.getName() + " has been stopped.");
					logError("Thread: " + thread.getName() + " has been stopped.");
				}
			}
		}
	}

	public ThreadGroup getThreadGroup() {
		return runThreadGroup;
	}

	public void cleanup() {
		try {
			runThreadGroup.destroy();
		} catch (Exception e) {
			Logger.logError("Warning, could not destroy " + runThreadGroup.getName(), e);
		}
	}

	public void start() {
		try {
			runThread = new Thread(runThreadGroup, robotPeer, robotPeer.getName());
			runThread.setDaemon(true);
			runThread.setPriority(Thread.NORM_PRIORITY);
			runThread.start();
		} catch (Exception e) {
			logError("Exception starting thread: " + e);
		}
	}

	public void waitForStop() {
		if (runThread == null) {
			return;
		}

		runThread.interrupt();

		for (int j = 0; j < 100 && runThread.isAlive(); j++) {
			if (j == 50) {
				logError("Waiting for robot " + robotPeer.getName() + " to stop");
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
				logError("Robot " + robotPeer.getName() + " is not stopping.  Forcing a stop.");
				forceStop();
			} else {
				logError("Robot " + robotPeer.getName() + " is still running.  Not stopping it because security is off.");
			}
		}
	}

	/**
	 * Gets the runThread.
	 *
	 * @return Returns a Thread
	 */
	public Thread getRunThread() {
		return runThread;
	}

	@SuppressWarnings("deprecation")
	private void stopThread(Thread t) {
		synchronized (runThread) {
			t.stop();
		}
	}
}
