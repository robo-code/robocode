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


import robocode.peer.proxies.*;
import robocode.peer.IRobotPeer;
import robocode.peer.RobotStatics;
import robocode.peer.robot.RobotClassManager;
import robocode.repository.RobotFileSpecification;

import java.security.AccessControlException;


/**
 * @author Pavel Savara (original)
 */
public class HostManager implements IHostManager {
	private final RobocodeManager manager;

	HostManager(RobocodeManager manager) {
		this.manager = manager;
	}

	public long getRobotFilesystemQuota() {
		return manager.getProperties().getRobotFilesystemQuota();
	}

	public IThreadManager getThreadManager() {
		return RobocodeManager.getThreadManager();
	}

	public IHostingRobotProxy createRobotProxy(RobotClassManager robotClassManager, RobotStatics statics, IRobotPeer peer) {
		IHostingRobotProxy robotProxy;
		RobotFileSpecification robotSpecification = robotClassManager.getRobotSpecification();

		if (robotSpecification.isTeamRobot()) {
			robotProxy = new TeamRobotProxy(robotClassManager, this, peer, statics);
		} else if (robotSpecification.isAdvancedRobot()) {
			robotProxy = new AdvancedRobotProxy(robotClassManager, this, peer, statics);
		} else if (robotSpecification.isStandardRobot()) {
			robotProxy = new StandardRobotProxy(robotClassManager, this, peer, statics);
		} else if (robotSpecification.isJuniorRobot()) {
			robotProxy = new JuniorRobotProxy(robotClassManager, this, peer, statics);
		} else {
			throw new AccessControlException("Unknown robot type");
		}
		return robotProxy;
	}

	public void cleanup() {// TODO
	}
}
