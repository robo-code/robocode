/*******************************************************************************
 * Copyright (c) 2001-2012 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.repository;


import robocode.control.RobotSpecification;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public interface IRepositoryManager extends IRepositoryManagerBase {
	File getRobotsDirectory();

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

	void createTeam(File target, URL web, String desc, String author, String members, String teamVersion) throws IOException;

	String createPackage(File target, URL web, String desc, String author, String version, boolean source, List<IRobotSpecItem> selectedRobots);

}
