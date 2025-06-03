/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;

import java.security.Permission;

/**
 * Interface for Robocode security management that abstracts away from
 * the underlying implementation, allowing for compatibility with
 * both Java 8+ and Java 24+.
 *
 * @author Flemming N. Larsen (original)
 */
public interface ISecurityManager {
    /**
     * Checks if the requested permission is allowed.
     *
     * @param perm the requested permission
     * @throws SecurityException if the permission is denied
     */
    void checkPermission(Permission perm);

    /**
     * Initializes the security manager.
     * This method should be called during application startup.
     */
    void init();
}
