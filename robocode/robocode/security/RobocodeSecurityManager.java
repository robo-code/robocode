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
 *     - Added checkPackageAccess() to limit access to the robocode.util Robocode
 *       package only
 *     - Ported to Java 5.0
 *     - Code cleanup
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.security;


import java.io.*;
import java.security.AccessControlException;
import java.security.Permission;
import java.util.*;

import robocode.RobocodeFileOutputStream;
import robocode.manager.ThreadManager;
import robocode.peer.RobotPeer;
import robocode.peer.robot.RobotFileSystemManager;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
public class RobocodeSecurityManager extends SecurityManager {
	private Map<Thread, RobocodeFileOutputStream> outputStreamThreads;
	private List<Thread> safeThreads;
	private List<ThreadGroup> safeThreadGroups;
	private Thread battleThread;
	public String status;

	private ThreadManager threadManager;
	private PrintStream syserr = System.err;
	private Object safeSecurityContext;
	private boolean enabled = true;

	/**
	 * RobocodeSecurityManager constructor
	 */
	public RobocodeSecurityManager(Thread safeThread, ThreadManager threadManager, boolean enabled) {
		super();
		safeThreads = Collections.synchronizedList(new ArrayList<Thread>());
		safeThreads.add(safeThread);
		safeThreadGroups = Collections.synchronizedList(new ArrayList<ThreadGroup>());
		outputStreamThreads = new HashMap<Thread, RobocodeFileOutputStream>();
		this.threadManager = threadManager;
		safeSecurityContext = getSecurityContext();
		this.enabled = enabled;
	}

	private synchronized void addRobocodeOutputStream(RobocodeFileOutputStream o) {
		outputStreamThreads.put(Thread.currentThread(), o);
	}

	public synchronized void addSafeThread(Thread safeThread) {
		checkPermission(new RobocodePermission("addSafeThread"));
		safeThreads.add(safeThread);
	}

	public synchronized void addSafeThreadGroup(ThreadGroup safeThreadGroup) {
		checkPermission(new RobocodePermission("addSafeThreadGroup"));
		safeThreadGroups.add(safeThreadGroup);
	}

