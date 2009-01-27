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
package net.sf.robocode.repository2.root;


import net.sf.robocode.repository2.items.IItem;
import net.sf.robocode.repository2.items.RobotItem;
import net.sf.robocode.repository2.items.TeamItem;
import net.sf.robocode.repository2.Database;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.FileUtil;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;


/**
 * @author Pavel Savara (original)
 */
public class ClassPathRoot implements IRepositoryRoot {
	final Database db;
	URL url;
	File rootPath;
	boolean isDevel = false;

	public ClassPathRoot(Database db, File rootPath) {
		this.db = db;
		this.rootPath = rootPath;
		try {
			url = rootPath.toURL();
		} catch (MalformedURLException e) {
			Logger.logError(e);
		}

		isDevel = !(rootPath.equals(FileUtil.getRobotsDir()));
	}

	public void update() {
		final ArrayList<File> properties = new ArrayList<File>();
		final ArrayList<File> classes = new ArrayList<File>();
		final ArrayList<File> teams = new ArrayList<File>();

		visitDirectory(properties, classes, teams, rootPath);
		registerItems(properties, classes, teams);
	}

	private void visitDirectory(ArrayList<File> properties, ArrayList<File> classes, ArrayList<File> teams, File path) {
		// find properties
		properties.addAll(Arrays.asList(path.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".properties");
			}
		})));

		// find teams
		teams.addAll(Arrays.asList(path.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".team");
			}
		})));

		// find classes
		classes.addAll(Arrays.asList(path.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".class");
			}
		})));

		// find sub-directories
		for (File subDir : path.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		})) {
			visitDirectory(properties, classes, teams, subDir);
		}
	}

	private void registerItems(ArrayList<File> properties, ArrayList<File> classes, ArrayList<File> teams) {
		try {
			Hashtable<URL, RobotItem> robots = new Hashtable<URL, RobotItem>();
			ArrayList<RobotItem> robotsList = new ArrayList<RobotItem>();
			ArrayList<Long> modified = new ArrayList<Long>();

			// properties
			for (File property : properties) {
				if (property.getName().equals("robocode.properties")) {
					continue;
				}
				final URL pUrl = property.toURL();
				RobotItem item = (RobotItem) db.getOldItem(pUrl.toString());

				if (item == null) {
					item = new RobotItem(null, pUrl, this);
				} else {
					item.setPropertiesUrl(pUrl);
				}
				robots.put(item.getFullUrl(), item);
				robots.put(pUrl, item);
				robotsList.add(item);
				modified.add(property.lastModified());
			}

			// classes
			for (File clazz : classes) {
				final URL cUrl = clazz.toURL();
				RobotItem  item = (RobotItem) db.getOldItem(cUrl.toString());

				if (item == null) {
					item = robots.get(cUrl);
				}
				if (item == null) {
					item = new RobotItem(cUrl, null, this);
				} else {
					item.setClassUrl(cUrl);
				}
				robots.put(item.getFullUrl(), item);
				robotsList.add(item);
				modified.add(clazz.lastModified());
			}

			// now update robots
			for (int i = 0; i < robotsList.size(); i++) {
				RobotItem robot = robotsList.get(i);

				robot.update(modified.get(i));
				db.addItem(robot);
			}

			// teams
			for (File team : teams) {
				final URL tUrl = team.toURL();
				IItem item = db.getOldItem(tUrl.toString());

				if (item == null) {
					item = new TeamItem(tUrl, this);
				}
				item.update(team.lastModified());
				db.addItem(item);
			}

		} catch (MalformedURLException e) {
			Logger.logError(e);
		}
	}

	public boolean isChanged(IItem item) {
		File f = new File(item.getFullUrl().toString());

		return f.lastModified() > item.getLastModified();
	}

	public URL getUrl() {
		return url; 
	}

	public boolean isDevel() {
		return isDevel;
	}
}
