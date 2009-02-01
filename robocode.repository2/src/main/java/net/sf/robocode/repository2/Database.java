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


import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRepositoryItem;
import net.sf.robocode.repository2.items.IItem;
import net.sf.robocode.repository2.items.RobotItem;
import net.sf.robocode.repository2.items.TeamItem;
import net.sf.robocode.repository2.root.ClassPathRoot;
import net.sf.robocode.repository2.root.IRepositoryRoot;
import net.sf.robocode.repository2.root.JarRoot;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.util.*;


/**
 * @author Pavel Savara (original)
 */
public class Database {
	private Hashtable<String, IRepositoryRoot> roots = new Hashtable<String, IRepositoryRoot>();
	private Hashtable<String, IItem> items = new Hashtable<String, IItem>();
	private Hashtable<String, IItem> oldItems = new Hashtable<String, IItem>();

	public void update(File robots, List<File> devDirs) {
		try {
			Hashtable<String, IRepositoryRoot> newroots = new Hashtable<String, IRepositoryRoot>();

			// find directories
			final List<File> dirs = new ArrayList<File>(devDirs);

			dirs.add(robots);

			// update directories
			for (File dir : dirs) {
				IRepositoryRoot root = roots.get(dir.toURL().toString());

				if (root == null) {
					root = new ClassPathRoot(this, dir);
				}
				root.update();
				newroots.put(dir.toURL().toString(), root);
			}

			// find jar files
			final File[] jars = robots.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					final String low = pathname.toString().toLowerCase();

					return pathname.isFile() && (low.endsWith(".jar") || low.endsWith(".zip"));
				}
			});

			// update jar files
			for (File jar : jars) {
				IRepositoryRoot root = roots.get(jar.toURL().toString());

				if (root == null) {
					root = new JarRoot(this, jar);
				}
				root.update();
				newroots.put(jar.toURL().toString(), root);
			}

			roots = newroots;
			oldItems = new Hashtable<String, IItem>();
		} catch (MalformedURLException e) {
			Logger.logError(e);
		}
		System.gc();
		System.gc();
		System.gc();
		System.gc();
	}

	public void update(String url, boolean force) {
		final IItem iItem = items.get(url);

		iItem.getRoot().update(iItem, force);
	}

	public void addItem(IItem item) {
		items.put(item.getFullUrl().toString(), item);
		final List<String> friendlyUrls = item.getFriendlyUrls();

		if (friendlyUrls != null) {
			for (String friendly : friendlyUrls) {
				items.put(friendly, item);
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

	public List<IRepositoryItem> filterSpecifications(boolean onlyWithSource, boolean onlyWithPackage, boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment) {
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
		return res;
	}

	public List<IRepositoryItem> getAllSpecifications() {
		final ArrayList<IRepositoryItem> res = new ArrayList<IRepositoryItem>();

		for (IItem item : items.values()) {
			final IRepositoryItem spec = (IRepositoryItem) item;

			if (item.isValid() && !res.contains(spec)) {
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
}
