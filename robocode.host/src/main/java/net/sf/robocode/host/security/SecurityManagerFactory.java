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
 * Factory for creating the appropriate security manager implementation
 * based on the Java version and system configuration.
 * <p>
 * This factory allows Robocode to work with both Java 8+ (with SecurityManager)
 * and Java 24+ (without SecurityManager).
 *
 * @author Flemming N. Larsen (original)
 */
public class SecurityManagerFactory {

	private static final boolean IS_SECURITY_DISABLED = Boolean.getBoolean("NOSECURITY");

	/**
	 * Creates and initializes the appropriate security manager implementation.
	 *
	 * @param threadManager the thread manager to use
	 * @return an initialized ISecurityManager instance
	 */
	public static ISecurityManager createSecurityManager(IThreadManager threadManager) {
		if (IS_SECURITY_DISABLED) {
			Logger.logMessage("Security is disabled by system property");
			return new NoSecurityManager();
		}

		// Try to detect Java version and capabilities
		try {
			int javaVersion = getJavaVersion();
			boolean securityManagerAvailable = isSecurityManagerAvailable();

			Logger.logMessage("Detected Java version: " + javaVersion);

			if (javaVersion >= 24 || !securityManagerAvailable) {
				Logger.logMessage("Using Java 24+ compatible security adapter");
				ISecurityManager manager = new RobocodeSecurityAdapter(threadManager);
				manager.init();
				return manager;
			} else {
				Logger.logMessage("Using legacy SecurityManager");
				LegacyRobocodeSecurityManager manager = new LegacyRobocodeSecurityManager(threadManager);
				manager.init();
				return manager;
			}
		} catch (Exception e) {
			Logger.logError("Error creating security manager, falling back to adapter", e);
			ISecurityManager manager = new RobocodeSecurityAdapter(threadManager);
			manager.init();
			return manager;
		}
	}

	/**
	 * Detects the current Java version.
	 *
	 * @return the Java version as an integer (8, 11, 17, 24, etc.)
	 */
	private static int getJavaVersion() {
		String version = System.getProperty("java.version");
		if (version.startsWith("1.")) {
			// Java 8 or earlier uses 1.X format
			return Integer.parseInt(version.substring(2, 3));
		} else {
			// Java 9+ uses X format
			int dotPos = version.indexOf('.');
			if (dotPos != -1) {
				return Integer.parseInt(version.substring(0, dotPos));
			} else {
				return Integer.parseInt(version);
			}
		}
	}

	/**
	 * Checks if the SecurityManager is available and can be enabled.
	 *
	 * @return true if SecurityManager can be used, false otherwise
	 */
	private static boolean isSecurityManagerAvailable() {
		try {
			// First check if the SecurityManager class can be loaded
			Class.forName("java.lang.SecurityManager");

			try {
				// Then try to get the current security manager
				// This will throw UnsupportedOperationException in Java 24+
				System.getSecurityManager();
				return true;
			} catch (UnsupportedOperationException e) {
				return false;
			}
		} catch (ClassNotFoundException e) {
			// SecurityManager class is not available at all
			return false;
		} catch (Throwable t) {
			// Any other issue - err on the side of caution
			Logger.logError("Error checking SecurityManager availability", t);
			return false;
		}
	}

	/**
	 * A no-op security manager implementation for when security is disabled.
	 */
	private static class NoSecurityManager implements ISecurityManager {
		@Override
		public void checkPermission(java.security.Permission perm) {
			// Allow everything
		}

		@Override
		public void init() {
			// Nothing to initialize
		}
	}
}
