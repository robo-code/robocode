/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
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
 *     - moved most of checks to RobocodeSecurityPolicy
 *******************************************************************************/
package net.sf.robocode.host.security;


import net.sf.robocode.host.IHostedThread;
import net.sf.robocode.host.IThreadManager;

import java.security.AccessControlException;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Pavel Savara (contributor)
 */
public class RobocodeSecurityManager extends SecurityManager {
	public static final boolean isSecutityOn = !System.getProperty("NOSECURITY", "false").equals("true");

	private final IThreadManager threadManager;

	public RobocodeSecurityManager(IThreadManager threadManager) {
		super();
		this.threadManager = threadManager;

		ThreadGroup tg = Thread.currentThread().getThreadGroup();

		while (tg != null) {
			threadManager.addSafeThreadGroup(tg);
			tg = tg.getParent();
		}
		// We need to exercise it in order to load all used classes on this thread
		isSafeThread(Thread.currentThread());
		if (isSecutityOn) {
			System.setSecurityManager(this);
		}
	}

	@Override
	public void checkAccess(Thread t) {
		if (!isSecutityOn) {
			return;
		}

		Thread c = Thread.currentThread();

		if (isSafeThread(c)) {
			return;
		}
		super.checkAccess(t);

		// Threads belonging to other thread groups is not allowed to access threads belonging to other thread groups
		// Bug fix [3021140] Possible for robot to kill other robot threads.
		// In the following the thread group of the current thread must be in the thread group hierarchy of the
		// attacker thread; otherwise an AccessControlException must be thrown.

		boolean found = false;
		
		ThreadGroup cg = c.getThreadGroup();
		ThreadGroup tg = t.getThreadGroup();

		while (tg != null) {
			if (tg == cg) {
				found = true;
				break;
			}
			try {
				tg = tg.getParent();
			} catch (AccessControlException e) {
				// We expect an AccessControlException due to missing RuntimePermission modifyThreadGroup
				break;
			}
		}
		if (!found) {
			String message = "Preventing " + c.getName() + " from access to " + t.getName();
			IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);

			if (robotProxy != null) {
				robotProxy.punishSecurityViolation(message);
			}
			throw new AccessControlException(message);
		}
	}

	@Override
	public void checkAccess(ThreadGroup g) {
		if (!isSecutityOn) {
			return;
		}
		Thread c = Thread.currentThread();

		if (isSafeThread(c)) {
			return;
		}
		super.checkAccess(g);

		final ThreadGroup cg = c.getThreadGroup();

		if (cg == null) {
			// What the heck is going on here?  JDK 1.3 is sending me a dead thread.
			// This crashes the entire jvm if I don't return here.
			return;
		}

		IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);

		if (robotProxy == null) {
			throw new AccessControlException("Preventing " + c.getName() + " from access to " + g.getName());			
		}

		if (cg.activeCount() > 5) {
			String message = "Robots are only allowed to create up to 5 threads!";

			robotProxy.punishSecurityViolation(message);
			throw new AccessControlException(message);
		}
	}

	private boolean isSafeThread(Thread c) {
		return threadManager.isSafeThread(c);
	}
}
