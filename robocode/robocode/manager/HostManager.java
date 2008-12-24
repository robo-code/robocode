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


import net.sf.robocode.peer.*;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.serialization.RbSerializer;
import robocode.control.RobotSpecification;
import robocode.peer.RobotStatics;
import robocode.peer.proxies.*;
import net.sf.robocode.repository.IRobotFileSpecification;

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

	public IHostingRobotProxy createRobotProxy(RobotSpecification robotSpecification, RobotStatics statics, IRobotPeer peer) {
		IHostingRobotProxy robotProxy;
		final IRobotFileSpecification specification = (IRobotFileSpecification) HiddenAccess.getFileSpecification(
				robotSpecification);

		if (specification.isTeamRobot()) {
			robotProxy = new TeamRobotProxy(specification, this, peer, statics);
		} else if (specification.isAdvancedRobot()) {
			robotProxy = new AdvancedRobotProxy(specification, this, peer, statics);
		} else if (specification.isStandardRobot()) {
			robotProxy = new StandardRobotProxy(specification, this, peer, statics);
		} else if (specification.isJuniorRobot()) {
			robotProxy = new JuniorRobotProxy(specification, this, peer, statics);
		} else {
			throw new AccessControlException("Unknown robot type");
		}
		return robotProxy;
	}

	static {
		RbSerializer.register(ExecCommands.class, RbSerializer.ExecCommands_TYPE);
		RbSerializer.register(BulletCommand.class, RbSerializer.BulletCommand_TYPE);
		RbSerializer.register(TeamMessage.class, RbSerializer.TeamMessage_TYPE);
		RbSerializer.register(DebugProperty.class, RbSerializer.DebugProperty_TYPE);
		RbSerializer.register(ExecResults.class, RbSerializer.ExecResults_TYPE);
		RbSerializer.register(BulletStatus.class, RbSerializer.BulletStatus_TYPE);
	}

	public void cleanup() {// TODO
	}
}
