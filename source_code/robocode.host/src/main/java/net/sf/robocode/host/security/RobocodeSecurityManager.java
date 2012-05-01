/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
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
		// we need to excersize it, to load all used classes on this thread.
		isSafeThread(Thread.currentThread());
		if (isSecutityOn) {
			System.setSecurityManager(this);
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

		if (robotProxy != null) {
			if (cg.activeCount() > 5) {
				final String message = "Preventing " + c.getName() + " from access to thread group " + g.getName()
						+ ". You may only create 5 threads.";

				robotProxy.punishSecurityViolation(message);
				throw new AccessControlException(message);
			}
			return;
		}

		throw new AccessControlException(
				"Preventing thread " + c.getName() + " with unknown thread group " + g.getName() + " from access");
	}

	private boolean isSafeThread(Thread c) {
		return threadManager.isSafeThread(c);
	}
/*
    @Override
    public void checkExit(int status) {
    	super.checkExit(status);
    	
    	new Throwable().printStackTrace();
    }*/
}