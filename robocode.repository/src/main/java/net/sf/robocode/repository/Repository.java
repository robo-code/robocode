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
import net.sf.robocode.repository.root.BaseRoot;
import net.sf.robocode.repository.root.IRepositoryRoot;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * Repository containing robot and team items.
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Repository implements IRepository {

	private Map<String, IRepositoryRoot> roots = new HashMap<String, IRepositoryRoot>();
	private Map<String, IItem> items = new HashMap<String, IItem>();
	private Map<String, IItem> removedItems = new HashMap<String, IItem>();

	@Override
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

	@Override
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
				addOrUpdateItem(item);
			}
		} catch (IOException e) {
			Logger.logError("Can't load robot database: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			Logger.logError("Can't load robot database: " + e.getMessage());
		} finally {
			FileUtil.cleanupStream(ois);
		}
	}

	@Override
	public void addOrUpdateItem(IItem item) {
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

	@Override
	public IItem getItem(String friendlyUrl) {
		IItem item = items.get(friendlyUrl);
		if (item == null) {
			item = removedItems.get(friendlyUrl);
		}
		return item;
	}

	@Override
	public Map<String, IItem> getItems() {
		return Collections.unmodifiableMap(items);
	}

	@Override
	public Map<String, IRepositoryRoot> getRoots() {
		return Collections.unmodifiableMap(roots);
	}

	@Override
	public void removeRoot(String friendlyUrl) {
		roots.remove(friendlyUrl);
	}

	@Override
	public void removeItemsFromRoot(IRepositoryRoot root) {
		Collection<Map.Entry<String, IItem>> itemsToMove = new ArrayList<Map.Entry<String, IItem>>();

		for (Map.Entry<String, IItem> entry : items.entrySet()) {
			if (entry.getValue().getRoot().equals(root)) {
				itemsToMove.add(entry);
			}
		}

		for (Map.Entry<String, IItem> entry : itemsToMove) {
			String key = entry.getKey();

			removedItems.put(key, entry.getValue());
			items.remove(key);
		}
	}

	/**
	 * Replaces the repository roots with new repository roots.
	 *
	 * @param newRoots is the new repository roots for this repository.
	 */
	// Only for the RepositoryManager
	public void setRoots(Map<String, IRepositoryRoot> newRoots) {
		roots = newRoots;
		removedItems.clear(); 
	}
}
