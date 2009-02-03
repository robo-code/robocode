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
import net.sf.robocode.repository2.items.handlers.ItemHandler;
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
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;


/**
 * Represents one .jar file
 * @author Pavel Savara (original)
 */
public class JarRoot extends BaseRoot implements IRepositoryRoot {
	private static final long serialVersionUID = 1L;

	private URL jarUrl;
	private long lastModified;

	public JarRoot(Database db, File rootPath) {
		super(db, rootPath);
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


			final ArrayList<IItem> items = new ArrayList<IItem>();

			visitItems(items);
			for (IItem item : items) {
				item.update(lastModified, false);
			}
		}
	}

	private void visitItems(ArrayList<IItem> items) {
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
					if (!name.contains(".data/")) {
						try {
							String pUrl = root + entry.getName();
							final IItem item = ItemHandler.registerItems(new URL(pUrl), JarRoot.this, db);
							if (item!=null){
								items.add(item);
							}
						} catch (MalformedURLException e) {
							Logger.logError(e);
						}
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

	public void update(IItem item, boolean force) {
		item.update(rootPath.lastModified(), force);
	}
	
	public boolean isChanged(IItem item) {
		return rootPath.lastModified() > lastModified;
	}

	public URL getRootUrl() {
		return jarUrl;
	}

	public boolean isDevel() {
		return false;
	}

	public boolean isPackage() {
		return true;
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
