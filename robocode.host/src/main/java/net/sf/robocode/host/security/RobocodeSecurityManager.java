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
	private boolean isSecutityOn;

	private final IThreadManager threadManager;

	public RobocodeSecurityManager(IThreadManager threadManager, boolean enabled) {
		super();
		this.threadManager = threadManager;
		this.isSecutityOn = enabled;

		ThreadGroup tg = Thread.currentThread().getThreadGroup();

		while (tg != null) {
			threadManager.addSafeThreadGroup(tg);
			tg = tg.getParent();
		}
		// we need to excersize it, to load all used classes on this thread.
		isSafeThread(Thread.currentThread());
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

		ThreadGroup cg = c.getThreadGroup();

		if (cg == null) {
			// What the heck is going on here?  JDK 1.3 is sending me a dead thread.
			// This crashes the entire jvm if I don't return here.
			return;
		}

		IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);

		if (robotProxy != null) {
			// TODO ZAMO, review, should I rather ban all access to thread and thread group ?
			if (cg.activeCount() > 5) {
				final String message = "Preventing " + Thread.currentThread().getName()
						+ " from access to threadgroup: " + g.getName() + ".  You may only create 5 threads.";

				robotProxy.println(message);
				robotProxy.drainEnergy();
				throw new AccessControlException(message);
			}
		}
	}

	private boolean isSafeThread(Thread c) {
		return threadManager.isSafeThread(c);
	}
}
