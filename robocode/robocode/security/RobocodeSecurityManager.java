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
 *     - Added checkPackageAccess() to limit access to the robocode.util Robocode
 *       package only
 *     - Ported to Java 5.0
 *     - Removed unnecessary method synchronization
 *     - Fixed potential NullPointerException in getFileOutputStream()
 *     - Added setStatus()
 *     - Fixed synchronization issue with accessing battleThread
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *     - we create safe AWT queue for robot's thread group
 *******************************************************************************/
package robocode.security;


import robocode.RobocodeFileOutputStream;
import robocode.io.RobocodeObjectInputStream;
import robocode.manager.IThreadManager;
import robocode.peer.BulletCommand;
import robocode.peer.BulletState;
import robocode.peer.ExecResult;
import robocode.peer.proxies.IHostedThread;
import robocode.peer.robot.RobotFileSystemManager;
import robocode.peer.robot.TeamMessage;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlException;
import java.security.Permission;
import java.util.*;
import java.util.List;
import java.awt.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Pavel Savara (contributor)
 */
public class RobocodeSecurityManager extends SecurityManager {
	private final PrintStream syserr = System.err;

	private final IThreadManager threadManager;
	private final Object safeSecurityContext;
	private final boolean enabled;
	private final boolean experimental;

	private final Map<Thread, RobocodeFileOutputStream> outputStreamThreads = Collections.synchronizedMap(
			new HashMap<Thread, RobocodeFileOutputStream>());
	private final List<Thread> safeThreads = Collections.synchronizedList(new ArrayList<Thread>());
	private final List<ThreadGroup> safeThreadGroups = Collections.synchronizedList(new ArrayList<ThreadGroup>());

	private Thread battleThread;

	@SuppressWarnings({ "UnusedDeclaration", "EmptyCatchBlock"})
	public RobocodeSecurityManager(Thread safeThread, IThreadManager threadManager, boolean enabled, boolean experimental) {
		super();
		safeThreads.add(safeThread);
		this.threadManager = threadManager;
		this.enabled = enabled;
		this.experimental = experimental;
		safeSecurityContext = getSecurityContext();

		// Fake loading of classes
		BulletState s = BulletState.INACTIVE;
		BulletCommand c = new BulletCommand(null, false, 0, -1);
		ExecResult r = new ExecResult(null, null, null, null, null, false, false, false);
		TeamMessage t = new TeamMessage(null, null, null);

		try {
			RobocodeObjectInputStream is = new RobocodeObjectInputStream(new ByteArrayInputStream(new byte[0]), null);
		} catch (IOException e) {}

		Toolkit.getDefaultToolkit(); 
	}

	private synchronized void addRobocodeOutputStream(RobocodeFileOutputStream o) {
		outputStreamThreads.put(Thread.currentThread(), o);
	}

	public void addSafeThread(Thread safeThread) {
		checkPermission(new RobocodePermission("addSafeThread"));
		safeThreads.add(safeThread);
	}

	public void addSafeThreadGroup(ThreadGroup safeThreadGroup) {
		checkPermission(new RobocodePermission("addSafeThreadGroup"));
		safeThreadGroups.add(safeThreadGroup);
	}

	@Override
	public void checkAccess(Thread t) {
		super.checkAccess(t);
		Thread c = Thread.currentThread();

		if (isSafeThread(c) && isSafeContext()) {
			return;
		}

		IHostedThread robotProxy = threadManager.getRobotProxy(c);

		if (robotProxy == null) {
			robotProxy = threadManager.getLoadingRobotProxy(c);
			if (robotProxy != null) {
				throw new AccessControlException(
						"Preventing " + robotProxy.getStatics().getName() + " from access to thread: " + t.getName());
			}
			checkPermission(new RuntimePermission("modifyThread"));
			return;
		}

		ThreadGroup cg = c.getThreadGroup();
		ThreadGroup tg = t.getThreadGroup();

		if (cg == null || tg == null) {
			throw new AccessControlException(
					"Preventing " + Thread.currentThread().getName()
					+ " from access to a thread, because threadgroup is null.");
		}

		if (cg != tg) {
			throw new AccessControlException(
					"Preventing " + Thread.currentThread().getName()
					+ " from access to a thread, because threadgroup is different.");
		}

		if (cg.equals(tg)) {
			return;
		}

		throw new AccessControlException(
				"Preventing " + Thread.currentThread().getName() + " from access to threadgroup: " + tg.getName()
				+ ".  You must use your own ThreadGroup.");
	}

