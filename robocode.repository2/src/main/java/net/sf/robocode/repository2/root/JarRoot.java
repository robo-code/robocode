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


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.repository2.Database;
import net.sf.robocode.repository2.items.IItem;
import net.sf.robocode.repository2.items.handlers.ItemHandler;
import net.sf.robocode.ui.IWindowManager;

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

	private void setStatus(IWindowManager windowManager, String message) {
		if (windowManager != null) {
			windowManager.setStatus(message);
		}
	}
}
