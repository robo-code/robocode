/*******************************************************************************
 * Copyright (c) 2001-2012 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
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
import net.sf.robocode.repository.IRepository;
import net.sf.robocode.dotnet.repository.items.DotNetRobotItem;
import net.sf.robocode.dotnet.repository.root.DllRoot;

import java.net.URL;
import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public class DotnetPropertiesHandler extends PropertiesHandler {

	@Override
	public IItem acceptItem(URL itemURL, IRepositoryRoot root, IRepository repository) {
		String name = itemURL.toString().toLowerCase();
		if (name.contains(".dll!/")) {
			return register(itemURL, root, repository);
		}
		return null;
	}

	private IItem register(URL itemURL, IRepositoryRoot root, IRepository repository) {
		RobotItem item = (RobotItem) repository.getItem(itemURL.toString());
		if (item == null) {
			item = createItem(itemURL, root, repository);
		}
		repository.addOrUpdateItem(item);
		return item;
	}

	@Override
	protected RobotItem createItem(URL itemURL, IRepositoryRoot root, IRepository repository) {
		File file = new File(itemURL.toString().replace(".properties", ".dll"));
		DotNetRobotItem item = new DotNetRobotItem(new DllRoot(repository, file), itemURL);

		item.setClassURL(itemURL);
		return item;
	}
}