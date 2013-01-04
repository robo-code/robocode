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


import net.sf.robocode.core.Container;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.URLJarCollector;
import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.items.TeamItem;
import net.sf.robocode.repository.items.BaseItem;
import net.sf.robocode.repository.packager.JarCreator;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.repository.root.handlers.RootHandler;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.settings.ISettingsListener;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.ui.IWindowManager;
import net.sf.robocode.version.IVersionManager;
import robocode.control.RobotSpecification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * @author Pavel Savara (original)
 */
public class RepositoryManager implements IRepositoryManager {

	private static final String DATABASE_FILENAME = "robot.database";
	
	private final ISettingsManager properties;
	private Repository repository;

	public RepositoryManager(ISettingsManager properties) {
		this.properties = properties;
		properties.addPropertyListener(new SettingsListener());
	}

	// ------------------------------------------
	// interfaces
	// ------------------------------------------

	public File getRobotsDirectory() {
		return FileUtil.getRobotsDir();
	}

	public List<File> getDevelDirectories() {
		List<File> develDirectories = new ArrayList<File>();

		for (String path : properties.getOptionsEnabledDevelopmentPaths()) {
			try {
				develDirectories.add(new File(path).getCanonicalFile());
			} catch (IOException e) {
				Logger.logError(e);
			}
		}
		return develDirectories;
	}

	public void refresh(String friendlyUrl) {
		if (!updateItemRoot(friendlyUrl, true)) {
			refresh(true);
		}
		URLJarCollector.gc();
	}

	public boolean refresh() {
		return refresh(false);
	}

	public boolean refresh(boolean force) {
		boolean refreshed = update(getRobotsDirectory(), getDevelDirectories(), force);
		if (refreshed) {
			setStatus("Saving robot database");
			save();
		}

		setStatus("");
		URLJarCollector.gc();

		return refreshed;
	}

	private boolean update(File robotsDir, Collection<File> devDirs, boolean force) {
		final int prev = repository.getItems().size();

		RootHandler.openHandlers();

		Map<String, IRepositoryRoot> currentRoots = repository.getRoots();
		Map<String, IRepositoryRoot> newRoots = new HashMap<String, IRepositoryRoot>();

		RootHandler.visitDirectories(robotsDir, false, newRoots, repository, force);
		for (File dir : devDirs) {
			RootHandler.visitDirectories(dir, true, newRoots, repository, force);
		}

		// removed roots
		for (IRepositoryRoot oldRoot : currentRoots.values()) {
			if (!newRoots.containsKey(oldRoot.getURL().toString())) {
				repository.removeItemsFromRoot(oldRoot);
			}
		}

		repository.setRoots(newRoots);

		RootHandler.closeHandlers();

		return prev != repository.getItems().size();
	}

	private boolean updateItemRoot(String friendlyUrl, boolean force) {
		IItem item = repository.getItems().get(friendlyUrl);
		if (item != null) {
			item.getRoot().update(item, force);
			return true;
		}
		return false; 
	}

