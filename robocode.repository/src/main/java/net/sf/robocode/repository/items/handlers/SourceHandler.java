/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.repository.items.handlers;


import net.sf.robocode.repository.IRepository;
import net.sf.robocode.repository.items.IRepositoryItem;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.root.ClasspathRoot;
import net.sf.robocode.repository.root.IRepositoryRoot;

import java.net.URL;


/**
 * Handler for accepting and registering source files, e.g. .java files.
 *
 * @author Flemming N. Larsen (original)
 */
public class SourceHandler extends ItemHandler {
	public IRepositoryItem acceptItem(URL itemURL, IRepositoryRoot root, IRepository repository) {
		final String name = itemURL.toString().toLowerCase();

		if (name.endsWith(".java") && !name.contains("$")) {
			return register(itemURL, root, repository);
		}
		return null;
	}

	private IRepositoryItem register(URL itemURL, IRepositoryRoot root, IRepository repository) {
		RobotItem item = null;

		if (root instanceof ClasspathRoot) {
			String friendly = ((ClasspathRoot) root).getFriendlyProjectURL(itemURL);

			if (friendly != null) {
				item = (RobotItem) repository.getItem(friendly);
			}
		}
		if (item == null) {
			item = (RobotItem) repository.getItem(itemURL.toString());
		}
		if (item == null) {
			item = new RobotItem(itemURL, root);
		}
		item.addSourcePathURL(root.getURL());

		repository.addOrUpdateItem(item);
		return item;
	}
}
