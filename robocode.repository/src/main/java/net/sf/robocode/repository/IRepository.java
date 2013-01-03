/*******************************************************************************
 * Copyright (c) 2001-2012 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.repository;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.root.IRepositoryRoot;


public interface IRepository {

	public void load(InputStream in);

	public void save(OutputStream out);

	/**
	 * Adds or updates an item. If the item is not present in the repository, it will be added;
	 * otherwise it will be updated, if the specified item has a never version than the existing
	 * item in the repository.<p>
	 *
	 * The item can be retrieved using {@link IRepository#getItem(String)} using a "friendly URL"
	 * identifying the item. See {@link IItem#getFriendlyURLs()}.
	 *
	 * @param item is the item to add or update.
	 */
	void addOrUpdateItem(IItem item);

	IItem getItem(String friendlyURL);

	Map<String, IItem> getItems();

	Map<String, IRepositoryRoot> getRoots();
	
	void removeRoot(String friendlyURL);
	
	void removeItemsFromRoot(IRepositoryRoot root);
}
