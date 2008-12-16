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


import robocode.repository.FileSpecification;
import robocode.control.RobotSpecification;

import java.io.File;
import java.util.List;

import net.sf.robocode.repository.IRepositoryManagerBase;


/**
 * @author Pavel Savara (original)
 */
public interface IRepositoryManager extends IRepositoryManagerBase {
	File getRobotCache();

	File getRobotsDirectory();

	void clearRobotList();

	void loadRobotRepository();

	boolean cleanupOldSampleRobots(boolean delete);

	RobocodeManager getManager();

	List<FileSpecification> getRobotSpecificationsList();
	List<FileSpecification> getRobotSpecificationsList(boolean onlyWithSource, boolean onlyWithPackage,
			boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean ignoreTeamRobots);

	FileSpecification getRobot(String fullClassNameWithVersion);
}
