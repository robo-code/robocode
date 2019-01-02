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
import net.sf.robocode.core.Container;

import java.net.URL;


/**
 * Handler for accepting and registering Java properties files.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class PropertiesHandler extends ItemHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRepositoryItem acceptItem(URL itemURL, IRepositoryRoot root, IRepository repository) {
		// Accept and register the item if it is a Java properties file
		String name = itemURL.toString().toLowerCase();
		if (name.endsWith(".properties")) {
			return register(itemURL, root, repository);
		}
		return null;
	}

	/**
	 * Registers the properties file with the specified URL as a RobotItem.
	 *
	 * @param propertiesFileUrl is the URL of the properties file to register.
	 * @param root is the repository root containing the properties file to register.
	 * @param repository is the repository, where the properties file is automatically added or updated,
	 *                   when the properties file is registered.
	 * @return a RobotItem that has been created or updated in the repository.
	 */
	private RobotItem register(URL propertiesFileUrl, IRepositoryRoot root, IRepository repository) {
		RobotItem item = null;

		// Check if the properties file is already registered in the repository
		String friendlyUrl = UrlUtil.removeFileExtension(propertiesFileUrl.toString());

		IRepositoryItem repositoryItem = repository.getItem(friendlyUrl);
		if (repositoryItem instanceof RobotItem) {
			item = (RobotItem) repositoryItem;
		}

		// If the properties file has not been registered then create a new RobotItem based on the properties file URL
		if (item == null) {
			item = createRobotItem(propertiesFileUrl, root, repository);
		}

		// Add or update the item in the repository and return it
		if (item != null) {
			repository.addOrUpdateItem(item);
		}
		return item;
	}

	/**
	 * Creates a new RobotItem based on the properties file URL.
	 * This method will dispatch creating and registering the properties file to another handler,
	 * if the platform is not Java.
	 *
	 * @param propertiesFileUrl is the URL of the properties file.
	 * @param root is the repository root containing the properties file to create the RobotItem from.
	 * @param repository is the repository, where the properties file is automatically added or updated,
	 *                   when the properties file is registered.
	 * @return a new RobotItem that has been created or null if the RobotItem could not be created.
	 */
	private RobotItem createRobotItem(URL propertiesFileUrl, IRepositoryRoot root, IRepository repository) {
		// Create a RobotItem based on the properties file URL
		RobotItem item = new RobotItem(propertiesFileUrl, root);

		// Check if the robot is for the Java platform
		String platform = item.getPlatform();
		if (platform.equalsIgnoreCase("Java")) {
			// Java platform -> set the properties URL on the RobotItem
			item.setPropertiesURL(propertiesFileUrl);
		} else {
			// Another platform -> Look for another properties handler
			PropertiesHandler otherHandler = Container.getComponent(PropertiesHandler.class,
					platform + "PropertiesHandler");

			// If the another properties handler was found then let create the RobotItem; otherwise return null 
			return (otherHandler == null) ? null : otherHandler.createRobotItem(propertiesFileUrl, root, repository);
		}
		return item;
	}
}
