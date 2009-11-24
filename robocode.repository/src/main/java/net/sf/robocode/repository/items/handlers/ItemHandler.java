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
 *     Flemming N. Larsen
 *     - Added getFullRobotNameFromURL() method
 *******************************************************************************/
package net.sf.robocode.repository.items.handlers;


import net.sf.robocode.core.Container;
import net.sf.robocode.repository.Database;
import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.root.ClassPathRoot;
import net.sf.robocode.repository.root.IRepositoryRoot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


/**
 * Abstract class for handlers for accepting and registering a specific item type.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
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

	public static String getItemKey(URL itemURL, IRepositoryRoot root) {
		assert (itemURL != null);
		assert (root != null);

		String name = itemURL.toString();

		if (root instanceof ClassPathRoot) {
			ClassPathRoot croot = (ClassPathRoot) root;
			
			if (!croot.getRootPath().equals(croot.getParentPath())) {
				String parentPath = null;
	
				try {
					parentPath = croot.getParentPath().toURI().toURL().toString();
				} catch (MalformedURLException ignore) {}		
	
				if (parentPath != null) {
					name = name.substring(croot.getRootUrl().toString().length());
					name = parentPath + name;
				}
			}
		}
		return name.substring(0, name.lastIndexOf('.'));
	}
}
