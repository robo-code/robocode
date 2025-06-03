/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;

import net.sf.robocode.host.IHostedThread;

/**
 * Stores the current robot context for security checks without
 * relying on Java's SecurityManager.
 * 
 * @author Flemming N. Larsen (original)
 */
public class RobotSecurityContext {
    private static final ThreadLocal<IHostedThread> currentRobot = new ThreadLocal<>();

    /**
     * Sets the robot proxy for the current thread's security context.
     * 
     * @param robotProxy the robot proxy to set, or null to clear
     */
    public static void setRobotProxy(IHostedThread robotProxy) {
        currentRobot.set(robotProxy);
    }

    /**
     * Gets the robot proxy from the current thread's security context.
     * 
     * @return the current robot proxy or null if not in a robot context
     */
    public static IHostedThread getRobotProxy() {
        return currentRobot.get();
    }

    /**
     * Clears the robot proxy from the current thread's security context.
     */
    public static void clearRobotProxy() {
        currentRobot.remove();
    }
}
