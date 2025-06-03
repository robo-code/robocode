/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;

/**
 * Error thrown when a robot violates security constraints.
 * This replaces SecurityExceptions that were previously thrown by SecurityManager.
 *
 * @author Robocode Contributors
 */
public class RobotSecurityViolationError extends Error {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new RobotSecurityViolationError with the specified detail message.
     *
     * @param message the detail message
     */
    public RobotSecurityViolationError(String message) {
        super(message);
    }

    /**
     * Constructs a new RobotSecurityViolationError with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of this error
     */
    public RobotSecurityViolationError(String message, Throwable cause) {
        super(message, cause);
    }
}
