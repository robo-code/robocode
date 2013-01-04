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
import net.sf.robocode.repository.items.IRepositoryItem;
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
 * Repository containing robot and team repositoryItems.
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Repository implements IRepository {

	private Map<String, IRepositoryRoot> roots = new HashMap<String, IRepositoryRoot>();
	private Map<String, IRepositoryItem> repositoryItems = new HashMap<String, IRepositoryItem>();
	private Map<String, IRepositoryItem> removedItems = new HashMap<String, IRepositoryItem>();

	@Override
	public void save(OutputStream out) {
		Collection<IRepositoryItem> uniqueitems = new LinkedList<IRepositoryItem>();
		Collection<IRepositoryRoot> uniqueroots = new LinkedList<IRepositoryRoot>();

		for (IRepositoryItem repositoryItem : repositoryItems.values()) {
			if (!uniqueitems.contains(repositoryItem)) {
				uniqueitems.add(repositoryItem);
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
		Collection<IRepositoryItem> uniqueitems;
		Collection<IRepositoryRoot> uniqueroots;

		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(in);

			uniqueroots = (Collection<IRepositoryRoot>) ois.readObject();
			uniqueitems = (Collection<IRepositoryItem>) ois.readObject();

			for (IRepositoryRoot root : uniqueroots) {
				((BaseRoot) root).setRepository(this);

				String key = root.getURL().toString();
				key = URLDecoder.decode(key, "UTF-8");

				roots.put(key, root);
			}
			for (IRepositoryItem repositoryItem : uniqueitems) {
				addOrUpdateItem(repositoryItem);
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
	public void addOrUpdateItem(IRepositoryItem repositoryItem) {
		Collection<String> friendlyUrls = repositoryItem.getFriendlyURLs();
		if (friendlyUrls != null) {
			// Add or update the item so it can be found using later using any friendly URL
			for (String friendly : friendlyUrls) {
				if (friendly != null) {
					IRepositoryItem existingItem = repositoryItems.get(friendly);
					// Add the item if it does not exist already, or update it if the version is newer
					// than the existing item.
					if (existingItem == null || repositoryItem.compareTo(existingItem) > 0) {
						repositoryItems.put(friendly, repositoryItem);
					}
				}
			}
		}
	}

	@Override
	public IRepositoryItem getItem(String friendlyUrl) {
		IRepositoryItem repositoryItem = repositoryItems.get(friendlyUrl);
		if (repositoryItem == null) {
			repositoryItem = removedItems.get(friendlyUrl);
		}
		return repositoryItem;
	}

	@Override
	public Map<String, IRepositoryItem> getItems() {
		return Collections.unmodifiableMap(repositoryItems);
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
		Collection<Map.Entry<String, IRepositoryItem>> itemsToMove = new ArrayList<Map.Entry<String, IRepositoryItem>>();

		for (Map.Entry<String, IRepositoryItem> entry : repositoryItems.entrySet()) {
			if (entry.getValue().getRoot().equals(root)) {
				itemsToMove.add(entry);
			}
		}

		for (Map.Entry<String, IRepositoryItem> entry : itemsToMove) {
			String key = entry.getKey();

			removedItems.put(key, entry.getValue());
			repositoryItems.remove(key);
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
