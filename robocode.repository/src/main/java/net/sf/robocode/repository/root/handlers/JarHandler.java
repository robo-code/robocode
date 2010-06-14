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
package net.sf.robocode.repository.root.handlers;


import net.sf.robocode.repository.Database;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.repository.root.JarRoot;
import net.sf.robocode.io.Logger;
import net.sf.robocode.io.URLJarCollector;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.util.Map;


/**
 * @author Pavel Savara (original)
 */
public class JarHandler extends RootHandler {
	public void visitDirectory(File dir, boolean isDevel, Map<String, IRepositoryRoot> newroots, Map<String, IRepositoryRoot> roots, Database db, boolean force) {
		if (!isDevel) {
			// find jar files
			final File[] jars = dir.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					final String low = pathname.toString().toLowerCase();

					return pathname.isFile() && (low.endsWith(".jar") || low.endsWith(".zip"));
				}
			});

			if (jars == null) {
				return; // Avoid NPE by returning
			}

			// update jar files
			for (File jar : jars) {
				String key;

				try {
					key = "jar:" + jar.toURI().toURL().toString() + "!/";
				} catch (MalformedURLException e) {
					e.printStackTrace();
					continue;
				}
				IRepositoryRoot root = roots.get(key);

				if (root == null) {
					root = new JarRoot(db, jar);
				} else {
					roots.remove(key);
				}

				root.update(force);
				newroots.put(key, root);

				try {
					URLJarCollector.closeJarURLConnection(jar.toURI().toURL());
				} catch (MalformedURLException e) {
					Logger.logError(e);
				}
				URLJarCollector.gc();
			}
		}
	}
}
