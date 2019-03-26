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
import net.sf.robocode.io.RobocodeProperties;

import java.net.SocketPermission;
import java.security.AccessControlException;
import java.security.Permission;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Pavel Savara (contributor)
 */
public class RobocodeSecurityManager extends SecurityManager {

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
		if (RobocodeProperties.isSecurityOn()) {
			System.setSecurityManager(this);
		}
	}

	@Override
	public void checkAccess(Thread t) {
		if (RobocodeProperties.isSecurityOff()) {
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
			throw new SecurityException(message);
		}
	}

	@Override
	public void checkAccess(ThreadGroup g) {
		if (RobocodeProperties.isSecurityOff()) {
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

		// Bug fix #382 Unable to run robocode.bat -- Access Control Exception
		if ("SeedGenerator Thread".equals(c.getName()) && "SeedGenerator ThreadGroup".equals(cg.getName())) {
			return; // The SeedGenerator might create a thread, which needs to be silently ignored
		}
		
		IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);

		if (robotProxy == null) {
			throw new AccessControlException("Preventing " + c.getName() + " from access to " + g.getName());			
		}

		if (cg.activeCount() > 5) {
			String message = "Robots are only allowed to create up to 5 threads!";

			robotProxy.punishSecurityViolation(message);
			throw new SecurityException(message);
		}
	}
	
    public void checkPermission(Permission perm) {
		if (RobocodeProperties.isSecurityOff()) {
			return;
		}
		Thread c = Thread.currentThread();
		if (isSafeThread(c)) {
			return;
		}
        super.checkPermission(perm);

        if (perm instanceof SocketPermission) {
    		IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(c);
        	String message = "Using socket is not allowed";
        	robotProxy.punishSecurityViolation(message);
            throw new SecurityException(message);
        }
    }

	private boolean isSafeThread(Thread c) {
		return threadManager.isSafeThread(c);
	}
}
