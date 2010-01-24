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


import net.sf.robocode.repository.items.handlers.PropertiesHandler;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.repository.Database;
import net.sf.robocode.dotnet.repository.items.DotNetRobotItem;
import net.sf.robocode.dotnet.repository.root.DllRoot;
import net.sf.robocode.core.Container;

import java.net.URL;
import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public class DotnetPropertiesHandler extends PropertiesHandler {

	@Override
	public IItem acceptItem(URL itemURL, IRepositoryRoot root, Database db) {
		final String name = itemURL.toString().toLowerCase();

		if (name.contains(".dll!/")) {
			return register(itemURL, root, db);
		}
		return null;
	}

	private IItem register(URL itemURL, IRepositoryRoot root, Database db) {
        final String itemKey = itemURL.toString();
		RobotItem item = (RobotItem) db.getOldItem(itemKey);

		if (item == null) {
			item = (RobotItem) db.getItem(itemKey);
		}
		if (item == null) {
			item = new DotNetRobotItem(root, itemURL);
            item.setClassUrl(itemURL);
		} else {
			item.setClassUrl(itemURL);
		}
        db.putItem(itemKey, item);
		return item;
	}

	@Override
	protected RobotItem createItem(URL itemURL, IRepositoryRoot root, Database db) {
		final File file = new File(itemURL.toString().replace(".properties", ".dll"));
		final DotNetRobotItem item = new DotNetRobotItem(new DllRoot(db, file), itemURL);
        item.setClassUrl(itemURL);
		return item;
	}
}
