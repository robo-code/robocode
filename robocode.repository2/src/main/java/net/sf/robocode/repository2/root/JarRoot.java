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
import java.io.InputStream;
import java.io.IOException;
import java.util.jar.JarInputStream;
import java.util.jar.JarEntry;
import java.util.ArrayList;
import java.util.Hashtable;


/**
 * Represents one .jar file
 * @author Pavel Savara (original)
 */
public class JarRoot implements IRepositoryRoot {
	final Database db;
	URL url;
	File rootPath;
	long lastModified;

	public JarRoot(Database db, File rootPath) {
		this.db = db;
		this.rootPath = rootPath;
		try {
			url = rootPath.toURL();
		} catch (MalformedURLException e) {
			Logger.logError(e);
		}
	}

	public void update() {
		long lm = rootPath.lastModified();

		if (lm > this.lastModified) {
			this.lastModified = lm;

			final ArrayList<URL> properties = new ArrayList<URL>();
			final ArrayList<URL> classes = new ArrayList<URL>();
			final ArrayList<URL> teams = new ArrayList<URL>();

			visitItems(properties, classes, teams);
			registerItems(properties, classes, teams);
		}
	}

	private void visitItems(ArrayList<URL> properties, ArrayList<URL> classes, ArrayList<URL> teams) {
		final String root = url.toString();
		InputStream is = null;
		JarInputStream jarIS = null;

		try {
			is = url.openStream();
			jarIS = new JarInputStream(is);

			JarEntry entry = jarIS.getNextJarEntry();

			while (entry != null) {
				String name = entry.getName().toLowerCase();

				if (!entry.isDirectory()) {
					if (name.endsWith(".properties")) {
						String pUrl = "jar:" + root + "!/" + entry.getName();

						properties.add(new URL(pUrl));
					} else if (name.endsWith(".team")) {
						String tUrl = "jar:" + root + "!/" + entry.getName();

						teams.add(new URL(tUrl));
					} else if (name.endsWith(".class")) {
						String cUrl = "jar:" + root + "!/" + entry.getName();

						classes.add(new URL(cUrl));
					}
				}
				entry = jarIS.getNextJarEntry();
			}
		} catch (IOException e) {
			Logger.logError(e);
		} finally {
			FileUtil.cleanupStream(jarIS);
			FileUtil.cleanupStream(is);
		}
	}

	private void registerItems(ArrayList<URL> properties, ArrayList<URL> classes, ArrayList<URL> teams) {
		Hashtable<URL, RobotItem> robots = new Hashtable<URL, RobotItem>();
		ArrayList<RobotItem> robotsList = new ArrayList<RobotItem>();

		// properties
		for (URL pUrl : properties) {
			RobotItem item = (RobotItem) db.getOldItem(pUrl.toString());

			if (item == null) {
				item = new RobotItem(null, pUrl, this);
			} else {
				item.setPropertiesUrl(pUrl);
			}
			robots.put(item.getFullUrl(), item);
			robots.put(pUrl, item);
			robotsList.add(item);
		}

		// classes
		for (URL cUrl : classes) {
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
		}

		// now update robots
		for (RobotItem robot : robotsList) {
			robot.update(lastModified, false);
			db.addItem(robot);
		}

		// teams
		for (URL tUrl : teams) {
			IItem item = db.getOldItem(tUrl.toString());

			if (item == null) {
				item = new TeamItem(tUrl, this);
			}
			item.update(lastModified, false);
			db.addItem(item);
		}
	}

	public void update(IItem item, boolean force) {// TODO ZAMO
	}
	
	public boolean isChanged(IItem item) {
		return rootPath.lastModified() > lastModified;
	}

	public URL getUrl() {
		return url;
	}

	public boolean isDevel() {
		return false;
	}

	public String toString() {
		return url.toString();
	}
}
