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
import net.sf.robocode.repository.items.TeamItem;
import net.sf.robocode.repository.root.IRepositoryRoot;

import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public class TeamHandler extends ItemHandler {
	public IItem acceptItem(URL itemURL, IRepositoryRoot root, Database db) {
		if (itemURL.toString().toLowerCase().endsWith(".team")) {
			return register(itemURL, root, db);
		}
		return null;
	}

	private IItem register(URL itemURL, IRepositoryRoot root, Database db) {
		TeamItem item = (TeamItem) db.getOldItem(itemURL.toString());

		if (item == null) {
			item = (TeamItem) db.getItem(itemURL.toString());
		}
		if (item == null) {
			item = new TeamItem(itemURL, root);
		}
		db.addItem(item);
		return item;
	}
}
