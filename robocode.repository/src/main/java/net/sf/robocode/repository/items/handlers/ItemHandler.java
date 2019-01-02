/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository.items.handlers;


import net.sf.robocode.core.Container;
import net.sf.robocode.repository.IRepository;
import net.sf.robocode.repository.items.IRepositoryItem;
import net.sf.robocode.repository.root.IRepositoryRoot;

import java.net.URL;
import java.util.List;


/**
 * Abstract item handler used for accepting and registering specific item types.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public abstract class ItemHandler {

	/**
	 * Tests whether or not the item with at the specified item URL is accepted and can be handled by this item handler.
	 *
	 * @param itemUrl is the URL of the item to test.
	 * @param root is the repository root containing the item to test.
	 * @param repository is the repository, where the item is automatically added or updated, if the item is accepted.
	 * @return a repository item that has been created or updated in the repository or null if the item was not
	 *         accepted by this item handler.
	 */
	protected abstract IRepositoryItem acceptItem(URL itemUrl, IRepositoryRoot root, IRepository repository);

	/**
	 * Tests whether or not the item with at the specified item URL is accepted and can be handled by <em>any</em>
	 * available item handler.
	 *
	 * @param itemUrl is the URL of the item to test.
	 * @param root is the repository root containing the item to test.
	 * @param repository is the repository, where the item is automatically added or updated, if the item is accepted.
	 * @return a repository item that has been created or updated in the repository or null if the item was not
	 *         accepted by an item handler.
	 */
	public final static IRepositoryItem registerItem(URL itemUrl, IRepositoryRoot root, IRepository repository) {
		// Test if any available item handler will accept and register the item
		List<ItemHandler> itemHandlers = Container.getComponents(ItemHandler.class);
		for (ItemHandler handler : itemHandlers) {
			IRepositoryItem repositoryItem = handler.acceptItem(itemUrl, root, repository);
			if (repositoryItem != null) {
				return repositoryItem;
			}
		}
		return null;
	}
}