	private void save() {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(getRobotsDirectory(), DATABASE_FILENAME));
			repository.save(fos);
		} catch (IOException e) {
			Logger.logError("Can't save robot database", e);
		} finally {
			FileUtil.cleanupStream(fos);
		}
	}

	private Repository load() {
		Repository repository = new Repository();
		
		FileInputStream fis = null;
		try {
			File file = new File(getRobotsDirectory(), DATABASE_FILENAME);
			if (file.exists()) {
				fis = new FileInputStream(file);
				repository.load(fis);
			}
		} catch (IOException e) {
			Logger.logError("Can't load robot database", e);
			repository = null;
		} finally {
			FileUtil.cleanupStream(fis);
		}
		return repository;
	}
	
	public void reload(boolean rebuild) {
		// Bug fix [2867326] - Lockup on start if too many bots in robots dir (cont'd).
		URLJarCollector.enableGc(true);
		URLJarCollector.gc();

		if (rebuild) {
			Logger.logMessage("Rebuilding robot database...");
			repository = new Repository();
		} else if (repository == null) {
			setStatus("Reading robot database");
			repository = load();
			if (repository == null) {
				setStatus("Building robot database");
				repository = new Repository();
			}
		}
		refresh(true);
		setStatus("");
	}

	public RobotSpecification[] getSpecifications() {
		checkDbExists();
		final Collection<IRepositoryItem> list = getAllValidItems();
		Collection<RobotSpecification> res = new ArrayList<RobotSpecification>();

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
		Collection<RobotSpecification> battlingRobotsList = new ArrayList<RobotSpecification>();
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
		Collection<RobotSpecification> battlingRobotsList = new ArrayList<RobotSpecification>();
		final Collection<IRepositoryItem> list = getValidItems(selectedRobots);
		int teamNum = 0;

		for (IRepositoryItem item: list) {
			loadItem(battlingRobotsList, null, item, teamNum);
			teamNum++;
		}
		return battlingRobotsList.toArray(new RobotSpecification[battlingRobotsList.size()]);
	}

	private boolean loadItem(Collection<RobotSpecification> battlingRobotsList, RobotSpecification spec, IRepositoryItem item, int teamNum) {
		String teamId = String.format("%4d", teamNum);

		if (item != null) {
			if (item.isTeam()) {
				teamId = item.getFullClassNameWithVersion() + "[" + teamId + "]";
				final Collection<RobotItem> members = getRobotItems((TeamItem) item);

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
		Collection<RobotSpecification> battlingRobotsList = new ArrayList<RobotSpecification>();
		final Collection<IRepositoryItem> list = getValidItems(selectedRobots);

		for (IRepositoryItem item: list) {
			battlingRobotsList.add(item.createRobotSpecification());
		}
		return battlingRobotsList.toArray(new RobotSpecification[battlingRobotsList.size()]);
	}

	public List<IRepositoryItem> getSelectedSpecifications(String selectedRobots) {
		checkDbExists();
		return getValidItems(selectedRobots);
	}

	private Collection<IRepositoryItem> getAllValidItems() {
		final ArrayList<IRepositoryItem> res = new ArrayList<IRepositoryItem>();

		for (IItem item : repository.getItems().values()) {
			final IRepositoryItem spec = (IRepositoryItem) item;
			if (item.isValid() && !res.contains(spec)) {
				res.add(spec);
			}
		}
		return res;
	}

	private List<IRepositoryItem> getValidItems(String friendlyUrls) {
		List<IRepositoryItem> result = new ArrayList<IRepositoryItem>();
		StringTokenizer tokenizer = new StringTokenizer(friendlyUrls, ",");

		while (tokenizer.hasMoreTokens()) {
			String friendlyUrl = tokenizer.nextToken().trim();

			IItem item = repository.getItem(friendlyUrl);
			if (item != null) {
				if (item.isValid()) {
					result.add((IRepositoryItem) item);
				} else {
					Logger.logError("Can't load '" + friendlyUrl + "' because it is an invalid robot or team.");
				}
			} else {
				Logger.logError("Can't find '" + friendlyUrl + '\'');
			}
		}
		return result;
	}

	public List<IRepositoryItem> getRepositoryItems(boolean onlyWithSource, boolean onlyWithPackage, boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean ignoreTeamRobots, boolean onlyInJar) {
		checkDbExists();

		final List<IRepositoryItem> res = new ArrayList<IRepositoryItem>();

		for (IItem item : repository.getItems().values()) {
			final IRepositoryItem spec = (IRepositoryItem) item;

			if (!item.isValid()) {
				continue;
			}
			if (onlyWithSource && !spec.isSourceIncluded()) {
				continue;
			}
			if (onlyWithPackage && spec.getFullPackage() == null) {
				continue;
			}
			if (onlyInJar && !spec.isInJAR()) {
				continue;
			}
			if (onlyRobots && !(item instanceof RobotItem)) {
				continue;
			}
			if (onlyDevelopment && !spec.isDevelopmentVersion()) {
				continue;
			}
			if (onlyNotDevelopment && spec.isDevelopmentVersion()) {
				continue;
			}
			if (res.contains(spec)) {
				continue;
			}
			res.add(spec);
		}
		Collections.sort(res);
		return res;
	}

	public boolean verifyRobotName(String robotName, String shortClassName) {
		return RobotItem.verifyRobotName(robotName, shortClassName, true);
	}

	public int extractJar(IRepositoryItem item) {
		if (!item.isInJAR()) {
			return -2;
		}
		((BaseItem) item).getRoot().extractJAR();
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
		final List<RobotItem> robots = getAllRobotItems(selectedRobots);
		final List<TeamItem> teams = getTeamItemsOnly(selectedRobots);

		final String res = JarCreator.createPackage(target, source, robots, teams, web, desc, author, version);

		refresh(target.toURI().toString());
		return res;
	}

	private Collection<RobotItem> getRobotItems(TeamItem team) {
		Collection<RobotItem> result = new ArrayList<RobotItem>();
		StringTokenizer teamTokenizer = new StringTokenizer(team.getMembers(), ",");

		while (teamTokenizer.hasMoreTokens()) {
			String botNameAndVersion = teamTokenizer.nextToken();
			int versionIndex = botNameAndVersion.indexOf(' ');
			String botPath = versionIndex < 0 ? botNameAndVersion : botNameAndVersion.substring(0, versionIndex);

			botPath = botPath.replace('.', '/').replaceAll("\\*", "");

			// first load from same classPath
			String teamBot = team.getRoot().getURL() + botPath;
			IItem res = repository.getItem(teamBot);

			if (res != null && res instanceof RobotItem) {
				result.add((RobotItem) res);
				continue;
			}

			// try general search
			res = repository.getItem(botNameAndVersion);
			if (res != null && res instanceof RobotItem) {
				result.add((RobotItem) res);
				continue;
			}

			// not found
			Logger.logError("Can't find robot: " + botNameAndVersion);
		}
		return result;
	}

	private List<RobotItem> getAllRobotItems(Collection<IRepositoryItem> items) {
		List<RobotItem> result = new ArrayList<RobotItem>();

		for (IRepositoryItem item : items) {
			if (item.isTeam()) {
				result.addAll(getRobotItems((TeamItem) item));
			} else {
				result.add((RobotItem) item);
			}
		}
		return result;
	}

	private static List<TeamItem> getTeamItemsOnly(Collection<IRepositoryItem> items) {
		List<TeamItem> result = new ArrayList<TeamItem>();

		for (IRepositoryItem item : items) {
			if (item.isTeam()) {
				result.add((TeamItem) item);
			}
		}
		return result;
	}

	private IRepositoryItem getRobot(String fullClassNameWithVersion) {
		final IItem item = repository.getItem(fullClassNameWithVersion);

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
		if (repository == null) {
			reload(false);
		}
	}

	// ------------------------------------------
	// settings listener
	// ------------------------------------------

	private class SettingsListener implements ISettingsListener {

		private Collection<String> lastEnabledDevelPaths;

		public void settingChanged(String property) {
			if (property.equals(ISettingsManager.OPTIONS_DEVELOPMENT_PATH)
					|| property.equals(ISettingsManager.OPTIONS_DEVELOPMENT_PATH_EXCLUDED)) {

				Collection<String> enabledDevelPaths = properties.getOptionsEnabledDevelopmentPaths();

				if (lastEnabledDevelPaths == null || !enabledDevelPaths.equals(lastEnabledDevelPaths)) {
					lastEnabledDevelPaths = enabledDevelPaths;
					reload(true);
				}
			}
		}
	}
}
