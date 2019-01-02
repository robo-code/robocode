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
import net.sf.robocode.repository.root.ClasspathRoot;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.util.UrlUtil;

import java.net.URL;


/**
 * Item handler for accepting and registering source files, e.g. Java source files.
 *
 * @author Flemming N. Larsen (original)
 */
public class SourceHandler extends ItemHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRepositoryItem acceptItem(URL itemURL, IRepositoryRoot root, IRepository repository) {
		// Accept and register the item if it is a Java source file, but not an inner Java source file (e.g. mechanical generated)
		String name = itemURL.toString().toLowerCase();
		if (name.endsWith(".java") && !name.contains("$")) {
			return register(itemURL, root, repository);
		}
		return null;
	}

	/**
	 * Registers the source file with the specified URL as a RobotItem.
	 *
	 * @param sourceFileUrl is the URL of the source file to register.
	 * @param root is the repository root containing the source file to register.
	 * @param repository is the repository, where the source file is automatically added or updated,
	 *                   when the source file is registered.
	 * @return a RobotItem that has been created or updated in the repository.
	 */
	private RobotItem register(URL sourceFileUrl, IRepositoryRoot root, IRepository repository) {
		RobotItem item = null;

		// Check if the source file is already registered in the repository with a project URL
		if (root instanceof ClasspathRoot) {
			String projectUrl = ((ClasspathRoot) root).getFriendlyProjectURL(sourceFileUrl);
			if (projectUrl != null) {
				IRepositoryItem repositoryItem = repository.getItem(projectUrl);
				if (repositoryItem instanceof RobotItem) {
					item = (RobotItem) repositoryItem;
				}
			}
		}

		// If no project URL was registered with the source file then check if the source file is registered
		// in the repository. 
		if (item == null) {
			String friendlyUrl = UrlUtil.removeFileExtension(sourceFileUrl.toString());

			IRepositoryItem repositoryItem = repository.getItem(friendlyUrl);
			if (repositoryItem instanceof RobotItem) {
				item = (RobotItem) repositoryItem;
			}
		}

		// If the source file has not been registered then create a new RobotItem based on the source file URL
		if (item == null) {
			item = new RobotItem(sourceFileUrl, root);
		}

		// Add the root URL as source path with the RobotIteam
		item.addSourcePathURL(root.getURL());

		// Add or update the item in the repository and return it
		repository.addOrUpdateItem(item);
		return item;
	}
}
