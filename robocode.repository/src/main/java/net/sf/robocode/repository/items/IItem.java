/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.repository.items;


import net.sf.robocode.repository.root.IRepositoryRoot;

import java.net.URL;
import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public interface IItem extends Comparable<Object> {
	URL getItemURL();
	IRepositoryRoot getRoot();
	List<String> getFriendlyURLs();

	void update(long lastModified, boolean force);
	long getLastModified();
	boolean isValid();
}
