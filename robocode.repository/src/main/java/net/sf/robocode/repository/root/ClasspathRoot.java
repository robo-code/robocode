/*******************************************************************************
 * Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
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
import net.sf.robocode.util.UrlUtil;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


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

	/**
	 * {@inheritDoc}
	 */
	public void updateItems(boolean force) {
		setStatus("Updating classpath: " + rootPath.toString());
		
		// We remove all items from the root so we don't need to check, which items that might have been deleted.
		// This is fast to do, as items are simple referenced by friendly URLs in a map.
		// Items will be re-added or updated later in this method.
		repository.removeItemsFromRoot(this);

		// Retrieve all items accessible from this classpath root along with their 'last modified' date
		List<IRepositoryItem> items = new ArrayList<IRepositoryItem>();
		List<Long> itemsLastModification = new ArrayList<Long>();

		visitDirectory(rootPath, items, itemsLastModification);

		// Run thru all found repository items and update these according to their 'last modified' date
		for (int i = 0; i < items.size(); i++) {
			IRepositoryItem repositoryItem = items.get(i);
			repositoryItem.update(itemsLastModification.get(i), force);
		}
	}

	private void visitDirectory(File path, final List<IRepositoryItem> items, final List<Long> itemsLastModification) {
		
		path.listFiles(
				new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.isFile()) {
					try {
						IRepositoryItem repositoryItem = ItemHandler.registerItem(pathname.toURI().toURL(),
								ClasspathRoot.this, repository);
						if (repositoryItem != null) {
							items.add(repositoryItem);
							itemsLastModification.add(pathname.lastModified());
						}
					} catch (MalformedURLException e) {
						Logger.logError(e);
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
				visitDirectory(subDir, items, itemsLastModification);
			}
		}
	}

	public void updateItem(IRepositoryItem item, boolean force) {
		setStatus("Updating classpath: " + rootPath);

		File file = new File(item.getItemURL().toString());
		item.update(file.lastModified(), force);
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

				url = rootPath + UrlUtil.removeFileExtension(itemPath);

			} catch (MalformedURLException e) {
				Logger.logError(e);				
			}
		}
		return url;
	}
}
