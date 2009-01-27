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
package net.sf.robocode.repository;


import robocode.control.RobotSpecification;

import java.io.File;
import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public interface IRepositoryManager extends IRepositoryManagerBase {
	File getRobotsDirectory();

	List<File> getDevelDirectories();

	void clearRobotList();

	void reload(String file);

	void loadRobotRepository();

	List<INamedFileSpecification> getRobotSpecificationsList();
	List<INamedFileSpecification> getRobotSpecificationsList(boolean onlyWithSource, boolean onlyWithPackage,
			boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean ignoreTeamRobots);

	boolean load(List<RobotSpecification> battlingRobotsList, String bot, RobotSpecification battleRobotSpec, int teamNum);

	boolean verifyRobotName(String robotName, String shortClassName);
	int extractJar(File jarFile);

	INamedFileSpecification createTeam();
}
