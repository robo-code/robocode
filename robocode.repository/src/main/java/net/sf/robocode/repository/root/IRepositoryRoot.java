/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.repository.root;


import net.sf.robocode.repository.items.IItem;

import java.io.File;
import java.net.URL;


/**
 * @author Pavel Savara (original)
 */
public interface IRepositoryRoot {
	void update(boolean updateInvalid);
	void update(IItem item, boolean force);
	boolean isChanged(IItem item);
	URL getRootUrl();
	File getRootPath();
	URL getClassPathUrl();
	boolean isJar();
	boolean isDevel();
	void extractJar();
}
