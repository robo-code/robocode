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
package net.sf.robocode.repository2.items.handlers;

import net.sf.robocode.repository2.root.IRepositoryRoot;
import net.sf.robocode.repository2.items.RobotItem;
import net.sf.robocode.repository2.items.IItem;
import net.sf.robocode.repository2.Database;

import java.net.URL;

/**
 * @author Pavel Savara (original)
 */
public class PropertiesHandler extends ItemHandler {

	public IItem acceptItem(URL itemURL, IRepositoryRoot root, Database db) {
		final String name = itemURL.toString().toLowerCase();
		if (name.endsWith(".properties") && !name.equals("robocode.properties")){
			return register(itemURL, root, db);
		}
		return null;
	}

	private IItem register(URL itemURL, IRepositoryRoot root, Database db) {
		RobotItem item = (RobotItem) db.getOldItem(itemURL.toString());
		if (item == null){
			item = (RobotItem) db.getItem(itemURL.toString());
		}
		if (item == null) {
			item = new RobotItem(null, itemURL, root);
		} else {
			item.setPropertiesUrl(itemURL);
		}
		db.addItem(item);
		return item;
	}
}
