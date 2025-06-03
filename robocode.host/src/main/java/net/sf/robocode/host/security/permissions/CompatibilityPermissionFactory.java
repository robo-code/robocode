/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security.permissions;

import net.sf.robocode.io.Logger;

import java.security.Permission;

/**
 * Factory class that creates appropriate permissions based on the Java version.
 * This provides compatibility between Java 8+ and Java 24+.
 *
 * @author Flemming N. Larsen (original)
 */
public class CompatibilityPermissionFactory {

	private static volatile Boolean hasRuntimePermission;

	/**
	 * Checks if the original RuntimePermission class is available.
	 *
	 * @return true if available, false otherwise
	 */
	public static boolean hasRuntimePermission() {
		if (hasRuntimePermission == null) {
			try {
				Class.forName("java.security.RuntimePermission");
				hasRuntimePermission = true;
			} catch (ClassNotFoundException e) {
				hasRuntimePermission = false;
			}
		}
		return hasRuntimePermission;
	}

	/**
	 * Creates a runtime permission that works on all Java versions.
	 *
	 * @param name the permission name
	 * @return a compatible runtime permission
	 */
	public static Permission createRuntimePermission(String name) {
		if (hasRuntimePermission()) {
			try {
				// Use reflection to create a RuntimePermission
				Class<?> permClass = Class.forName("java.security.RuntimePermission");
				return (Permission) permClass.getConstructor(String.class).newInstance(name);
			} catch (Exception e) {
				Logger.logError("Error creating RuntimePermission, falling back to custom implementation", e);
				return new RobocodeRuntimePermission(name);
			}
		} else {
			return new RobocodeRuntimePermission(name);
		}
	}

	/**
	 * Creates a runtime permission with actions that works on all Java versions.
	 *
	 * @param name    the permission name
	 * @param actions the permission actions
	 * @return a compatible runtime permission
	 */
	public static Permission createRuntimePermission(String name, String actions) {
		if (hasRuntimePermission()) {
			try {
				// Use reflection to create a RuntimePermission
				Class<?> permClass = Class.forName("java.security.RuntimePermission");
				return (Permission) permClass.getConstructor(String.class, String.class).newInstance(name, actions);
			} catch (Exception e) {
				Logger.logError("Error creating RuntimePermission, falling back to custom implementation", e);
				return new RobocodeRuntimePermission(name, actions);
			}
		} else {
			return new RobocodeRuntimePermission(name, actions);
		}
	}
}
