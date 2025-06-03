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

import java.net.SocketPermission;
import java.security.AccessControlException;
import java.security.Permission;

/**
 * Legacy security manager implementation that uses Java's SecurityManager.
 * This implementation will be used on Java versions that support SecurityManager (before Java 24).
 *
 * @author Flemming N. Larsen (original)
 */
public class LegacyRobocodeSecurityManager extends SecurityManager implements ISecurityManager {

	private final IThreadManager threadManager;
	private boolean initialized = false;

	public LegacyRobocodeSecurityManager(IThreadManager threadManager) {
		super();
		this.threadManager = threadManager;
	}

	@Override
	public void init() {
		if (initialized) {
			return;
		}

		ThreadGroup tg = Thread.currentThread().getThreadGroup();

		while (tg != null) {
			threadManager.addSafeThreadGroup(tg);
			tg = tg.getParent();
		}
		// We need to exercise it in order to load all used classes on this thread
		isSafeThread(Thread.currentThread());

		try {
			// Check if SecurityManager is supported in this Java version
			try {
				// This will throw UnsupportedOperationException in Java 24+
				System.getSecurityManager();

				// If we got here, SecurityManager is supported
				System.setSecurityManager(this);
				initialized = true;
				Logger.logMessage("Legacy SecurityManager initialized successfully");
			} catch (UnsupportedOperationException e) {
				Logger.logMessage("SecurityManager not supported in this Java version - using alternative security");
				initialized = true;
			}
		} catch (Exception e) {
			Logger.logError("Failed to initialize security manager", e);
			throw new RuntimeException("Failed to initialize security", e);
		}
	}

	@Override
	public void checkAccess(Thread t) {
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

	@Override
	public void checkPermission(Permission perm) {
		Thread c = Thread.currentThread();
		if (isSafeThread(c)) {
			return;
		}
		if (perm instanceof RuntimePermission) { // Check if System.getenv is called
			RuntimePermission runtimePermission = (RuntimePermission) perm;
			final String name = runtimePermission.getName();
			if (name != null && name.startsWith("getenv.")) {
				return;
			}
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
