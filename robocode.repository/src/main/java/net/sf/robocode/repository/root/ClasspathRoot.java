/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository.root;


import net.sf.robocode.host.security.ClassAnalyzer;
import net.sf.robocode.host.security.ClassFileReader;
import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRepository;
import net.sf.robocode.repository.items.IRepositoryItem;
import net.sf.robocode.repository.items.handlers.ItemHandler;
import net.sf.robocode.util.UrlUtil;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

		ClassAnalyzer.RobotMainClassPredicate mainClassPredicate = ClassFileReader.createMainClassPredicate(rootURL);

		visitDirectory(rootPath.toURI(), rootPath, items, itemsLastModification, mainClassPredicate);

		// Run thru all found repository items and update these according to their 'last modified' date
		for (int i = 0; i < items.size(); i++) {
			IRepositoryItem repositoryItem = items.get(i);
			repositoryItem.update(itemsLastModification.get(i), force);
		}
	}

	private void visitDirectory(final URI rootURI, final File path, final List<IRepositoryItem> items, final List<Long> itemsLastModification, final ClassAnalyzer.RobotMainClassPredicate mainClassPredicate) {
		final HashMap<IRepositoryItem, Integer> map = new HashMap<IRepositoryItem, Integer>();

		path.listFiles(
				new FileFilter() {
			public boolean accept(File pathname) {
				boolean accept = false;

				if (pathname.isFile()) {
					String fullName = rootURI.relativize(pathname.toURI()).toString();
					if (fullName.toLowerCase().endsWith(".class")) {
						accept = mainClassPredicate.isMainClassBinary(fullName.substring(0, fullName.length() - 6));
					} else {
						accept = true;
					}
				}

				if (accept) visitFile(pathname, map, items, itemsLastModification);
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
				visitDirectory(rootURI, subDir, items, itemsLastModification, mainClassPredicate);
			}
		}
	}

	private void visitFile(File pathname, HashMap<IRepositoryItem, Integer> map, List<IRepositoryItem> items, List<Long> itemsLastModification) {
		try {
			IRepositoryItem repositoryItem = ItemHandler.registerItem(pathname.toURI().toURL(),
					ClasspathRoot.this, repository);
			if (repositoryItem != null) {
				Integer indice = map.get(repositoryItem);
				long lastModified = pathname.lastModified();

				if (indice == null) {
					map.put(repositoryItem, itemsLastModification.size());
					items.add(repositoryItem);
					itemsLastModification.add(lastModified);
				} else {
					int index = indice;
					long v = itemsLastModification.get(index);
					if (lastModified > v) {
						itemsLastModification.set(index, lastModified);
					}
				}
			}
		} catch (MalformedURLException e) {
			Logger.logError(e);
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
