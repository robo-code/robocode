/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import net.sf.robocode.repository.items.IRepositoryItem;
import net.sf.robocode.repository.root.IRepositoryRoot;


/**
 * Interface for a repository containing meta-data for robot and team items.<p>
 *
 * Items can be retrieved from the reposition by using {@link IRepository#getItem(String)} using "friendly URLs"
 * that identifies the individual item. See {@link IRepositoryItem#getFriendlyURLs()}. An item has typically several
 * friendly URLs that identifies it, e.g. a file URL to a local property file or class file, or a URL to a JAR entry.
 *
 * @author Flemming N. Larsen (original)
 */
public interface IRepository {

	/**
	 * Loads data into this repository, previously saved by {@link #save(OutputStream)}.
	 * 
	 * @param in is an input stream used for reading the data.
	 * 
	 * @see #save(OutputStream)
	 */
	public void load(InputStream in);

	/**
	 * Saves the data in this repository to an output stream.
	 *
	 * @param out is the output stream used for writing the data.
	 * 
	 * @see #load(InputStream)
	 */
	public void save(OutputStream out);

	/**
	 * Adds or updates an item.<p>
	 *
	 * If the item is not present in the repository, it will be added; otherwise it will be updated, if the specified
	 * item has a never version than the existing item in the repository.<p>
	 * 
	 * This method will store the item under each friendly URL (keys) by using {@link IRepositoryItem#getFriendlyURLs()}.
	 *
	 * @param item is the item to add or update.
	 * 
	 * @see #getItem(String)
	 * @see #getItems()
	 */
	void addOrUpdateItem(IRepositoryItem item);

	/**
	 * Returns an item stored in this repository identified by a friendly URL (key).
	 *
	 * @param friendlyUrl is a key to the item used when the item was stored using {@link IRepositoryItem#getFriendlyURLs()}.
	 * @return an item from the repository identified by the friendly URL or null if the item does not exist.
	 * 
	 * @see #addOrUpdateItem(IRepositoryItem)
	 * @see #getItems()
	 */
	IRepositoryItem getItem(String friendlyUrl);

	/**
	 * Returns all items stored in this repository.
	 * @return a map containing items, where the keys are friendly URLs used for identifying the items.
	 * 
	 * @see #getItem(String)
	 */
	Map<String, IRepositoryItem> getItems();

	/**
	 * Returns all repository roots stored in this repository.<p>
	 *
	 * @return a map containing repository roots, where the keys are URLs used for identifying the individual
	 * repository root.
	 * 
	 * @see IRepositoryRoot
	 */
	Map<String, IRepositoryRoot> getRoots();

	/**
	 * Removes a repository root from this repository.
	 *
	 * @param url is the URL (key) of the repository root to remove.
	 */
	void removeRoot(String url);

	/**
	 * Removes all items from this repository that belongs to a specific repository root.
	 *
	 * @param root is the repository root containing the items to remove.
	 */
	void removeItemsFromRoot(IRepositoryRoot root);
}
