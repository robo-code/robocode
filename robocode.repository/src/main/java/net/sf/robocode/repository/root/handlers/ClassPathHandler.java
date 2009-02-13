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
package net.sf.robocode.repository.root.handlers;


import net.sf.robocode.repository.Database;
import net.sf.robocode.repository.root.ClassPathRoot;
import net.sf.robocode.repository.root.IRepositoryRoot;

import java.io.File;
import java.util.Hashtable;


/**
 * @author Pavel Savara (original)
 */
public class ClassPathHandler extends RootHandler {
	public void visitDirectory(File dir, boolean isDevel, Hashtable<String, IRepositoryRoot> newroots, Hashtable<String, IRepositoryRoot> roots, Database db, boolean updateInvalid) {
		final String key = dir.toURI().toString();
		IRepositoryRoot root = roots.get(key);

		if (root == null) {
			root = new ClassPathRoot(db, dir);
		} else {
			roots.remove(key);
		}
		root.update(updateInvalid);
		newroots.put(dir.toURI().toString(), root);
	}
}
