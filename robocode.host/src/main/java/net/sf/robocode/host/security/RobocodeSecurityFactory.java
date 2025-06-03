/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;

import net.sf.robocode.host.IThreadManager;
import net.sf.robocode.io.Logger;

/**
 * Factory for creating security components in Robocode.
 * This provides a single point of access for security-related components.
 *
 * @author Flemming N. Larsen (original)
 */
public class RobocodeSecurityFactory {

    private static volatile ISecurityManager securityManager;
    private static volatile RobocodeSecurityPolicy securityPolicy;
    private static volatile SecurityPolicyManager policyManager;

    /**
     * Get or create the Robocode security manager.
     * 
     * @param threadManager the thread manager to use
     * @return the security manager instance
     */
    public static synchronized ISecurityManager getSecurityManager(IThreadManager threadManager) {
        if (securityManager == null) {
            securityManager = SecurityManagerFactory.createSecurityManager(threadManager);
        }
        return securityManager;
    }

    /**
     * Get or create the Robocode security policy.
     * 
     * @param threadManager the thread manager to use
     * @return the security policy instance
     */
    public static synchronized RobocodeSecurityPolicy getSecurityPolicy(IThreadManager threadManager) {
        if (securityPolicy == null) {
            try {
                securityPolicy = new RobocodeSecurityPolicy(threadManager);
            } catch (UnsupportedOperationException e) {
                Logger.logError("Security policy creation failed - probably running on Java 24+", e);
                return null;
            }
        }
        return securityPolicy;
    }

    /**
     * Get or create the Robocode security policy manager.
     * 
     * @return the security policy manager instance
     */
    public static synchronized SecurityPolicyManager getPolicyManager() {
        if (policyManager == null) {
            policyManager = new SecurityPolicyManager();
        }
        return policyManager;
    }
}