	@Override
	public void checkAccess(ThreadGroup g) {
		super.checkAccess(g);

		Thread c = Thread.currentThread();

		if (isSafeThread(c) && isSafeContext()) {
			return;
		}
		ThreadGroup cg = c.getThreadGroup();

		if (cg == null) {
			// What the heck is going on here?  JDK 1.3 is sending me a dead thread.
			// This crashes the entire jvm if I don't return here.
			return;
		}

		IHostedThread robotProxy = threadManager.getRobotProxy(c);

		if (robotProxy == null) {
			robotProxy = threadManager.getLoadingRobotProxy(c);
			if (robotProxy != null) {
				throw new AccessControlException(
						"Preventing " + robotProxy.getStatics().getName() + " from access to threadgroup: " + g.getName());
			}
			checkPermission(new RuntimePermission("modifyThreadGroup"));
			return;
		}

		if (g == null) {
			throw new NullPointerException("Thread group can't be null");
		}

		if (cg.equals(g)) {
			if (g.activeCount() > 5) {
				throw new AccessControlException(
						"Preventing " + Thread.currentThread().getName() + " from access to threadgroup: " + g.getName()
						+ ".  You may only create 5 threads.");
			}
			return;
		}

		robotProxy.drainEnergy();
		throw new AccessControlException(
				"Preventing " + Thread.currentThread().getName() + " from access to threadgroup: " + g.getName()
				+ " -- you must use your own ThreadGroup.");

	}

	/**
	 * Robocode's main security:  checkPermission
	 * If the calling thread is in our list of safe threads, allow permission.
	 * Else deny, with a few exceptions.
	 */
	@Override
	public void checkPermission(Permission perm, Object context) {
		syserr.println("Checking permission " + perm + " for context " + context);
		checkPermission(perm);
	}

	@Override
	public void checkPermission(Permission perm) {
		// For John Burkey at Apple
		if (!enabled) {
			return;
		}

		// Check if the current running thread is a safe thread
		if (isSafeThread()) {
			return;
		}

		// First, if we're running in Robocode's security context,
		// AND the thread is a safe thread, permission granted.
		// Essentially this optimizes the security manager for Robocode.
		if (isSafeContext()) {
			return;
		}

		// Ok, could be system, could be robot
		// We'll check the system policy (RobocodeSecurityPolicy)
		// This allows doPrivileged blocks to work.
		try {
			super.checkPermission(perm);
		} catch (SecurityException e) {
			// ok wa have a problem
			handleSecurityProblem(perm);
		}
	}

	private void handleSecurityProblem(Permission perm) {
		// For development purposes, allow read any file if override is set.
		if (perm instanceof FilePermission) {
			FilePermission fp = (FilePermission) perm;

			if (fp.getActions().equals("read")) {
				if (System.getProperty("OVERRIDEFILEREADSECURITY", "false").equals("true")) {
					return;
				}
			}
		}

		// Allow reading of properties.
		if (perm instanceof PropertyPermission) {
			if (perm.getActions().equals("read")) {
				return;
			}
		}

		if (perm instanceof RuntimePermission) {
			if (perm.getName() != null && perm.getName().length() >= 24) {
				if (perm.getName().substring(0, 24).equals("accessClassInPackage.sun")) {
					return;
				}
			}
		}

		// Ok, we need to figure out who our robot is.
		Thread c = Thread.currentThread();

		IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);

		// We don't know who this is, so deny permission.
		if (robotProxy == null) {
			if (perm instanceof RobocodePermission) {
				if (perm.getName().equals("System.out") || perm.getName().equals("System.err")
						|| perm.getName().equals("System.in")) {
					return;
				}
			}

			// Show warning on console.
			syserr.println("Preventing unknown thread " + Thread.currentThread().getName() + " from access: " + perm);
			syserr.flush();

			// Attempt to stop the window from displaying
			// This is a hack.
			if (perm instanceof java.awt.AWTPermission) {
				if (perm.getName().equals("showWindowWithoutWarningBanner")) {
					throw new ThreadDeath();
				}
			}

			// Throw the exception
			throw new AccessControlException(
					"Preventing unknown thread " + Thread.currentThread().getName() + " from access: " + perm);
		}

		// At this point, we have robotProxy set to the RobotProxy object requesting permission.

