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


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.items.TeamItem;
import net.sf.robocode.repository.root.BaseRoot;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.repository.root.handlers.RootHandler;

import java.io.*;
import java.net.URL;
import java.util.*;


/**
 * @author Pavel Savara (original)
 */
public class Database {
	private Hashtable<String, IRepositoryRoot> roots = new Hashtable<String, IRepositoryRoot>();
	private Hashtable<String, IItem> items = new Hashtable<String, IItem>();
	private Hashtable<String, IItem> oldItems = new Hashtable<String, IItem>();
	private final IRepositoryManager manager;

	public Database(IRepositoryManager manager) {
		this.manager = manager;
	}

	public boolean update(File robotsDir, List<File> devDirs, boolean updateInvalid) {
		final int prev = items.size();
		Hashtable<String, IRepositoryRoot> newroots = new Hashtable<String, IRepositoryRoot>();

		RootHandler.visitDirectories(robotsDir, false, newroots, roots, this, updateInvalid);
		for (File dir : devDirs) {
			RootHandler.visitDirectories(dir, true, newroots, roots, this, updateInvalid);
		}

		// removed roots
		for (IRepositoryRoot oldRoot : roots.values()) {
			moveOldItems(oldRoot);
		}
		roots = newroots;
		oldItems = new Hashtable<String, IItem>();

		System.gc();
		return prev != items.size();
	}

	public boolean update(String url, boolean force) {
		final IItem item = items.get(url);

		if (item != null) {
			item.getRoot().update(item, force);
			return true;
		}
		return false; 
	}

	public void addItem(IItem item) {
		final URL url = item.getFullUrl();

		if (url != null) {
			items.put(url.toString(), item);
		}
		final List<String> friendlyUrls = item.getFriendlyUrls();

		if (friendlyUrls != null) {
			for (String friendly : friendlyUrls) {
				if (friendly != null) {
					final IItem conflict = items.get(friendly);

					if (conflict != null) {
						if (item.compareTo(conflict) > 0) {
							// replace with higher version
							items.put(friendly, item);
						}
					} else {
						items.put(friendly, item);
					}
				}
			}
		}
	}

	public IItem getItem(String url) {
		return items.get(url);
	}

	public IItem getOldItem(String url) {
		return oldItems.get(url);
	}

	public void moveOldItems(IRepositoryRoot root) {
		List<Map.Entry<String, IItem>> move = new ArrayList<Map.Entry<String, IItem>>();

		for (Map.Entry<String, IItem> entry : items.entrySet()) {
			if (entry.getValue().getRoot() == root) {
				move.add(entry);
			}
		}

		for (Map.Entry<String, IItem> entry : move) {
			oldItems.put(entry.getKey(), entry.getValue());
			items.remove(entry.getKey());
		}
	}

	public List<TeamItem> filterTeams(List<IRepositoryItem> selectedRobots) {
		List<TeamItem> result = new ArrayList<TeamItem>();

		for (IRepositoryItem item : selectedRobots) {
			if (item.isTeam()) {
				result.add((TeamItem) item);
			}
		}
		return result;
	}

	public List<RobotItem> expandTeams(List<IRepositoryItem> selectedRobots) {
		List<RobotItem> result = new ArrayList<RobotItem>();

		for (IRepositoryItem item : selectedRobots) {
			if (item.isTeam()) {
				result.addAll(expandTeam((TeamItem) item));
			} else {
				result.add((RobotItem) item);
			}
		}
		return result;
	}

	public List<RobotItem> expandTeam(TeamItem team) {
		List<RobotItem> result = new ArrayList<RobotItem>();
		StringTokenizer teamTokenizer = new StringTokenizer(team.getMembers(), ",");

		while (teamTokenizer.hasMoreTokens()) {
			final String botName = teamTokenizer.nextToken();

			// first load from same classPath
			String teamBot = team.getRoot().getRootUrl() + botName.replace('.', '/');
			IItem res = getItem(teamBot);

			if (res != null && res instanceof RobotItem) {
				result.add((RobotItem) res);
				continue;
			}

			// try general search
			res = getItem(botName);
			if (res != null && res instanceof RobotItem) {
				result.add((RobotItem) res);
				continue;
			}

			// no found
			Logger.logError("Can't find robot: " + botName);
		}
		return result;
	}

