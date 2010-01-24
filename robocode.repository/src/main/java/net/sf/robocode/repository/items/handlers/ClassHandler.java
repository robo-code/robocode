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


import net.sf.robocode.repository.Database;
import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.root.IRepositoryRoot;

import java.net.URL;


/**
 * Handler for accepting and registering .class files.
 *
 * @author Pavel Savara (original)
 */
public class ClassHandler extends ItemHandler {
	public IItem acceptItem(URL itemURL, IRepositoryRoot root, Database db) {
		final String name = itemURL.toString().toLowerCase();

		if (name.endsWith(".class") && !name.contains("$")) {
			return register(itemURL, root, db);
		}
		return null;
	}

	private IItem register(URL itemURL, IRepositoryRoot root, Database db) {
		final String itemKey = getItemKey(itemURL, root);

		RobotItem item = (RobotItem) db.getOldItem(itemKey);

		if (item == null) {
			item = (RobotItem) db.getItem(itemKey);
		}
		if (item == null) {
			item = new RobotItem(root);
		}
		item.setClassPathURL(root.getRootUrl());
		item.setClassUrl(itemURL);

		db.putItem(itemKey, item);
		return item;
	}
}
