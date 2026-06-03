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

	// Total time to wait for a thread to stop on its own after being interrupted.
	// Generous enough to tolerate CPU starvation on slow/loaded machines (e.g. CI runners),
	// where the target thread may simply not be scheduled rather than being stuck.
	private static final long WAIT_STOP_TOTAL_MS = 3000;
	// Polling granularity while waiting for a thread to stop.
	private static final long WAIT_STOP_POLL_MS = 10;
	// How often to re-assert the interrupt while waiting (a single early interrupt can be
	// missed if the thread is not yet at an interruptible point).
	private static final long REASSERT_INTERRUPT_EVERY_MS = 100;
	// Bounded join used by interrupt()/stop() escalation steps.
	private static final long INTERRUPT_JOIN_MS = 1000;
	private static final long STOP_JOIN_MS = 3000;

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
        Thread thr = runThread; // forceStop() can set to null concurrently
		if (thr != null && thr.isAlive()) {
			thr.interrupt();
			waitForStop(thr);
			isAlive = thr.isAlive();
		}

		Thread[] threads = new Thread[100];

		if (runThreadGroup != null) {
			runThreadGroup.enumerate(threads);
		}

		for (Thread thread : threads) {
			if (thread != null && thread != thr && thread.isAlive()) {
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

	private void stop(Thread t) {
		if (t != null) {
			// noinspection deprecation
			t.interrupt();
			try {
				t.join(STOP_JOIN_MS);
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
				t.join(INTERRUPT_JOIN_MS);
			} catch (InterruptedException e) {
				// Immediately reasserts the exception by interrupting the caller thread itself
				Thread.currentThread().interrupt();
			}
		}
	}

	private void waitForStop(Thread thread) {
		final long maxIters = WAIT_STOP_TOTAL_MS / WAIT_STOP_POLL_MS;
		final long reassertEvery = Math.max(1, REASSERT_INTERRUPT_EVERY_MS / WAIT_STOP_POLL_MS);
		boolean loggedWaiting = false;

		for (long j = 0; j < maxIters && thread.isAlive(); j++) {
			if (!loggedWaiting && j >= maxIters / 2) {
				loggedWaiting = true;
				logMessage(
						"Waiting for robot " + robotProxy.getStatics().getName() + " to stop thread " + thread.getName());
			}
			// Re-assert the interrupt periodically in case the thread had not yet reached
			// an interruptible point when it was first interrupted.
			if (j % reassertEvery == 0) {
				thread.interrupt();
			}
			try {
				Thread.sleep(WAIT_STOP_POLL_MS);
				// Give the target thread a chance to be scheduled, especially when the CPU is
				// contended and time passes without the thread making progress.
				Thread.yield();
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
