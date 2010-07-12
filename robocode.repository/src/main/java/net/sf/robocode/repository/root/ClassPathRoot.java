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


import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.Database;
import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.items.handlers.ItemHandler;
import net.sf.robocode.ui.IWindowManager;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Represents a class path root
 * @author Pavel Savara (original)
 */
public class ClassPathRoot extends BaseRoot implements IRepositoryRoot {
	private static final long serialVersionUID = 1L;

	private final File projectPath;

	public ClassPathRoot(Database db, File rootPath, File projectPath) {
		super(db, rootPath);
		this.projectPath = projectPath;
	}

	public void update(boolean force) {
		final IWindowManager windowManager = net.sf.robocode.core.Container.getComponent(IWindowManager.class);

		setStatus(windowManager, "Updating class path: " + rootPath);
		db.moveOldItems(this);
		final ArrayList<IItem> items = new ArrayList<IItem>();
		final ArrayList<Long> modified = new ArrayList<Long>();

		visitDirectory(rootPath, items, modified);
		for (int i = 0; i < items.size(); i++) {
			IItem item = items.get(i);

			item.update(modified.get(i), force);
		}
	}

	private void visitDirectory(File path, final ArrayList<IItem> items, final ArrayList<Long> modified) {
		// find files
		// noinspection ResultOfMethodCallIgnored
		path.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.isFile()) {
					try {
						final IItem item = ItemHandler.registerItems(pathname.toURI().toURL(), ClassPathRoot.this, db);

						if (item != null) {
							items.add(item);
							modified.add(pathname.lastModified());
						}
					} catch (MalformedURLException e) {
						Logger.logError(e);
						return false;
					}
				}
				return false;
			}
		});

		// find sub-directories
		File[] subDirs = path.listFiles(
				new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory() && !pathname.getName().toLowerCase().endsWith(".data")
						&& !pathname.getName().toLowerCase().endsWith(".robotcache");
			}
		});

		if (subDirs != null) {
			for (File subDir : subDirs) {
				visitDirectory(subDir, items, modified);
			}
		}
	}

	public void update(IItem item, boolean force) {
		File f = new File(item.getItemURL().toString());

		item.update(f.lastModified(), force);
	}

	public boolean isChanged(IItem item) {
		File f = new File(item.getItemURL().toString());

		return f.lastModified() > item.getLastModified();
	}

	public boolean isDevelopmentRoot() {
		return true;
	}

	public boolean isJAR() {
		return false;
	}

	private void setStatus(IWindowManager windowManager, String message) {
		if (windowManager != null) {
			windowManager.setStatus(message);
		}
	}

	public String getFriendlyProjectURL(URL itemURL) {
		String noType = null;

		if (projectPath != null) {
			try {
				String rootPath = projectPath.toURI().toURL().toString();
				String itemPath = itemURL.toString().substring(getURL().toString().length());

				noType = rootPath + itemPath.substring(0, itemPath.lastIndexOf('.'));
			} catch (MalformedURLException ignore) {}
		}
		return noType;
	}
}
