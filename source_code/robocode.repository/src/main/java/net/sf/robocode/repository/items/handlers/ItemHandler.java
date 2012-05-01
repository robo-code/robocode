/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.repository.items.handlers;


import net.sf.robocode.core.Container;
import net.sf.robocode.repository.Database;
import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.root.IRepositoryRoot;

import java.net.URL;
import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public abstract class ItemHandler {
	public abstract IItem acceptItem(URL itemURL, IRepositoryRoot root, Database db);

	public static IItem registerItems(URL itemURL, IRepositoryRoot root, Database db) {
		// walk thru all plugins, give them chance to accept a file
		final List<ItemHandler> itemHandlerList = Container.getComponents(ItemHandler.class);

		for (ItemHandler handler : itemHandlerList) {
			final IItem item = handler.acceptItem(itemURL, root, db);

			if (item != null) {
				return item;
			}
		}
		return null;
	}

}
