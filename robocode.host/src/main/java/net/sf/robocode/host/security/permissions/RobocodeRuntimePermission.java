/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security.permissions;

import java.security.BasicPermission;

/**
 * A custom implementation of runtime permission for Robocode that works in Java 24+
 * where the SecurityManager and associated permission classes are removed.
 * <p>
 * This class replicates the essential functionality of the original RuntimePermission
 * for the specific needs of Robocode's security system.
 *
 * @author Flemming N. Larsen (original)
 */
public class RobocodeRuntimePermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new RobocodeRuntimePermission with the specified name.
	 *
	 * @param name the name of the permission
	 */
	public RobocodeRuntimePermission(String name) {
		super(name);
	}

	/**
	 * Creates a new RobocodeRuntimePermission with the specified name and actions.
	 *
	 * @param name    the name of the permission
	 * @param actions the actions string (ignored for this type of permission)
	 */
	public RobocodeRuntimePermission(String name, String actions) {
		super(name, actions);
	}
}
