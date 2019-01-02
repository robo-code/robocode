/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository.root.handlers;


import net.sf.robocode.repository.IRepository;
import net.sf.robocode.repository.parsers.ClasspathFileParser;
import net.sf.robocode.repository.root.ClasspathRoot;
import net.sf.robocode.repository.root.IRepositoryRoot;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Map;


/**
 * Handler for registering class path roots.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class ClassPathHandler extends RootHandler {
	public void visitDirectory(File dir, boolean isDevel, Map<String, IRepositoryRoot> newRoots, IRepository repository, boolean force) {	
		if (isDevel) {
			File classpathFile = new File(dir, ".classpath");

			if (classpathFile.exists()) {	
				ClasspathFileParser classpathParser = new ClasspathFileParser();

				boolean parsed = true;

				try {
					classpathParser.parse(classpathFile.toURI().toURL());
				} catch (MalformedURLException e) {
					parsed = false;
				}
				if (parsed) {
					String classPath = classpathParser.getClassPath();

					if (classPath != null) {
						File classPathDir = new File(dir, classPath);

						handleDirectory(classPathDir, dir, newRoots, repository, force);			
					}

					for (String sourcePath : classpathParser.getSourcePaths()) {
						if (sourcePath != null) {
							File sourcePathDir = new File(dir, sourcePath);

							handleDirectory(sourcePathDir, dir, newRoots, repository, force);			
						}
					}

					return; // we are done! 
				}
			}
		}
		handleDirectory(dir, null, newRoots, repository, force);
	}

	private void handleDirectory(File dir, File projectDir, Map<String, IRepositoryRoot> newRoots, IRepository repository, boolean force) {
		String key;

		try {
			key = dir.toURI().toURL().toString();
		} catch (MalformedURLException e) {
			return;
		}

		IRepositoryRoot root = repository.getRoots().get(key);
		if (root == null) {
			root = new ClasspathRoot(repository, dir, projectDir);
		} else {
			repository.removeRoot(key);
		}

		root.updateItems(force);
		newRoots.put(key, root);
	}
}