		// FilePermission access request.
		if (perm instanceof FilePermission) {
			FilePermission fp = (FilePermission) perm;

			// Robot wants access to read something
			if (fp.getActions().equals("read")) {
				// Get the fileSystemManager
				RobotFileSystemManager fileSystemManager = robotProxy.getRobotFileSystemManager();

				// If there is no readable directory, deny access.
				if (fileSystemManager.getReadableDirectory() == null) {
					robotProxy.drainEnergy();
					throw new AccessControlException(
							"Preventing " + robotProxy.getStatics().getName() + " from access: " + perm
							+ ": Robots that are not in a package may not read any files.");
				}
				// If this is a readable file, return.
				if (fileSystemManager.isReadable(fp.getName())) {
					return;
				} // Else disable robot
				robotProxy.drainEnergy();
				throw new AccessControlException(
						"Preventing " + robotProxy.getStatics().getName() + " from access: " + perm
						+ ": You may only read files in your own root package directory. ");
			} // Robot wants access to write something
			else if (fp.getActions().equals("write")) {
				// Get the RobocodeOutputStream the robot is trying to use.
				RobocodeFileOutputStream o = getRobocodeOutputStream();

				// There isn't one.  Deny access.
				if (o == null) {
					robotProxy.drainEnergy();
					throw new AccessControlException(
							"Preventing " + robotProxy.getStatics().getName() + " from access: " + perm
							+ ": You must use a RobocodeOutputStream.");
				}
				// Remove the RobocodeOutputStream so future access checks will fail.
				removeRobocodeOutputStream();

				// Get the fileSystemManager
				RobotFileSystemManager fileSystemManager = robotProxy.getRobotFileSystemManager();

				// If there is no writable directory, deny access
				if (fileSystemManager.getWritableDirectory() == null) {
					robotProxy.drainEnergy();

					throw new AccessControlException(
							"Preventing " + robotProxy.getStatics().getName() + " from access: " + perm
							+ ": Robots that are not in a package may not write any files.");
				}
				// If this is a writable file, permit access
				if (fileSystemManager.isWritable(fp.getName())) {
					return;
				} // else it's not writable, deny access.

				// We are creating the directory.
				if (fileSystemManager.getWritableDirectory().toString().equals(fp.getName())) {
					return;
				} // Not a writable directory.

				robotProxy.drainEnergy();
				// robotProxy.getOut().println("I would allow access to: " + fileSystemManager.getWritableDirectory());
				robotProxy.getOut().println(
						"Preventing " + robotProxy.getStatics().getName() + " from access: " + perm
						+ ": You may only write files in your own data directory. ");

				throw new AccessControlException(
						"Preventing " + robotProxy.getStatics().getName() + " from access: " + perm
						+ ": You may only write files in your own data directory. ");
			} // Robot wants access to write something
			else if (fp.getActions().equals("delete")) {
				// Get the fileSystemManager
				RobotFileSystemManager fileSystemManager = robotProxy.getRobotFileSystemManager();

				// If there is no writable directory, deny access
				if (fileSystemManager.getWritableDirectory() == null) {
					robotProxy.drainEnergy();
					throw new AccessControlException(
							"Preventing " + robotProxy.getStatics().getName() + " from access: " + perm
							+ ": Robots that are not in a package may not delete any files.");
				}
				// If this is a writable file, permit access
				if (fileSystemManager.isWritable(fp.getName())) {
					return;
				} // else it's not writable, deny access.

				// We are deleting our data directory.
				if (fileSystemManager.getWritableDirectory().toString().equals(fp.getName())) {
					// robotProxy.out.println("SYSTEM:  Please let me know if you see this string.  Thanks.  -Mat");
					return;
				} // Not a writable directory.

				robotProxy.drainEnergy();
				throw new AccessControlException(
						"Preventing " + robotProxy.getStatics().getName() + " from access: " + perm
						+ ": You may only delete files in your own data directory. ");
			}
		}

		if (perm instanceof RobocodePermission) {

			if (perm.getName().equals("System.out") || perm.getName().equals("System.err")) {
				robotProxy.println("SYSTEM:  You cannot write to System.out or System.err.");
				robotProxy.println("SYSTEM:  Please use out.println instead of System.out.println");
				throw new AccessControlException(
						"Preventing " + robotProxy.getStatics().getName() + " from access: " + perm);
			} else if (perm.getName().equals("System.in")) {
				robotProxy.println("SYSTEM:  You cannot read from System.in.");
				throw new AccessControlException(
						"Preventing " + robotProxy.getStatics().getName() + " from access: " + perm);

			}

		}

		// Permission denied.
		syserr.println("Preventing " + robotProxy.getStatics().getName() + " from access: " + perm);

		robotProxy.drainEnergy();

		if (perm instanceof java.awt.AWTPermission) {
			if (perm.getName().equals("showWindowWithoutWarningBanner")) {
				throw new ThreadDeath();
			}
		}

