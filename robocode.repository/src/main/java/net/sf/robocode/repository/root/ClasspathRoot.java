/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.repository.root;


import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRepository;
import net.sf.robocode.repository.items.IRepositoryItem;
import net.sf.robocode.repository.items.handlers.ItemHandler;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Represents a classpath root.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public final class ClasspathRoot extends BaseRoot implements IRepositoryRoot {
	private static final long serialVersionUID = 1L;

	private final File projectPath;

	public ClasspathRoot(IRepository repository, File rootPath, File projectPath) {
		super(repository, rootPath);
		this.projectPath = projectPath;
	}

	public void update(boolean force) {
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
		path.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.isFile()) {
					try {
						IRepositoryItem repositoryItem = ItemHandler.registerItems(pathname.toURI().toURL(), ClasspathRoot.this, repository);
						if (repositoryItem != null) {
							repositoryItems.add(repositoryItem);
							modified.add(pathname.lastModified());
						}
					} catch (MalformedURLException e) {
						Logger.logError(e);
					}
				}
				return false;
			}
		});

		// find sub-directories
		File[] subDirs = path.listFiles(new FileFilter() {
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
		setStatus("Updating classpath: " + rootPath);

		File file = new File(repositoryItem.getItemURL().toString());
		repositoryItem.update(file.lastModified(), force);
	}

	public boolean isChanged(IRepositoryItem repositoryItem) {
		File file = new File(repositoryItem.getItemURL().toString());
		return file.lastModified() > repositoryItem.getLastModified();
	}

	public boolean isDevelopmentRoot() {
		return true;
	}

	public boolean isJAR() {
		return false;
	}

	public String getFriendlyProjectURL(URL itemURL) {
		String url = null;

		if (projectPath != null) {
			try {
				String rootPath = projectPath.toURI().toURL().toString();
				String itemPath = itemURL.toString().substring(getURL().toString().length());

				url = rootPath + itemPath.substring(0, itemPath.lastIndexOf('.'));
			} catch (MalformedURLException e) {
				Logger.logError(e);				
			}
		}
		return url;
	}
}
