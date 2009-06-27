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
package net.sf.robocode.dotnet.repository.items.handlers;


import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.items.handlers.ItemHandler;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.repository.Database;
import net.sf.robocode.dotnet.repository.items.DotNetRobotItem;

import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public class DllHandler extends ItemHandler {
	public IItem acceptItem(URL itemURL, IRepositoryRoot root, Database db) {
		final String name = itemURL.toString().toLowerCase();

		if (name.endsWith(".dll")) {
			return register(itemURL, root, db);
		}
		return null;
	}

	private IItem register(URL itemURL, IRepositoryRoot root, Database db) {
		DotNetRobotItem item = (DotNetRobotItem) db.getOldItem(itemURL.toString());

		if (item == null) {
			item = (DotNetRobotItem) db.getItem(itemURL.toString());
		}
		if (item == null) {
			item = new DotNetRobotItem(itemURL, null, root);
		} else {
			item.setClassUrl(itemURL);
		}
		db.addItem(item);
		return item;
	}
}
