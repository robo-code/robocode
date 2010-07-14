/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.repository.root;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.URLJarCollector;
import net.sf.robocode.io.JarJar;
import net.sf.robocode.repository.Database;
import net.sf.robocode.repository.packager.JarExtractor;
import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.items.handlers.ItemHandler;
import net.sf.robocode.ui.IWindowManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


/**
 * Represents one .jar file
 * @author Pavel Savara (original)
 */
public class JarRoot extends BaseRoot implements IRepositoryRoot {
	private static final long serialVersionUID = 1L;

	private URL jarUrl;
	private String jarNoSeparator;
	private long lastModified;

	public JarRoot(Database db, File rootPath) {
		super(db, rootPath);
		try {
			jarNoSeparator = "jar:" + rootPath.toURI().toString();

			jarUrl = new URL(jarNoSeparator + "!/");
		} catch (MalformedURLException e) {
			Logger.logError(e);
		}
	}

	public void update(boolean force) {
		final IWindowManager windowManager = net.sf.robocode.core.Container.getComponent(IWindowManager.class);

		setStatus(windowManager, "Updating .jar: " + rootPath.toString());
		long lm = rootPath.lastModified();

		if (lm > this.lastModified) {
			db.moveOldItems(this);
			this.lastModified = lm;

			final ArrayList<IItem> items = new ArrayList<IItem>();

			visitItems(items);
			for (IItem item : items) {
				item.update(lastModified, force);
			}
		}
	}

	private void visitItems(ArrayList<IItem> items) {
		final String root = jarNoSeparator;
		InputStream is = null;
		BufferedInputStream bis = null;
		JarInputStream jarIS = null;

		try {
			final URLConnection con = URLJarCollector.openConnection(rootURL);

			is = con.getInputStream();
			bis = new BufferedInputStream(is);
			jarIS = new JarInputStream(bis);
			readJarStream(items, root, jarIS);

		} catch (Exception e) {
			Logger.logError(rootURL + " is probably corrupted (" + e.getClass().getName() + " " + e.getMessage() + ")");
		} finally {
			FileUtil.cleanupStream(jarIS);
			FileUtil.cleanupStream(bis);
			FileUtil.cleanupStream(is);
		}
	}

	private void readJarStream(ArrayList<IItem> items, String root, JarInputStream jarIS) throws IOException {
		JarEntry entry = jarIS.getNextJarEntry();

		while (entry != null) {
			String name = entry.getName().toLowerCase();

			if (!entry.isDirectory()) {
				if (name.contains(".data/") && !name.contains(".robotcache/")) {
					JarExtractor.extractFile(FileUtil.getRobotsDataDir(), jarIS, entry);
				} else {
					if (name.endsWith(".jar") || name.endsWith(".zip")) {
						JarInputStream inner = null;

						try {
							inner = new JarInputStream(jarIS);
							readJarStream(items, "jar:jar" + root + JarJar.SEPARATOR + entry.getName(), inner);
						} finally {
							if (inner != null) {
								inner.closeEntry();								
							}
						}
					} else {
						createItem(items, new URL(root + "!/"), entry);
					}
				}
			}
			entry = jarIS.getNextJarEntry();
		}
	}

	private void createItem(ArrayList<IItem> items, URL root, JarEntry entry) {
		try {
			String pUrl = root.toString() + entry.getName();
			final IItem item = ItemHandler.registerItems(new URL(pUrl), JarRoot.this, db);

			if (item != null) {
				if (item instanceof RobotItem) {
					RobotItem robotItem = (RobotItem) item; 

					robotItem.setClassPathURL(root);
				}
				items.add(item);
			}
		} catch (MalformedURLException e) {
			Logger.logError(e);
		}
	}

	public void update(IItem item, boolean force) {
		item.update(rootPath.lastModified(), force);
	}

	public boolean isChanged(IItem item) {
		return rootPath.lastModified() > lastModified;
	}

	public URL getURL() {
		return jarUrl;
	}

	public boolean isDevelopmentRoot() {
		return false;
	}

	public boolean isJAR() {
		return true;
	}

	public void extractJAR() {
		JarExtractor.extractJar(rootURL);
	}

	private static void setStatus(IWindowManager windowManager, String message) {
		if (windowManager != null) {
			windowManager.setStatus(message);
		}
	}
}
