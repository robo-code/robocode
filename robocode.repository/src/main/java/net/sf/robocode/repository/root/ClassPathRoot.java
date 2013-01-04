/*******************************************************************************
 * Copyright (c) 2001-2012 Mathew A. Nelson and Robocode contributors
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
import net.sf.robocode.repository.IRepository;
import net.sf.robocode.repository.items.IRepositoryItem;
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

	public ClassPathRoot(IRepository repository, File rootPath, File projectPath) {
		super(repository, rootPath);
		this.projectPath = projectPath;
	}

	public void update(boolean force) {
		final IWindowManager windowManager = net.sf.robocode.core.Container.getComponent(IWindowManager.class);

		setStatus(windowManager, "Updating class path: " + rootPath);
		repository.removeItemsFromRoot(this);
		final ArrayList<IRepositoryItem> repositoryItems = new ArrayList<IRepositoryItem>();
		final ArrayList<Long> modified = new ArrayList<Long>();

		visitDirectory(rootPath, repositoryItems, modified);
		for (int i = 0; i < repositoryItems.size(); i++) {
			IRepositoryItem repositoryItem = repositoryItems.get(i);

			repositoryItem.update(modified.get(i), force);
		}
	}

	private void visitDirectory(File path, final ArrayList<IRepositoryItem> repositoryItems, final ArrayList<Long> modified) {
		// find files
		// noinspection ResultOfMethodCallIgnored
		path.listFiles(
				new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.isFile()) {
					try {
						final IRepositoryItem repositoryItem = ItemHandler.registerItems(pathname.toURI().toURL(), ClassPathRoot.this,
								repository);

						if (repositoryItem != null) {
							repositoryItems.add(repositoryItem);
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
				visitDirectory(subDir, repositoryItems, modified);
			}
		}
	}

	public void update(IRepositoryItem repositoryItem, boolean force) {
		File f = new File(repositoryItem.getItemURL().toString());

		repositoryItem.update(f.lastModified(), force);
	}

	public boolean isChanged(IRepositoryItem repositoryItem) {
		File f = new File(repositoryItem.getItemURL().toString());

		return f.lastModified() > repositoryItem.getLastModified();
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
