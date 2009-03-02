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
package net.sf.robocode.js.repository.items.handlers;


import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.items.handlers.ItemHandler;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.repository.Database;
import net.sf.robocode.js.repository.items.JsRobotItem;

import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public class JsScriptHandler extends ItemHandler {
	public IItem acceptItem(URL itemURL, IRepositoryRoot root, Database db) {
		final String name = itemURL.toString().toLowerCase();

		if (name.endsWith(".js")) {
			return register(itemURL, root, db);
		}
		return null;
	}

	private IItem register(URL itemURL, IRepositoryRoot root, Database db) {
		JsRobotItem item = (JsRobotItem) db.getOldItem(itemURL.toString());

		if (item == null) {
			item = (JsRobotItem) db.getItem(itemURL.toString());
		}
		if (item == null) {
			item = new JsRobotItem(itemURL, null, root);
		} else {
			item.setClassUrl(itemURL);
		}
		db.addItem(item);
		return item;
	}
}
