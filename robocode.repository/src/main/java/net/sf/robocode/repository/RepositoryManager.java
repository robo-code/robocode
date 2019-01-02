/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository;


import net.sf.robocode.core.Container;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.URLJarCollector;
import net.sf.robocode.repository.items.IRepositoryItem;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.items.TeamItem;
import net.sf.robocode.repository.items.RepositoryItem;
import net.sf.robocode.repository.packager.JarCreator;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.repository.root.handlers.RootHandler;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.settings.ISettingsListener;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.ui.IWindowManager;
import robocode.control.RobotSpecification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
public class RepositoryManager implements IRepositoryManager { // NO_UCD (use default)

	private static final String DATABASE_FILENAME = "robot.database";
	
	private final ISettingsManager properties;
	private Repository repository;

	public RepositoryManager(ISettingsManager properties) { // NO_UCD (unused code)
		this.properties = properties;
		properties.addPropertyListener(new SettingsListener());
	}

	// ------------------------------------------
	// interfaces
	// ------------------------------------------

	@Override
	public File getRobotsDirectory() {
		return FileUtil.getRobotsDir();
	}

	@Override
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
		try {
			Map<String, IRepositoryRoot> newRoots = new HashMap<String, IRepositoryRoot>();
	
			RootHandler.visitDirectories(robotsDir, false, newRoots, repository, force);
			for (File dir : devDirs) {
				RootHandler.visitDirectories(dir, true, newRoots, repository, force);
			}
			repository.setRoots(newRoots);
		} finally {
			RootHandler.closeHandlers();
		}

		return prev != repository.getItems().size();
	}

	private boolean updateItemRoot(String friendlyUrl, boolean force) {
		IRepositoryItem repositoryItem = repository.getItems().get(friendlyUrl);
		if (repositoryItem != null) {
			repositoryItem.getRoot().updateItem(repositoryItem, force);
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
		final Collection<IRobotSpecItem> list = getAllValidItems();
		Collection<RobotSpecification> res = new ArrayList<RobotSpecification>();

		for (IRobotSpecItem s : list) {
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
			IRobotSpecItem item = (IRobotSpecItem) HiddenAccess.getFileSpecification(spec);

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
		final Collection<IRobotSpecItem> list = getValidItems(selectedRobots);
		int teamNum = 0;

		for (IRobotSpecItem item: list) {
			loadItem(battlingRobotsList, null, item, teamNum);
			teamNum++;
		}
		return battlingRobotsList.toArray(new RobotSpecification[battlingRobotsList.size()]);
	}

	private boolean loadItem(Collection<RobotSpecification> battlingRobotsList, RobotSpecification spec, IRobotSpecItem item, int teamNum) {
		String teamId = String.format("%4d", teamNum);

		if (item != null) {
			if (item.isTeam()) {
				teamId = item.getFullClassNameWithVersion() + "[" + teamId + "]";
				final Collection<RobotItem> members = getRobotItems((TeamItem) item);

				for (IRobotSpecItem member : members) {
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

	public List<IRobotSpecItem> getSelectedSpecifications(String selectedRobots) {
		checkDbExists();
		return getValidItems(selectedRobots);
	}

	private Collection<IRobotSpecItem> getAllValidItems() {
		final ArrayList<IRobotSpecItem> res = new ArrayList<IRobotSpecItem>();

		for (IRepositoryItem repositoryItem : repository.getItems().values()) {
			final IRobotSpecItem spec = (IRobotSpecItem) repositoryItem;
			if (repositoryItem.isValid() && !res.contains(spec)) {
				res.add(spec);
			}
		}
		return res;
	}

	private List<IRobotSpecItem> getValidItems(String friendlyUrls) {
		List<IRobotSpecItem> result = new ArrayList<IRobotSpecItem>();
		StringTokenizer tokenizer = new StringTokenizer(friendlyUrls, ",");

		while (tokenizer.hasMoreTokens()) {
			String friendlyUrl = tokenizer.nextToken().trim();

			IRepositoryItem repositoryItem = repository.getItem(friendlyUrl);
			if (repositoryItem != null) {
				if (repositoryItem.isValid()) {
					result.add((IRobotSpecItem) repositoryItem);
				} else {
					Logger.logError("Can't load '" + friendlyUrl + "' because it is an invalid robot or team.");
				}
			} else {
				Logger.logError("Can't find '" + friendlyUrl + '\'');
			}
		}
		return result;
	}

	public List<IRobotSpecItem> getRepositoryItems(boolean onlyWithSource, boolean onlyWithPackage, boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean ignoreTeamRobots, boolean onlyInJar) {
		checkDbExists();

		final List<IRobotSpecItem> res = new ArrayList<IRobotSpecItem>();

		for (IRepositoryItem repositoryItem : repository.getItems().values()) {
			final IRobotSpecItem spec = (IRobotSpecItem) repositoryItem;

			if (!repositoryItem.isValid()) {
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
			if (onlyRobots && !(repositoryItem instanceof RobotItem)) {
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

	public int extractJar(IRobotSpecItem item) {
		if (!item.isInJAR()) {
			return -2;
		}
		((RepositoryItem) item).getRoot().extractJAR();
		return 0; 
	}

	public void createTeam(File target, TeamProperties teamProps) throws IOException {
		checkDbExists();

		TeamItem.createOrUpdateTeam(target, teamProps);
		refresh(target.toURI().toString());
	}

	public String createPackage(File jarFile, List<IRobotSpecItem> selectedRobots, RobotProperties robotProps) {
		checkDbExists();
		List<RobotItem> robotItems = getAllRobotItems(selectedRobots);
		TeamItem teamItem = getTeamItem(selectedRobots);

		String res = JarCreator.createPackage(jarFile, robotItems, teamItem, robotProps);
		refresh(jarFile.toURI().toString());

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
			IRepositoryItem res = repository.getItem(teamBot);

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

	private List<RobotItem> getAllRobotItems(Collection<IRobotSpecItem> items) {
		List<RobotItem> result = new ArrayList<RobotItem>();

		for (IRobotSpecItem item : items) {
			if (item.isTeam()) {
				result.addAll(getRobotItems((TeamItem) item));
			} else {
				result.add((RobotItem) item);
			}
		}
		return result;
	}

	private static TeamItem getTeamItem(Collection<IRobotSpecItem> items) {
		for (IRobotSpecItem item : items) {
			if (item.isTeam()) {
				return (TeamItem) item;
			}
		}
		return null;
	}

	private IRobotSpecItem getRobot(String fullClassNameWithVersion) {
		final IRepositoryItem repositoryItem = repository.getItem(fullClassNameWithVersion);

		if (repositoryItem == null || !repositoryItem.isValid()) {
			return null;
		}
		return (IRobotSpecItem) repositoryItem;
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