	public synchronized void checkAccess(Thread t) {
		super.checkAccess(t);
		Thread c = Thread.currentThread();

		if (isSafeThread(c) && getSecurityContext().equals(safeSecurityContext)) {
			return;
		}

		RobotPeer r = threadManager.getRobotPeer(c);

		if (r == null) {
			r = threadManager.getLoadingRobotPeer(c);
			if (r != null) {
				throw new AccessControlException("Preventing " + r.getName() + " from access to thread: " + t.getName());
			} else {
				checkPermission(new RuntimePermission("modifyThread"));
				return;
			}
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

	public synchronized void checkAccess(ThreadGroup g) {
		super.checkAccess(g);

		Thread c = Thread.currentThread();

		if (isSafeThread(c) && getSecurityContext().equals(safeSecurityContext)) {
			return;
		}
		ThreadGroup cg = c.getThreadGroup();

		if (cg == null) {
			// What the heck is going on here?  JDK 1.3 is sending me a dead thread.
			// This crashes the entire jvm if I don't return here.
			return;
		}

		RobotPeer r = threadManager.getRobotPeer(c);

		if (r == null) {
			r = threadManager.getLoadingRobotPeer(c);
			if (r != null) {
				throw new AccessControlException(
						"Preventing " + r.getName() + " from access to threadgroup: " + g.getName());
			} else {
				checkPermission(new RuntimePermission("modifyThreadGroup"));
				return;
			}
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

		throw new AccessControlException(
				"Preventing " + Thread.currentThread().getName() + " from access to threadgroup: " + g.getName()
				+ " -- you must use your own ThreadGroup.");

	}

	/**
	 * Robocode's main security:  checkPermission
	 * If the calling thread is in our list of safe threads, allow permission.
	 * Else deny, with a few exceptions.
	 */
	public synchronized void checkPermission(Permission perm, Object context) {
		syserr.println("Checking permission " + perm + " for context " + context);
		super.checkPermission(perm);
	}

	public synchronized void checkPermission(Permission perm) {
		// For John Burkey at Apple
		if (!enabled) {
			return;
		}

		// First, if we're running in Robocode's security context,
		// AND the thread is a safe thread, permission granted.
		// Essentially this optimizes the security manager for Robocode.
		if (getSecurityContext().equals(safeSecurityContext)) {
			return;
		}
		Thread c = Thread.currentThread();

		// Ok, could be system, could be robot
		// We'll check the system policy (RobocodeSecurityPolicy)
		// This allows doPrivileged blocks to work.
		try {
			super.checkPermission(perm);
			return;
		} catch (SecurityException e) {}

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
		RobotPeer r = threadManager.getRobotPeer(c);

		if (r == null) {
			r = threadManager.getLoadingRobotPeer(c);
			// We don't know who this is, so deny permission.
			if (r == null) {
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
		}

		// At this point, we have r set to the RobotPeer object requesting permission.

		// FilePermission access request.
		if (perm instanceof FilePermission) {
			FilePermission fp = (FilePermission) perm;

			// Robot wants access to read something
			if (fp.getActions().equals("read")) {
				// Get the fileSystemManager
				RobotFileSystemManager fileSystemManager = r.getRobotFileSystemManager();

				// If there is no readable directory, deny access.
				if (fileSystemManager.getReadableDirectory() == null) {
					r.setEnergy(0);
					throw new AccessControlException(
							"Preventing " + r.getName() + " from access: " + perm
							+ ": Robots that are not in a package may not read any files.");
				}
				// If this is a readable file, return.
				if (fileSystemManager.isReadable(fp.getName())) {
					return;
				} // Else disable robot
				else {
					r.setEnergy(0);
					throw new AccessControlException(
							"Preventing " + r.getName() + " from access: " + perm
							+ ": You may only read files in your own root package directory. ");
				}
			} // Robot wants access to write something
			else if (fp.getActions().equals("write")) {
				// Get the RobocodeOutputStream the robot is trying to use.
				RobocodeFileOutputStream o = getRobocodeOutputStream();

				// There isn't one.  Deny access.
				if (o == null) {
					r.setEnergy(0);
					throw new AccessControlException(
							"Preventing " + r.getName() + " from access: " + perm + ": You must use a RobocodeOutputStream.");
				}
				// Remove the RobocodeOutputStream so future access checks will fail.
				removeRobocodeOutputStream();

				// Get the fileSystemManager
				RobotFileSystemManager fileSystemManager = r.getRobotFileSystemManager();

				// If there is no writable directory, deny access
				if (fileSystemManager.getWritableDirectory() == null) {
					r.setEnergy(0);

					throw new AccessControlException(
							"Preventing " + r.getName() + " from access: " + perm
							+ ": Robots that are not in a package may not write any files.");
				}
				// If this is a writable file, permit access
				if (fileSystemManager.isWritable(fp.getName())) {
					return;
				} // else it's not writable, deny access.
				else {
					// We are creating the directory.
					if (fileSystemManager.getWritableDirectory().toString().equals(fp.getName())) {
						return;
					} // Not a writable directory.
					else {
						r.setEnergy(0);
						// r.out.println("I would allow access to: " + fileSystemManager.getWritableDirectory());
						threadOut(
								"Preventing " + r.getName() + " from access: " + perm
								+ ": You may only write files in your own data directory. ");

						throw new AccessControlException(
								"Preventing " + r.getName() + " from access: " + perm
								+ ": You may only write files in your own data directory. ");
					}
				}
			} // Robot wants access to write something
			else if (fp.getActions().equals("delete")) {
				// Get the fileSystemManager
				RobotFileSystemManager fileSystemManager = r.getRobotFileSystemManager();

				// If there is no writable directory, deny access
				if (fileSystemManager.getWritableDirectory() == null) {
					r.setEnergy(0);
					throw new AccessControlException(
							"Preventing " + r.getName() + " from access: " + perm
							+ ": Robots that are not in a package may not delete any files.");
				}
				// If this is a writable file, permit access
				if (fileSystemManager.isWritable(fp.getName())) {
					return;
				} // else it's not writable, deny access.
				else {
					// We are deleting our data directory.
					if (fileSystemManager.getWritableDirectory().toString().equals(fp.getName())) {
						// r.out.println("SYSTEM:  Please let me know if you see this string.  Thanks.  -Mat");
						return;
					} // Not a writable directory.
					else {
						r.setEnergy(0);
						throw new AccessControlException(
								"Preventing " + r.getName() + " from access: " + perm
								+ ": You may only delete files in your own data directory. ");
					}
				}
			}
		}

		if (perm instanceof RobocodePermission) {

			if (perm.getName().equals("System.out") || perm.getName().equals("System.err")) {
				r.out.println("SYSTEM:  You cannot write to System.out or System.err.");
				r.out.println("SYSTEM:  Please use out.println instead of System.out.println");
				throw new AccessControlException("Preventing " + r.getName() + " from access: " + perm);
			} else if (perm.getName().equals("System.in")) {
				r.out.println("SYSTEM:  You cannot read from System.in.");
				throw new AccessControlException("Preventing " + r.getName() + " from access: " + perm);

			}

		}

		// Permission denied.
		if (status == null || status.equals("")) {
			syserr.println("Preventing " + r.getName() + " from access: " + perm);
		} else {
			syserr.println("Preventing " + r.getName() + " from access: " + perm + " (" + status + ")");
		}

		r.setEnergy(0);

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
			Thread c = Thread.currentThread();
			RobotPeer r = threadManager.getRobotPeer(c);
			File dir = r.getRobotFileSystemManager().getWritableDirectory();

			addRobocodeOutputStream(o); // it's gone already...
			r.out.println("SYSTEM: Creating a data directory for you.");
			dir.mkdir();
			addRobocodeOutputStream(o); // one more time...
			fos = new FileOutputStream(o.getName(), append);
		}
		o.setFileOutputStream(fos);
	}

	private synchronized RobocodeFileOutputStream getRobocodeOutputStream() {
		RobocodeFileOutputStream rfos = outputStreamThreads.get(Thread.currentThread());

		if (rfos == null) {
			return null;
		}
		if (rfos instanceof RobocodeFileOutputStream) {
			return rfos;
		} else {
			outputStreamThreads.remove(Thread.currentThread());
			throw new AccessControlException(
					"Preventing " + Thread.currentThread().getName() + " from access: This is not a RobocodeOutputStream.");
		}
	}

	public String getStatus() {
		return status;
	}

	public boolean isSafeThread(Thread c) {
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
	}

	private synchronized void removeRobocodeOutputStream() {
		outputStreamThreads.remove(Thread.currentThread());
	}

	public void removeSafeThread(Thread safeThread) {
		checkPermission(new RobocodePermission("removeSafeThread"));
		safeThreads.remove(safeThread);
	}

	public void setBattleThread(Thread newBattleThread) {
		checkPermission(new RobocodePermission("setBattleThread"));
		battleThread = newBattleThread;
	}

	public void setStatus(String newStatus) {
		status = newStatus;
	}

	public void threadOut(String s) {
		Thread c = Thread.currentThread();
		RobotPeer r = threadManager.getRobotPeer(c);

		if (r == null) {
			r = threadManager.getLoadingRobotPeer(c);
		}
		if (r == null) {
			throw new AccessControlException("Cannot call threadOut from unknown thread.");
		}

		r.out.println(s);
	}

	public PrintStream getRobotOutputStream() {
		Thread c = Thread.currentThread();

		try {
			if (isSafeThread(c)) {
				return null;
			}
		} catch (Exception e) {
			return null;
		}

		if (threadManager == null) {
			syserr.println("Null thread manager.");
		}

		try {
			RobotPeer r = threadManager.getRobotPeer(c);

			if (r == null) {
				r = threadManager.getLoadingRobotPeer(c);
			}
			return (r != null) ? r.getOut() : null;

		} catch (Exception e) {
			syserr.println("Unable to get output stream: " + e);
			return syserr;
		}
	}

	public boolean isSafeThread() {
		try {
			return isSafeThread(Thread.currentThread());
		} catch (Exception e) {
			syserr.println("Exception checking safe thread: " + e);
			return false;
		}
	}

	public void checkPackageAccess(String pkg) {
		if (pkg.equals("java.lang")) {
			return;
		}

		super.checkPackageAccess(pkg);

		// Accept if running in Robocode's security context
		if (getSecurityContext().equals(safeSecurityContext)) {
			return;
		}

		// Access to robocode sub package?
		if (pkg.startsWith("robocode.")) {

			String subPkg = pkg.substring(9);

			// Only access to robocode.util is allowed
			if (!(subPkg.equals("util"))) {
				RobotPeer r = threadManager.getRobotPeer(Thread.currentThread());

				if (r != null) {
					r.setEnergy(0);

					throw new AccessControlException(
							"Preventing " + Thread.currentThread().getName()
							+ " from access to the internal Robocode pakage: " + pkg);
				}
			}
		}
	}
}
