/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocodeui.security;

import robocode.ui.ISecurityExtension;
import robocode.manager.RobocodeManager;
import robocode.security.RobocodeSecurityManager;

/**
 * @author Pavel Savara (original)
 */
public class SecurityExtension implements ISecurityExtension {
	private RobocodeManager manager;
	private RobocodeSecurityManager securityManager ;

	public void initialize() {
		securityManager = (RobocodeSecurityManager) System.getSecurityManager();
		securityManager.addSafeContext();
	}

	public void setRobocodeManager(RobocodeManager robocodeManager) {
		manager = robocodeManager;
	}
}
