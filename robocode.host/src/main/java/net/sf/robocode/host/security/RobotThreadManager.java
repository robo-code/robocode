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
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.RobocodeProperties;
import static net.sf.robocode.io.Logger.logError;
import static net.sf.robocode.io.Logger.logMessage;
import static net.sf.robocode.io.Logger.logWarning;
import robocode.exception.RobotException;

import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 */
public class RobotThreadManager {

	private final IHostedThread robotProxy;
	private Thread runThread;
	private ThreadGroup runThreadGroup;
	private Object awtForThreadGroup;
	private final Map<Thread, Disposal> disposeAppContextThreadMap = new HashMap<Thread, Disposal>();

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
		if (awtForThreadGroup == null) {
			awtForThreadGroup = AccessController.doPrivileged(new PrivilegedAction<Object>() {
				public Object run() {
					return createNewAppContext();
				}
			});
		}
	}

	private boolean discardAWT() {
		boolean res = false;

		if (awtForThreadGroup != null && !(awtForThreadGroup instanceof Integer)) {
			res = disposeAppContext(awtForThreadGroup);
			awtForThreadGroup = null;
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
	 * @return true as peaceful stop
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
	 * @return true as peaceful stop
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
	 * @return 0 as peaceful stop
	 */
	private int stopSteps(Thread t) {
		if (t != null && t.isAlive()) {
			interrupt(t);
			if (t.isAlive()) {
				stop(t);
			}
			if (t.isAlive()) {
				// noinspection deprecation
				// t.suspend();
				logWarning("Unable to stop thread: " + runThread.getName());
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

	private Object createNewAppContext() {
		// Add the current thread to our disposeAppContextThreadMap if it does not exit already
		if (!disposeAppContextThreadMap.containsKey(Thread.currentThread())) {
			disposeAppContextThreadMap.put(Thread.currentThread(), new Disposal());
		}

		// same as SunToolkit.createNewAppContext();
		// we can't assume that we are always on Suns JVM, so we can't reference it directly
		// why we call that ? Because SunToolkit is caching AWTQueue instance form main thread group and use it on robots threads
		// and he is not asking us for checkAwtEventQueueAccess above
		try {
			final Class<?> sunToolkit = ClassLoader.getSystemClassLoader().loadClass("sun.awt.SunToolkit");

			// We need to wait for the sun.awt.AppContext.dispose() to complete for the current thread before creating
			// a new AppContext for it. Otherwise the AWT EventQueue fires events like WINDOW_CLOSE to our main window
			// which closes the entire application due to a System.exit()

			Disposal disposal = disposeAppContextThreadMap.get(Thread.currentThread());

			synchronized (disposal) {
				while (disposal.isDisposing) {
					try {
						disposal.wait();
					} catch (InterruptedException e) {
						return -1;
					}
				}
			}	

			// Call sun.awt.SunToolkit.createNewAppContext() to create a new AppContext for the current thread
			return sunToolkit.getDeclaredMethod("createNewAppContext").invoke(null);

		} catch (ClassNotFoundException e) {
			// we are not on sun JVM
			return -1;
		} catch (NoSuchMethodException e) {
			throw new Error("Looks like SunVM but unable to assure secured AWTQueue, sorry", e);
		} catch (InvocationTargetException e) {
			throw new Error("Looks like SunVM but unable to assure secured AWTQueue, sorry", e);
		} catch (IllegalAccessException e) {
			throw new Error("Looks like SunVM but unable to assure secured AWTQueue, sorry", e);
		}
		// end: same as SunToolkit.createNewAppContext();
	}

	private boolean disposeAppContext(final Object appContext) {
		// This method should must not be used when the AWT is running in headless mode!
		// Bugfix [2833271] IllegalThreadStateException with the AWT-Shutdown thread.
		// Read more about headless mode here:
		// http://java.sun.com/developer/technicalArticles/J2SE/Desktop/headless/

		// This check makes sure we exit, if the AWT is running in headless mode
		if (System.getProperty("java.awt.headless", "false").equals("true")) {
			return false;
		}

		// same as AppContext.dispose();
		try {
			final Class<?> sunToolkit = ClassLoader.getSystemClassLoader().loadClass("sun.awt.AppContext");

			// We run this in a thread, as invoking the AppContext.dispose() method sometimes takes several
			// seconds, and thus causes the cleanup of a battle to hang, which is annoying when trying to restart
			// a battle.
			new Thread(new Runnable() {
				public void run() {
					Disposal disposal = disposeAppContextThreadMap.get(Thread.currentThread());

					try {
						// Signal that the AppContext for the current thread is being disposed (start)
						if (disposal != null) {
							synchronized (disposal) {
								disposal.isDisposing = true;
								disposal.notifyAll();
							}
						}
						// Call sun.awt.AppContext.dispose(appContext) to dispose the AppContext for the current thread
						sunToolkit.getDeclaredMethod("dispose").invoke(appContext);	

					} catch (Exception e) {
						logError(e);
					} finally {
						// Signal that the AppContext for the current thread has been disposed (finish)
						if (disposal != null) {
							synchronized (disposal) {
								disposal.isDisposing = false;
								disposal.notifyAll();
							}
						}
					}
				}
			}, "DisposeAppContext").start();

			return true;
		} catch (ClassNotFoundException e) {
			logError(e);			
		}
		return false;
		// end: same as AppContext.dispose();
	}

	private static class Disposal {
		boolean isDisposing;
	}
}
