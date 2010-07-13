/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.repository.items.handlers;


import net.sf.robocode.repository.Database;
import net.sf.robocode.repository.items.IItem;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.root.ClassPathRoot;
import net.sf.robocode.repository.root.IRepositoryRoot;

import java.net.URL;


/**
 * Handler for accepting and registering source files, e.g. .java files.
 *
 * @author Flemming N. Larsen (original)
 */
public class SourceHandler extends ItemHandler {
	public IItem acceptItem(URL itemURL, IRepositoryRoot root, Database db) {
		final String name = itemURL.toString().toLowerCase();

		if (name.endsWith(".java") && !name.contains("$")) {
			return register(itemURL, root, db);
		}
		return null;
	}

	private IItem register(URL itemURL, IRepositoryRoot root, Database db) {
		RobotItem item = null;

		if (root instanceof ClassPathRoot) {
			String friendly = ((ClassPathRoot) root).getFriendlyProjectURL(itemURL);

			if (friendly != null) {
				item = (RobotItem) db.getItem(friendly);
			}
		}
		if (item == null) {
			item = (RobotItem) db.getItem(itemURL.toString());
		}
		if (item == null) {
			item = new RobotItem(itemURL, root);
		}
		item.addSourcePathURL(root.getURL());

		db.putItem(item);
		return item;
	}
}
