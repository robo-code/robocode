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


import net.sf.robocode.core.Container;
import net.sf.robocode.host.IHostManager;
import net.sf.robocode.host.IRobotClassLoader;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.repository2.Database;
import net.sf.robocode.repository2.items.IItem;
import net.sf.robocode.repository2.items.RobotItem;
import net.sf.robocode.repository2.items.TeamItem;
import net.sf.robocode.version.IVersionManager;
import net.sf.robocode.ui.IWindowManager;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;


/**
 * Represents one .jar file
 * @author Pavel Savara (original)
 */
public class JarRoot implements IRepositoryRoot, Serializable {
	private static final long serialVersionUID = 1L;

	final Database db;
	URL url;
	URL jarUrl;
	File rootPath;
	long lastModified;

	public JarRoot(Database db, File rootPath) {
		this.db = db;
		this.rootPath = rootPath;
		try {
			final String jUrl = "jar:" + rootPath.toURL().toString() + "!/";

			jarUrl = new URL(jUrl);
			url = rootPath.toURL();
		} catch (MalformedURLException e) {
			Logger.logError(e);
		}
	}

	public void update() {
		final IWindowManager windowManager = net.sf.robocode.core.Container.getComponent(IWindowManager.class);

		setStatus(windowManager, "Updating .jar: " + rootPath.toString());
		long lm = rootPath.lastModified();

		if (lm > this.lastModified) {
			db.moveOldItems(this);
			this.lastModified = lm;

			final ArrayList<URL> properties = new ArrayList<URL>();
			final ArrayList<URL> classes = new ArrayList<URL>();
			final ArrayList<URL> teams = new ArrayList<URL>();

			visitItems(properties, classes, teams);
			registerItems(properties, classes, teams, windowManager);
		}
	}

	private void visitItems(ArrayList<URL> properties, ArrayList<URL> classes, ArrayList<URL> teams) {
		final String root = jarUrl.toString();
		InputStream is = null;
		JarInputStream jarIS = null;

		try {
			final URLConnection con = url.openConnection();

			con.setUseCaches(false);
			is = con.getInputStream();
			jarIS = new JarInputStream(is);

			JarEntry entry = jarIS.getNextJarEntry();

			while (entry != null) {
				String name = entry.getName().toLowerCase();

				if (!entry.isDirectory()) {
					if (name.contains(".data/")) {// skip
					} else if (name.endsWith(".properties")) {
						String pUrl = root + entry.getName();

						properties.add(new URL(pUrl));
					} else if (name.endsWith(".team")) {
						String tUrl = root + entry.getName();

						teams.add(new URL(tUrl));
					} else if (name.endsWith(".class")) {
						String cUrl = root + entry.getName();

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

	private void registerItems(ArrayList<URL> properties, ArrayList<URL> classes, ArrayList<URL> teams, IWindowManager windowManager) {
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
			if (robot.isValid()){
				setStatus(windowManager, "Updating robot: " + robot.getFullClassName());
				robot.update(lastModified, false);
			}
			db.addItem(robot);
		}

		// teams
		for (URL tUrl : teams) {
			IItem item = db.getOldItem(tUrl.toString());

			if (item == null) {
				item = new TeamItem(tUrl, this);
			}
			setStatus(windowManager, "Updating team: " + ((TeamItem)item).getFullClassName());
			item.update(lastModified, false);
			db.addItem(item);
		}
	}

	public void update(IItem item, boolean force) {
		item.update(rootPath.lastModified(), force);
	}
	
	public boolean isChanged(IItem item) {
		return rootPath.lastModified() > lastModified;
	}

	public URL getRootUrl() {
		return jarUrl;
	}

	public URL getClassPathUrl() {
		return url;
	}

	public boolean isDevel() {
		return false;
	}

	public boolean isPackage() {
		return true;
	}

	public String toString() {
		return url.toString();
	}

	public static void createPackage(File target, boolean source, List<RobotItem> robots, List<TeamItem> teams) {
		final IHostManager host = Container.getComponent(IHostManager.class);
		final String rVersion = Container.getComponent(IVersionManager.class).getVersion();
		JarOutputStream jarout = null;
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(target);
			jarout = new JarOutputStream(fos);
			jarout.setComment(rVersion + " - Robocode version");

			for (TeamItem team : teams) {
				JarEntry jt = new JarEntry(team.getRelativePath() + '/' + team.getShortClassName() + ".team");

				jarout.putNextEntry(jt);
				team.storeProperties(jarout);
				jarout.closeEntry();
			}

			for (RobotItem robot : robots) {
				IRobotClassLoader loader = null;
				JarEntry jt = new JarEntry(robot.getRelativePath() + '/' + robot.getShortClassName() + ".properties");

				jarout.putNextEntry(jt);
				robot.storeProperties(jarout);
				jarout.closeEntry();
				packageClasses(source, host, jarout, robot, loader);
			}
		} catch (IOException e) {
			Logger.logError(e);
		} finally {
			FileUtil.cleanupStream(jarout);
			FileUtil.cleanupStream(fos);
		}
	}

	private static void packageClasses(boolean source, IHostManager host, JarOutputStream jarout, RobotItem robot, IRobotClassLoader loader) throws IOException {
		try {
			loader = host.createLoader(robot);
			loader.loadRobotMainClass(true);

			for (String className : loader.getReferencedClasses()) {
				if (className.startsWith("java") || className.startsWith("robocode") || className.contains("$")) {
					continue;
				}
				String name = className.replace('.', '/');

				if (source) {
					// todo test exist
					JarEntry je = new JarEntry(name + ".java");

					jarout.putNextEntry(je);
					// TODO upload file
					jarout.closeEntry();
				}
				// todo test exist
				JarEntry je = new JarEntry(name + ".class");

				jarout.putNextEntry(je);
				// TODO upload file
				jarout.closeEntry();
			}

		} catch (ClassNotFoundException e) {
			Logger.logError(e);
		} finally {
			loader.cleanup();
		}
	}

	private void setStatus(IWindowManager windowManager, String message) {
		if (windowManager != null) {
			windowManager.setStatus(message);
		}
	}
}
