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
package net.sf.robocode.repository;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.root.BaseRoot;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.repository.root.handlers.RootHandler;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;


/**
 * Repository containing robot and team items.
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Repository {

	private Map<String, IRepositoryRoot> roots = new Hashtable<String, IRepositoryRoot>();
	private Map<String, IItem> items = new Hashtable<String, IItem>();
	private Map<String, IItem> oldItems = new Hashtable<String, IItem>();

	/**
	 * Adds or updates an item. If the item is not present in the repository, it will be added;
	 * otherwise it will be updated if the specified item has a never version than the existing
	 * item in the repository.
	 *
	 * @param item is the item to add or update.
	 */
	public void putItem(IItem item) {
		Collection<String> friendlyUrls = item.getFriendlyURLs();
		if (friendlyUrls != null) {
			// Add or update the item so it can be found using later using any friendly URL
			for (String friendly : friendlyUrls) {
				if (friendly != null) {
					IItem existingItem = items.get(friendly);
					// Add the item if it does not exist already, or update it if the version is newer
					// than the existing item.
					if (existingItem == null || item.compareTo(existingItem) > 0) {
						items.put(friendly, item);
					}
				}
			}
		}
	}

	public IItem getItem(String friendlyURL) {
		IItem item = items.get(friendlyURL);
		if (item == null) {
			item = oldItems.get(friendlyURL);
		}
		return item;
	}

	public boolean updateItemRoot(String friendlyURL, boolean force) {
		IItem item = items.get(friendlyURL);
		if (item != null) {
			item.getRoot().update(item, force);
			return true;
		}
		return false; 
	}

	public void save(OutputStream out) {
		Collection<IItem> uniqueitems = new LinkedList<IItem>();
		Collection<IRepositoryRoot> uniqueroots = new LinkedList<IRepositoryRoot>();

		for (IItem item : items.values()) {
			if (!uniqueitems.contains(item)) {
				uniqueitems.add(item);
			}
		}

		for (IRepositoryRoot root : roots.values()) {
			uniqueroots.add(root);
		}

		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(out);
			oos.writeObject(uniqueroots);
			oos.writeObject(uniqueitems);
		} catch (IOException e) {
			Logger.logError("Can't save robot database", e);
		} finally {
			FileUtil.cleanupStream(oos);
		}
	}

	@SuppressWarnings("unchecked")
	public void load(InputStream in) {
		Collection<IItem> uniqueitems;
		Collection<IRepositoryRoot> uniqueroots;

		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(in);

			uniqueroots = (Collection<IRepositoryRoot>) ois.readObject();
			uniqueitems = (Collection<IItem>) ois.readObject();

			for (IRepositoryRoot root : uniqueroots) {
				((BaseRoot) root).setRepository(this);

				String key = root.getURL().toString();
				key = URLDecoder.decode(key, "UTF-8");

				roots.put(key, root);
			}
			for (IItem item : uniqueitems) {
				putItem(item);
			}
		} catch (IOException e) {
			Logger.logError("Can't load robot database: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			Logger.logError("Can't load robot database: " + e.getMessage());
		} finally {
			FileUtil.cleanupStream(ois);
		}
	}

	public boolean update(File robotsDir, Collection<File> devDirs, boolean force) {
		final int prev = items.size();

		RootHandler.openHandlers();

		Hashtable<String, IRepositoryRoot> newroots = new Hashtable<String, IRepositoryRoot>();

		RootHandler.visitDirectories(robotsDir, false, newroots, roots, this, force);
		for (File dir : devDirs) {
			RootHandler.visitDirectories(dir, true, newroots, roots, this, force);
		}

		// removed roots
		for (IRepositoryRoot oldRoot : roots.values()) {
			if (!newroots.containsKey(oldRoot.getURL().toString())) {
				moveOldItems(oldRoot);
			}
		}

		roots = newroots;
		oldItems = new Hashtable<String, IItem>();

		RootHandler.closeHandlers();

		return prev != items.size();
	}

	public void moveOldItems(IRepositoryRoot root) {
		Collection<Map.Entry<String, IItem>> itemsToMove = new ArrayList<Map.Entry<String, IItem>>();

		for (Map.Entry<String, IItem> entry : items.entrySet()) {
			if (entry.getValue().getRoot().equals(root)) {
				itemsToMove.add(entry);
			}
		}

		for (Map.Entry<String, IItem> entry : itemsToMove) {
			String key = entry.getKey();

			oldItems.put(key, entry.getValue());
			items.remove(key);
		}
	}

	public List<IRepositoryItem> getFilteredItems(boolean onlyWithSource, boolean onlyWithPackage, boolean onlyRobots, boolean onlyDevelopment, boolean onlyNotDevelopment, boolean onlyInJar) {
		final List<IRepositoryItem> res = new ArrayList<IRepositoryItem>();

		for (IItem item : items.values()) {
			final IRepositoryItem spec = (IRepositoryItem) item;

			if (!item.isValid()) {
				continue;
			}
			if (onlyWithSource && !spec.isSourceIncluded()) {
				continue;
			}
			if (onlyWithPackage && spec.getFullPackage() == null) {
				continue;
			}
			if (onlyInJar && !spec.isInJAR()) {
				continue;
			}
			if (onlyRobots && !(item instanceof RobotItem)) {
				continue;
			}
			if (onlyDevelopment && !spec.isDevelopmentVersion()) {
				continue;
			}
			if (onlyNotDevelopment && spec.isDevelopmentVersion()) {
				continue;
			}
			if (res.contains(spec)) {
				continue;
			}
			res.add(spec);
		}
		Collections.sort(res);
		return res;
	}

	public Collection<IRepositoryItem> getAllValidItems() {
		final ArrayList<IRepositoryItem> res = new ArrayList<IRepositoryItem>();

		for (IItem item : items.values()) {
			final IRepositoryItem spec = (IRepositoryItem) item;
			if (item.isValid() && !res.contains(spec)) {
				res.add(spec);
			}
		}
		return res;
	}

	public List<IRepositoryItem> getValidItems(String friendlyURLs) {
		List<IRepositoryItem> result = new ArrayList<IRepositoryItem>();
		StringTokenizer tokenizer = new StringTokenizer(friendlyURLs, ",");

		while (tokenizer.hasMoreTokens()) {
			String friendlyURL = tokenizer.nextToken().trim();

			IItem item = getItem(friendlyURL);
			if (item != null) {
				if (item.isValid()) {
					result.add((IRepositoryItem) item);
				} else {
					Logger.logError("Can't load '" + friendlyURL + "' because it is an invalid robot or team.");
				}
			} else {
				Logger.logError("Can't find '" + friendlyURL + '\'');
			}
		}
		return result;
	}
}
