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
import net.sf.robocode.repository.items.TeamItem;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.util.UrlUtil;

import java.net.URL;


/**
 * Item handler for accepting and registering robot team files.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class TeamHandler extends ItemHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRepositoryItem acceptItem(URL itemURL, IRepositoryRoot root, IRepository repository) {
		// Accept and register the item if it is a robot team file
		String name = itemURL.toString().toLowerCase();
		if (name.endsWith(".team")) {
			return register(itemURL, root, repository);
		}
		return null;
	}

	/**
	 * Registers the team file with the specified URL as a TeamItem.
	 *
	 * @param teamFileUrl is the URL of the team file to register.
	 * @param root is the repository root containing the team file to register.
	 * @param repository is the repository, where the team file is automatically added or updated,
	 *                   when the team file is registered.
	 * @return a TeamItem that has been created or updated in the repository.
	 */
	private TeamItem register(URL teamFileUrl, IRepositoryRoot root, IRepository repository) {
		TeamItem item = null;

		// Check if the team file is already registered in the repository
		String friendlyUrl = UrlUtil.removeFileExtension(teamFileUrl.toString());

		IRepositoryItem repositoryItem = repository.getItem(friendlyUrl);
		if (repositoryItem instanceof TeamItem) {
			item = (TeamItem) repositoryItem;
		}

		// If the team file has not been registered then create a new TeamItem based on the team file URL
		if (item == null) {
			item = new TeamItem(teamFileUrl, root);
		}

		// Add or update the item in the repository and return it
		repository.addOrUpdateItem(item);
		return item;
	}
}
