/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository;


import robocode.control.RobotSpecification;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public interface IRepositoryManager extends IRepositoryManagerBase {

	/**
	 * Returns the directory containing the robots.
	 *
	 * @return a file that is the directory containing the robots.
	 */
	File getRobotsDirectory();

	/**
	 * Returns all development directories that are additional directories to the 'robots' directory.
	 *
	 * @return a list of files containing development directories.
	 */
	List<File> getDevelDirectories();

	void refresh(String friendlyUrl);

	boolean refresh(boolean force);
	void reload(boolean forced);

	List<IRobotSpecItem> getRepositoryItems(boolean onlyWithSource, boolean onlyWithPackage,
			boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean ignoreTeamRobots, boolean onlyInJar);

	RobotSpecification[] loadSelectedRobots(RobotSpecification[] selectedRobots);

	List<IRobotSpecItem> getSelectedSpecifications(String selectedRobots);

	boolean verifyRobotName(String robotName, String shortClassName);

	int extractJar(IRobotSpecItem item);

	void createTeam(File target, TeamProperties teamProperties) throws IOException;

	String createPackage(File jarFile, List<IRobotSpecItem> selectedRobots, RobotProperties robotProperties);
}
