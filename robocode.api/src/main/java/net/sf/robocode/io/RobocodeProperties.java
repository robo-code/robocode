/*******************************************************************************
 * Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.io;


/**
 * Utility class for convenient access to Robocode system wide properties.
 * 
 * @author Flemming N. Larsen (original)
 */
public final class RobocodeProperties {

	private static final boolean NO_SECURITY = System.getProperty("NOSECURITY", "false").equals("true");
	private static final boolean DEBUG = System.getProperty("debug", "false").equals("true");

	private RobocodeProperties() {}

	/**
	 * Checks if security is off, i.e. that the <code>NOSECURITY</code> property has been set to <code>true</code>.
	 * @return true if security is disabled; false if security is enabled.
	 */
	public static boolean isSecurityOff() {
		return NO_SECURITY;
	}

	/**
	 * Checks if security is on, i.e. that the <code>NOSECURITY</code> property has been set to <code>false</code> or not defined at all.
	 * @return true if security is enabled; false if security is disabled.
	 */
	public static boolean isSecurityOn() {
		return !NO_SECURITY;
	}

	/**
	 * Checks if debugging is disabled.
	 * @return true if debugging is disabled; false is debugging is enabled.
	 */
	public static boolean isDebuggingOff() {
		return !DEBUG;
	}

	/**
	 * Checks if debugging is enabled.
	 * @return true if debugging is enabled; false is debugging is disabled.
	 */
	public static boolean isDebuggingOn() {
		return DEBUG;
	}
}
