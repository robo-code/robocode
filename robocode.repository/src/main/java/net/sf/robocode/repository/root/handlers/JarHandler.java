/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository.root.handlers;


import net.sf.robocode.repository.IRepository;
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
	public void visitDirectory(File dir, boolean isDevel, Map<String, IRepositoryRoot> newRoots, IRepository repository, boolean force) {
		if (!isDevel) {
			// find jar files
			final File[] jars = dir.listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					String path = pathname.toString().toLowerCase();
					return pathname.isFile() && (path.endsWith(".jar") || path.endsWith(".zip"));
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
				IRepositoryRoot root = repository.getRoots().get(key);
				if (root == null) {
					root = new JarRoot(repository, jar);
				} else {
					repository.removeRoot(key);
				}

				root.updateItems(force);
				newRoots.put(key, root);

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
