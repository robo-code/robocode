/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository.items.handlers;


import net.sf.robocode.repository.IRepository;
import net.sf.robocode.repository.items.IRepositoryItem;
import net.sf.robocode.repository.items.RobotItem;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.util.UrlUtil;

import java.net.URL;


/**
 * Item handler for accepting and registering Java class files.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class ClassHandler extends ItemHandler {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRepositoryItem acceptItem(URL itemURL, IRepositoryRoot root, IRepository repository) {
		// Accept and register the item if it is a Java class file, but not an inner class file
		String name = itemURL.toString().toLowerCase();
		if (name.endsWith(".class") && !name.contains("$")) {
			return register(itemURL, root, repository);
		}
		return null;
	}

	/**
	 * Registers the class file with the specified URL as a RobotItem.
	 *
	 * @param classFileUrl is the URL of the class file to register.
	 * @param root is the repository root containing the class file to register.
	 * @param repository is the repository, where the class file is automatically added or updated,
	 *                   when the class file is registered.
	 * @return a RobotItem that has been created or updated in the repository.
	 */
	private RobotItem register(URL classFileUrl, IRepositoryRoot root, IRepository repository) {
		RobotItem item = null;

		// Check if the class file is already registered in the repository
		String friendlyUrl = UrlUtil.removeFileExtension(classFileUrl.toString());
		
		IRepositoryItem repositoryItem = repository.getItem(friendlyUrl);
		if (repositoryItem instanceof RobotItem) {
			item = (RobotItem) repositoryItem;
		}

		// If the class file has not been registered then create a new RobotItem based on the class file URL
		if (item == null) {
			item = new RobotItem(classFileUrl, root);
		}

		// Set the class path URL and class URL on the RobotItem
		item.setClassPathURL(root.getURL());
		item.setClassURL(classFileUrl);

		// Add or update the item in the repository and return it
		repository.addOrUpdateItem(item);
		return item;
	}
}
