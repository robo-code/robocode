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
package robocode.manager;


/**
 * @author Pavel Savara (original)
 */
public class HostManager {
	private RobocodeManager manager;
    
	HostManager(RobocodeManager manager) {
		this.manager = manager;
	}

	public long getRobotFilesystemQuota() {
		return manager.getProperties().getRobotFilesystemQuota();
	}

	public ThreadManager getThreadManager() {
		return manager.getThreadManager(); 
	}

	public void cleanup() {// TODO
	}
}