		throw new AccessControlException("Preventing " + Thread.currentThread().getName() + " from access: " + perm);
	}

	public void getFileOutputStream(RobocodeFileOutputStream o, boolean append) throws FileNotFoundException {
		if (o == null) {
			throw new NullPointerException("Null RobocodeFileOutputStream");
		}
		addRobocodeOutputStream(o);
		FileOutputStream fos;

		try {
			fos = new FileOutputStream(o.getName(), append);
		} catch (FileNotFoundException e) {
			IHostedThread robotProxy = threadManager.getRobotProxy(Thread.currentThread());

			if (robotProxy == null) {
				syserr.println("RobotProxy is null");
				return;
			}
			File dir = robotProxy.getRobotFileSystemManager().getWritableDirectory();

			addRobocodeOutputStream(o); // it's gone already...
			robotProxy.println("SYSTEM: Creating a data directory for you.");
			dir.mkdir();
			addRobocodeOutputStream(o); // one more time...
			fos = new FileOutputStream(o.getName(), append);
		}
		o.setFileOutputStream(fos);
	}

	private synchronized RobocodeFileOutputStream getRobocodeOutputStream() {
		return outputStreamThreads.get(Thread.currentThread());
	}

	private boolean isSafeThread() {
		return isSafeThread(Thread.currentThread());
	}

	private boolean isSafeThread(Thread c) {
		try {
			if (c == battleThread) {
				return true;
			}

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
			syserr.println("Exception checking safe thread: " + e);
			return false;
		}
	}

	private boolean isSafeContext() {
		try {
			return getSecurityContext().equals(safeSecurityContext);
		} catch (Exception e) {
			syserr.println("Exception checking safe thread: " + e);
			return false;
		}
	}

	private synchronized void removeRobocodeOutputStream() {
		outputStreamThreads.remove(Thread.currentThread());
	}

	public void removeSafeThread(Thread safeThread) {
		checkPermission(new RobocodePermission("removeSafeThread"));
		safeThreads.remove(safeThread);
	}

	public synchronized void setBattleThread(Thread newBattleThread) {
		checkPermission(new RobocodePermission("setBattleThread"));
		battleThread = newBattleThread;
	}

	public static void printlnToRobot(String s) {
		SecurityManager m = System.getSecurityManager();

		if (m instanceof RobocodeSecurityManager) {
			RobocodeSecurityManager rsm = (RobocodeSecurityManager) m;

			final PrintStream stream = rsm.getRobotOutputStream();

			if (stream != null) {
				stream.println(s);
			}
		}
	}

	public PrintStream getRobotOutputStream() {
		Thread c = Thread.currentThread();

		if (isSafeThread(c)) {
			return null;
		}

		if (threadManager == null) {
			syserr.println("Null thread manager.");
			return null;
		}

		IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);

		return (robotProxy != null) ? robotProxy.getOut() : null;
	}

	@Override
	public void checkPackageAccess(String pkg) {
		super.checkPackageAccess(pkg);

		if (isSafeContext()) {
			return;
		}

		if (isSafeThread()) {
			return;
		}

		// Access to robocode sub package?
		if (pkg.startsWith("robocode.")) {

			String subPkg = pkg.substring(9);

			// Only access to robocode.util or robocode.robotinterfaces is allowed
			if (!(subPkg.equals("util") || subPkg.equals("robotinterfaces")
					|| (experimental && subPkg.equals("robotinterfaces.peer")) || (subPkg.equals("robotpaint")))) {

				Thread c = Thread.currentThread();

				IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);

				if (robotProxy != null) {
					robotProxy.drainEnergy();
					if (!experimental && subPkg.equals("robotinterfaces.peer")) {
						robotProxy.println(
								"SYSTEM: " + robotProxy.getStatics().getName()
								+ " is not allowed to access the internal Robocode package: " + pkg + "\n"
								+ "SYSTEM: Perhaps you did not set the -DEXPERIMENTAL=true option in the robocode.bat or robocode.sh file?\n"
								+ "SYSTEM: ----");
					}
				}

				throw new AccessControlException(
						"Preventing " + Thread.currentThread().getName() + " from access to the internal Robocode pakage: "
						+ pkg);
			}
		}
	}

	public static void createNewAppContext() {
		// same as SunToolkit.createNewAppContext();
		// we can't assume that we are always on Suns JVM, so we can't reference it directly
		// why we call that ? Because SunToolkit is caching AWTQueue instance form main thread group and use it on robots threads
		// and he is not asking us for checkAwtEventQueueAccess above
		try {
			final Class<?> sunToolkit = ClassLoader.getSystemClassLoader().loadClass("sun.awt.SunToolkit");
			final Method createNewAppContext = sunToolkit.getDeclaredMethod("createNewAppContext");

			createNewAppContext.invoke(null);
		} catch (ClassNotFoundException e) {// we are not on sun JVM
		} catch (NoSuchMethodException e) {
			throw new Error("Looks like SunVM but unable to assure secured AWTQueue, sorry", e);
		} catch (InvocationTargetException e) {
			throw new Error("Looks like SunVM but unable to assure secured AWTQueue, sorry", e);
		} catch (IllegalAccessException e) {
			throw new Error("Looks like SunVM but unable to assure secured AWTQueue, sorry", e);
		}
		// end: same as SunToolkit.createNewAppContext();
	}
}
