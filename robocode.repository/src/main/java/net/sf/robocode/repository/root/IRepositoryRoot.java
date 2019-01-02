/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository.root;


import net.sf.robocode.repository.items.IRepositoryItem;

import java.io.File;
import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public interface IRepositoryRoot {
	URL getURL();
	File getPath();

	/**
	 * Updates all items accessible from this repository root.
	 * Only items that needs to be updated will be updated, e.g. by checking the 'last modified' date on the item.
	 *
	 * @param force is a flag specifying if the items are forced to be updated.
	 *              If this parameter is true, then all items <em>will</em> be updated.
	 */
	void updateItems(boolean force);

	void updateItem(IRepositoryItem repositoryItem, boolean force);

	boolean isJAR();
	boolean isDevelopmentRoot();
	void extractJAR();
}
