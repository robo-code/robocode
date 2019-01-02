/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.dotnet.repository.root.handlers;


import net.sf.robocode.dotnet.repository.root.DllRootHelper;
import net.sf.robocode.dotnet.repository.root.DllRoot;
import net.sf.robocode.repository.root.handlers.RootHandler;
import net.sf.robocode.repository.root.IRepositoryRoot;
import net.sf.robocode.repository.IRepository;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class DllHandler extends RootHandler {

	public void open() {
		DllRootHelper.Open();
	}
    
	public void close() {
		DllRootHelper.Close();
	}

	public void visitDirectory(File dir, boolean isDevel, Map<String, IRepositoryRoot> newRoots, IRepository repository, boolean force) {
		// find dll files
		final File[] dlls = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				String path = pathname.toString().toLowerCase();
				return pathname.isFile() && path.endsWith(".dll") && !path.equalsIgnoreCase("robocode.dll")
						&& !path.contains("jni4net");
			}
		});

		if (dlls != null) {
			// update DLL files
			for (File dll : dlls) {
				String key = dll.toURI().toString();
				IRepositoryRoot root = repository.getRoots().get(key);
				if (root == null) {
					root = new DllRoot(repository, dll);
				} else {
					repository.removeRoot(key);
				}

				root.updateItems(force);
				newRoots.put(dll.toURI().toString(), root);
			}
		}
	}
}
