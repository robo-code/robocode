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
package net.sf.robocode.repository2.root.handlers;

import net.sf.robocode.repository2.root.IRepositoryRoot;
import net.sf.robocode.repository2.root.JarRoot;
import net.sf.robocode.repository2.Database;
import net.sf.robocode.io.Logger;

import java.io.File;
import java.io.FileFilter;
import java.util.Hashtable;
import java.net.MalformedURLException;

/**
 * @author Pavel Savara (original)
 */
public class JarHandler extends RootHandler {
	public void visitDirectory(File dir, boolean isDevel, Hashtable<String, IRepositoryRoot> newroots, Hashtable<String, IRepositoryRoot> roots, Database db) {
		try {
			if (!isDevel) {
				// find jar files
				final File[] jars = dir.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						final String low = pathname.toString().toLowerCase();

						return pathname.isFile() && (low.endsWith(".jar") || low.endsWith(".zip"));
					}
				});

				// update jar files
				for (File jar : jars) {
					final String key = jar.toURL().toString();
					IRepositoryRoot root = roots.get(key);

					if (root == null) {
						root = new JarRoot(db, jar);
					} else {
						roots.remove(key);
					}

					root.update();
					newroots.put(jar.toURL().toString(), root);
				}

			}
		} catch (MalformedURLException e) {
			Logger.logError(e);
		}
	}
}
