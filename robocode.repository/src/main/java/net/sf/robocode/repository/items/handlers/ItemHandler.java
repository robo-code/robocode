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

import java.io.File;
import java.net.URISyntaxException;
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

	/**
	 * Gets a suitable item key, which ensure that files belonging to an item will be put into the same item.
	 * If the root has a parent path, this path will be used as the key. Otherwise the item URL without the
	 * ending file extension will be used as item key.
	 *
	 * @param itemURL the URL of the item.
	 * @param root the repository root which the item belong to.
	 * @return an item key based on the specified item URL and repository root.
	 */
	public static String getItemKey(URL itemURL, IRepositoryRoot root) {
		assert (itemURL != null);
		assert (root != null);

		// Check if the ClassPathRoot contains a parent path. If so, we use the parent as
		// item key as this points to a project in a workspace can contain lots of individual
		// directories with class files, properties files, and java files.
		if (root instanceof ClassPathRoot) {
			File parentPath = ((ClassPathRoot) root).getParentPath();
			
			if (parentPath != null) {
				return parentPath.toString() + getItemPart(itemURL);
			}
		}

		// We don't have a parent path, so use the item URL without the file extension
		String name = itemURL.toString();

		return name.substring(0, name.lastIndexOf('.'));
	}

	/**
	 * Returns the item part name of the specified item URL.
	 *
	 * E.g. the itemURL "file:/C:/robot-workspace/holiday/bin/nat/holiday/Card.class"
	 * will output "Card", which suits well as last part of an item key.
	 */
	private static String getItemPart(URL itemURL) {
		String name;

		try {
			name = itemURL.toURI().toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}

		name = name.replaceAll("\\\\", "/"); // Turn all backslashes into slashes

		int index = name.lastIndexOf('/');

		if (index > 0) {
			name = name.substring(index);
		}
		index = name.indexOf('.');
		if (index > 0) {
			name = name.substring(0, index);
		}		
		return name;
	}
}
