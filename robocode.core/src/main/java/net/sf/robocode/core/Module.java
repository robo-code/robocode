/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.core;


import net.sf.robocode.host.RobotStatics;
import net.sf.robocode.peer.*;
import net.sf.robocode.serialization.RbSerializer;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.settings.SettingsManager;
import net.sf.robocode.version.IVersionManager;
import net.sf.robocode.version.VersionManager;

import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public class Module extends BaseModule {
	static {
		Container.cache.addComponent(RobocodeMain.class);
		Container.cache.addComponent(IVersionManager.class, VersionManager.class);
		Container.cache.addComponent(ISettingsManager.class, SettingsManager.class);
	}

	@Override
	public void afterLoaded(List<IModule> allModules) {
		RbSerializer.register(ExecCommands.class, RbSerializer.ExecCommands_TYPE);
		RbSerializer.register(BulletCommand.class, RbSerializer.BulletCommand_TYPE);
		RbSerializer.register(TeamMessage.class, RbSerializer.TeamMessage_TYPE);
		RbSerializer.register(DebugProperty.class, RbSerializer.DebugProperty_TYPE);
		RbSerializer.register(ExecResults.class, RbSerializer.ExecResults_TYPE);
		RbSerializer.register(BulletStatus.class, RbSerializer.BulletStatus_TYPE);
		RbSerializer.register(RobotStatics.class, RbSerializer.RobotStatics_TYPE);
	}
}