	public List<IRepositoryItem> filterSpecifications(boolean onlyWithSource, boolean onlyWithPackage, boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean onlyInJar) {
		final ArrayList<IRepositoryItem> res = new ArrayList<IRepositoryItem>();

		for (IItem item : items.values()) {
			final IRepositoryItem spec = (IRepositoryItem) item;

			if (!item.isValid()) {
				continue;
			}
			if (onlyWithSource && !spec.getJavaSourceIncluded()) {
				continue;
			}
			if (onlyWithPackage && spec.getFullPackage() == null) {
				continue;
			}
			if (onlyInJar && !spec.isInJar()) {
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

	public List<IRepositoryItem> getAllSpecifications() {
		final ArrayList<IRepositoryItem> res = new ArrayList<IRepositoryItem>();

		for (IItem item : items.values()) {
			final IRepositoryItem spec = (IRepositoryItem) item;

			if (!item.isValid()) {
				continue;
			}

			if (!res.contains(spec)) {
				res.add(spec);
			}
		}
		return res;
	}

	public List<IRepositoryItem> getSelectedSpecifications(String selectedRobots) {
		List<IRepositoryItem> result = new ArrayList<IRepositoryItem>();
		StringTokenizer tokenizer = new StringTokenizer(selectedRobots, ",");

		while (tokenizer.hasMoreTokens()) {
			String bot = tokenizer.nextToken();
			final IItem item = getItem(bot);

			if (item != null) {
				if (item.isValid()) {
					result.add((IRepositoryItem) item);
				} else {
					Logger.logError("Can't load " + bot + ", because it is invalid robot or team.");
				}
			} else {
				Logger.logError("Can't find " + bot);
			}
		}
		return result;

	}

	public void save() {
		List<IItem> uniqueitems = new LinkedList<IItem>();
		List<IRepositoryRoot> uniqueroots = new LinkedList<IRepositoryRoot>();

		for (IItem item : items.values()) {
			if (!uniqueitems.contains(item)) {
				uniqueitems.add(item);
			}
		}

		for (IRepositoryRoot root : roots.values()) {
			uniqueroots.add(root);
		}

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(new File(manager.getRobotsDirectory(), "robot.database"));
			oos = new ObjectOutputStream(fos);
			oos.writeObject(uniqueroots);
			oos.writeObject(uniqueitems);
		} catch (IOException e) {
			Logger.logError("Can't save robot database", e);
		} finally {
			FileUtil.cleanupStream(oos);
			FileUtil.cleanupStream(fos);
		}
	}

	@SuppressWarnings({ "unchecked"})
	public static Database load(IRepositoryManager manager) {
		List<IItem> uniqueitems;
		List<IRepositoryRoot> uniqueroots;
		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			final File file = new File(manager.getRobotsDirectory(), "robot.database");

			if (!file.exists()) {
				return null;
			}
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			uniqueroots = (List<IRepositoryRoot>) ois.readObject();
			uniqueitems = (List<IItem>) ois.readObject();
			Database res = new Database(manager);

			for (IRepositoryRoot root : uniqueroots) {
				((BaseRoot) root).setDatabase(res);
				res.roots.put(root.getRootPath().toURI().toString(), root);
			}
			for (IItem item : uniqueitems) {
				res.addItem(item);
			}
			return res;
		} catch (Throwable t) {
			Logger.logError("Can't load robot database: " + t.getMessage());
			return null;
		} finally {
			FileUtil.cleanupStream(ois);
			FileUtil.cleanupStream(fis);
		}
	}
}
