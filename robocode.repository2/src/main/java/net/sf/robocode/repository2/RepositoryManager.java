/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.repository2;


import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.repository.INamedFileSpecification;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.settings.ISettingsListener;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.repository2.items.RobotItem;
import net.sf.robocode.repository2.items.IItem;
import net.sf.robocode.repository2.items.TeamItem;

import java.io.File;
import java.io.IOException;
import java.util.*;

import robocode.control.RobotSpecification;


/**
 * @author Pavel Savara (original)
 */
public class RepositoryManager implements IRepositoryManager {

	private ISettingsManager properties;
	private Database db;

	public RepositoryManager(ISettingsManager properties) {
		this.properties = properties;
		properties.addPropertyListener(new ISettingsListener() {
			public void settingChanged(String property) {
				if (property.equals(ISettingsManager.OPTIONS_DEVELOPMENT_PATH)) {
					// TODO just devel
					clearRobotList();
				}
			}
		});
		db = new Database();
	}

	// ------------------------------------------
	// interfaces
	// ------------------------------------------

	public File getRobotsDirectory() {
		return FileUtil.getRobotsDir();
	}

	public List<File> getDevelDirectories() {
		List<File> develDirectories;

		develDirectories = new ArrayList<File>();
		String externalPath = properties.getOptionsDevelopmentPath();
		StringTokenizer tokenizer = new StringTokenizer(externalPath, File.pathSeparator);

		while (tokenizer.hasMoreTokens()) {
			try {
				File f = new File(tokenizer.nextToken()).getCanonicalFile();

				develDirectories.add(f);
			} catch (IOException e) {
				throw new Error(e);
			}
		}
		return develDirectories;
	}

	public void clearRobotList() {
		db = new Database();
		// TODO persist ?
	}

	public void reload(String file) {
		db.update(file, true);
	}

	public void loadRobotRepository() {
		db.update(getRobotsDirectory(), getDevelDirectories());
	}

	public List<INamedFileSpecification> getRobotSpecificationsList() {
		return db.getRobotSpecificationsList();
	}

	public RobotSpecification[] getRobotSpecifications() {
		final List<INamedFileSpecification> list = db.getRobotSpecificationsList();
		List<RobotSpecification> res = new ArrayList<RobotSpecification>();

		for (INamedFileSpecification s : list) {
			res.add(s.createRobotSpecification());
		}
		return res.toArray(new RobotSpecification[res.size()]);
	}

	public List<INamedFileSpecification> getRobotSpecificationsList(boolean onlyWithSource, boolean onlyWithPackage, boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean ignoreTeamRobots) {
		return null; // TODO ZAMO
	}

	public boolean load(List<RobotSpecification> battlingRobotsList, String bot, RobotSpecification battleRobotSpec, int teamNum) {
		return load(battlingRobotsList, bot, battleRobotSpec, String.format("%4d", teamNum), false);
	}

	public boolean verifyRobotName(String robotName, String shortClassName) {
		return RobotItem.verifyRobotName(robotName, shortClassName);
	}

	public int extractJar(File jarFile) {
		return 0; // TODO ZAMO
	}

	public INamedFileSpecification createTeam() {
		return null; // TODO ZAMO
	}

	private boolean load(List<RobotSpecification> battlingRobotsList, String bot, RobotSpecification battleRobotSpec, String teamName, boolean inTeam) {
		final INamedFileSpecification fileSpec = getRobot(bot);

		if (fileSpec != null) {
			if (fileSpec instanceof RobotItem) {
				RobotSpecification specification;

				if (!inTeam && battleRobotSpec != null) {
					specification = battleRobotSpec;
				} else {
					specification = fileSpec.createRobotSpecification();
				}
				HiddenAccess.setTeamName(specification, inTeam ? teamName : null);
				battlingRobotsList.add(specification);
				return true;
			} else if (fileSpec instanceof TeamItem) {
				TeamItem currentTeam = (TeamItem) fileSpec;
				String version = currentTeam.getVersion();

				if (version == null) {
					version = "";
				}

				final String newTeam = currentTeam.getFullClassName() + version + "[" + teamName + "]";
				StringTokenizer teamTokenizer = new StringTokenizer(currentTeam.getMembers(), ",");

				while (teamTokenizer.hasMoreTokens()) {
					final String botName = teamTokenizer.nextToken();
					// first load from same classPath
					String teamBot = currentTeam.getRoot().getUrl() + botName.replace('.', '/');

					if (!load(battlingRobotsList, teamBot, battleRobotSpec, newTeam, true)) {
						// try general search
						load(battlingRobotsList, botName, battleRobotSpec, newTeam, true);
					}
				}
				return true;
			}
		}
		return false;
	}

	private INamedFileSpecification getRobot(String fullClassNameWithVersion) {
		loadRobotRepository();
		final IItem item = db.getItem(fullClassNameWithVersion);

		if (item == null || !item.isValid()) {
			return null;
		}
		return (INamedFileSpecification) item;
	}
}
