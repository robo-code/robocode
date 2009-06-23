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
package net.sf.robocode.repository;


import net.sf.robocode.core.Container;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.URLJarCollector;
import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.items.TeamItem;
import net.sf.robocode.repository.items.BaseItem;
import net.sf.robocode.repository.packager.JarCreator;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.settings.ISettingsListener;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.ui.IWindowManager;
import net.sf.robocode.version.IVersionManager;
import robocode.control.RobotSpecification;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * @author Pavel Savara (original)
 */
public class RepositoryManager implements IRepositoryManager {

	private final ISettingsManager properties;
	private Database db;

	public RepositoryManager(ISettingsManager properties) {
		this.properties = properties;
		properties.addPropertyListener(new ISettingsListener() {
			public void settingChanged(String property) {
				if (property.equals(ISettingsManager.OPTIONS_DEVELOPMENT_PATH)) {
					reload(false);
				}
			}
		});
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

	public void refresh(String file) {
		if (!db.update(file, true)) {
			refresh(true);
		}
		URLJarCollector.gc();
	}

	public boolean refresh() {
		return refresh(false);
	}

	public boolean refresh(boolean updateInvalid) {
		if (db.update(getRobotsDirectory(), getDevelDirectories(), updateInvalid)) {
			setStatus("Saving robot database");
			db.save();
			setStatus("");
			URLJarCollector.gc();
			return true;
		}
		setStatus("");
		URLJarCollector.gc();
		return false;
	}

	public void reload(boolean forced) {
		if (forced) {
			db = new Database(this);
			Logger.logMessage("Rebuilding robot database...");
		} else if (db == null) {
			setStatus("Reading robot database");
			db = Database.load(this);
			if (db == null) {
				setStatus("Building robot database");
				db = new Database(this);
			}
		}
		refresh(true);
		setStatus("");
	}

	public RobotSpecification[] getSpecifications() {
		checkDbExists();
		final List<IRepositoryItem> list = db.getAllSpecifications();
		List<RobotSpecification> res = new ArrayList<RobotSpecification>();

		for (IRepositoryItem s : list) {
			res.add(s.createRobotSpecification());
		}
		return res.toArray(new RobotSpecification[res.size()]);
	}

	/**
	 * Expand teams, validate robots
	 * @param selectedRobots, names of robots and teams, comma separated
	 * @return robots in teams
	 */
	public RobotSpecification[] loadSelectedRobots(RobotSpecification[] selectedRobots) {
		checkDbExists();
		List<RobotSpecification> battlingRobotsList = new ArrayList<RobotSpecification>();
		int teamNum = 0;

		for (RobotSpecification spec: selectedRobots) {
			IRepositoryItem item = (IRepositoryItem) HiddenAccess.getFileSpecification(spec);

			if (item == null) {
				item = getRobot(spec.getNameAndVersion());
			}
			loadItem(battlingRobotsList, spec, item, teamNum);
			teamNum++;
		}
		return battlingRobotsList.toArray(new RobotSpecification[battlingRobotsList.size()]);
	}

	/**
	 * Expand teams, validate robots
	 * @param selectedRobots, names of robots and teams, comma separated
	 * @return robots in teams
	 */
	public RobotSpecification[] loadSelectedRobots(String selectedRobots) {
		checkDbExists();
		List<RobotSpecification> battlingRobotsList = new ArrayList<RobotSpecification>();
		final List<IRepositoryItem> list = db.getSelectedSpecifications(selectedRobots);
		int teamNum = 0;

		for (IRepositoryItem item: list) {
			loadItem(battlingRobotsList, null, item, teamNum);
			teamNum++;
		}
		return battlingRobotsList.toArray(new RobotSpecification[battlingRobotsList.size()]);
	}

	private boolean loadItem(List<RobotSpecification> battlingRobotsList, RobotSpecification spec, IRepositoryItem item, int teamNum) {
		String teamId = String.format("%4d", teamNum);

		if (item != null) {
			if (item.isTeam()) {
				teamId = item.getFullClassNameWithVersion() + "[" + teamId + "]";
				final List<RobotItem> members = db.expandTeam((TeamItem) item);

				for (IRepositoryItem member : members) {
					final RobotItem robot = (RobotItem) member;

					boolean tested = false;

					for (RobotSpecification loaded : battlingRobotsList) {
						if (HiddenAccess.getFileSpecification(loaded).equals(robot)) {
							tested = true;
							break;
						}
					}

					if (tested || robot.validate()) {
						battlingRobotsList.add(robot.createRobotSpecification(null, teamId));
					}
				}
			} else {
				final RobotItem robot = (RobotItem) item;

				if (robot.validate()) {
					battlingRobotsList.add(robot.createRobotSpecification(spec, null));
				} else {
					Logger.logError("Could not load robot: " + robot.getFullClassName());
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * @param selectedRobots, names of robots and teams, comma separated
	 * @return robots and teams
	 */
	public RobotSpecification[] getSelectedRobots(String selectedRobots) {
		checkDbExists();
		List<RobotSpecification> battlingRobotsList = new ArrayList<RobotSpecification>();
		final List<IRepositoryItem> list = db.getSelectedSpecifications(selectedRobots);

		for (IRepositoryItem item: list) {
			battlingRobotsList.add(item.createRobotSpecification());
		}
		return battlingRobotsList.toArray(new RobotSpecification[battlingRobotsList.size()]);
	}

	public List<IRepositoryItem> getSelectedSpecifications(String selectedRobots) {
		checkDbExists();
		return db.getSelectedSpecifications(selectedRobots);
	}

	public List<IRepositoryItem> filterRepositoryItems(boolean onlyWithSource, boolean onlyWithPackage, boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean ignoreTeamRobots, boolean onlyInJar) {
		checkDbExists();
		return db.filterSpecifications(onlyWithSource, onlyWithPackage, onlyRobots, onlyDevelopment, onlyNotDevelopment,
				onlyInJar);
	}

	public boolean verifyRobotName(String robotName, String shortClassName) {
		return RobotItem.verifyRobotName(robotName, shortClassName, true);
	}

	public int extractJar(IRepositoryItem item) {
		if (!item.isInJar()) {
			return -2;
		}
		((BaseItem) item).getRoot().extractJar();
		return 0; 
	}

	public void createTeam(File target, URL web, String desc, String author, String members, String teamVersion) throws IOException {
		checkDbExists();
		final String ver = Container.getComponent(IVersionManager.class).getVersion();

		TeamItem.createOrUpdateTeam(target, web, desc, author, members, teamVersion, ver);
		refresh(target.toURI().toString());
	}

	public String createPackage(File target, URL web, String desc, String author, String version, boolean source, List<IRepositoryItem> selectedRobots) {
		checkDbExists();
		final List<RobotItem> robots = db.expandTeams(selectedRobots);
		final List<TeamItem> teams = db.filterTeams(selectedRobots);

		final String res = JarCreator.createPackage(target, source, robots, teams, web, desc, author, version);

		refresh(target.toURI().toString());
		return res;
	}

	private IRepositoryItem getRobot(String fullClassNameWithVersion) {
		final IItem item = db.getItem(fullClassNameWithVersion);

		if (item == null || !item.isValid()) {
			return null;
		}
		return (IRepositoryItem) item;
	}

	private void setStatus(String message) {
		IWindowManager windowManager = Container.getComponent(IWindowManager.class);

		if (windowManager != null) {
			windowManager.setStatus(message);
		}
		if (message.length() > 0) {
			Logger.logMessage(message);
		}
	}

	private void checkDbExists() {
		if (db == null) {
			reload(false);
		}
	}
}
