/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository.items;


import net.sf.robocode.repository.root.IRepositoryRoot;

import java.net.URL;
import java.util.Set;


/**
 * @author Pavel Savara (original)
 */
public interface IRepositoryItem extends Comparable<Object> {
	URL getItemURL();
	IRepositoryRoot getRoot();
	Set<String> getFriendlyURLs();

	/**
	 * Updates the repository item if specified 'last modified' date is newer than the one recorded on this item,
	 * or if it is a forced update. 
	 *
	 * @param lastModified is a 'last modified' date (millis since 1970). If this parameter is newer than the one
	 *                     previously set on this item, then this item <em>will</em> be updated.
	 * @param force is a flag specifying if this item is forced to be updated.
	 *              If this parameter is true, then this item <em>will</em> be updated.
	 */
	void update(long lastModified, boolean force);

	long getLastModified();
	boolean isValid();
}
