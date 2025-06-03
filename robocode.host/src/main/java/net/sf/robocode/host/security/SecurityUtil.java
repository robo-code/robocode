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

/**
 * Modern security utility to replace functionality previously provided by SecurityManager.
 * Created as part of removing SecurityManager dependency (deprecated in Java 17, removed in Java 24).
 *
 * @author Flemming N. Larsen (original)
 */
public class SecurityUtil {

    /**
     * Validates thread access by checking if the current thread has access to the specified thread.
     * Similar to what SecurityManager.checkAccess(Thread) used to do.
     *
     * @param thread The thread to check access for
     * @param threadManager The thread manager instance
     * @return true if access is allowed, false otherwise
     */
    public static boolean checkThreadAccess(Thread thread, IThreadManager threadManager) {
        Thread currentThread = Thread.currentThread();

        if (threadManager.isSafeThread(currentThread)) {
            return true;
        }

        // Threads belonging to other thread groups is not allowed to access threads belonging to other thread groups
        ThreadGroup currentThreadGroup = currentThread.getThreadGroup();
        ThreadGroup targetThreadGroup = thread.getThreadGroup();

        if (currentThreadGroup == null || targetThreadGroup == null) {
            return false;
        }

        // Check if the current thread's group is in the hierarchy of the target thread's group
        boolean found = false;
        ThreadGroup tg = targetThreadGroup;

        while (tg != null) {
            if (tg == currentThreadGroup) {
                found = true;
                break;
            }
            try {
                tg = tg.getParent();
            } catch (Exception e) {
                break;
            }
        }

        if (!found) {
            String message = "Preventing " + currentThread.getName() + " from access to " + thread.getName();
            IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(currentThread);

            if (robotProxy != null) {
                robotProxy.punishSecurityViolation(message);
            }

            Logger.logError(message);
            return false;
        }

        return true;
    }

    /**
     * Validates thread group access by checking if the current thread has access to the specified thread group.
     * Similar to what SecurityManager.checkAccess(ThreadGroup) used to do.
     *
     * @param group The thread group to check access for
     * @param threadManager The thread manager instance
     * @return true if access is allowed, false otherwise
     */
    public static boolean checkThreadGroupAccess(ThreadGroup group, IThreadManager threadManager) {
        Thread currentThread = Thread.currentThread();

        if (threadManager.isSafeThread(currentThread)) {
            return true;
        }

        ThreadGroup currentThreadGroup = currentThread.getThreadGroup();

        if (currentThreadGroup == null) {
            return false;
        }

        // Special case for SeedGenerator Thread
        if ("SeedGenerator Thread".equals(currentThread.getName()) && 
            "SeedGenerator ThreadGroup".equals(currentThreadGroup.getName())) {
            return true;
        }

        IHostedThread robotProxy = threadManager.getLoadedOrLoadingRobotProxy(currentThread);

        if (robotProxy == null) {
            Logger.logError("Preventing " + currentThread.getName() + " from access to " + group.getName());
            return false;
        }

        if (currentThreadGroup.activeCount() > 5) {
            String message = "Robots are only allowed to create up to 5 threads!";
            robotProxy.punishSecurityViolation(message);
            Logger.logError(message);
            return false;
        }

        return true;
    }
}
