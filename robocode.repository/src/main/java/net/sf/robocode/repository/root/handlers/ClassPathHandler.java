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
 *     Flemming N. Larsen
 *     - Extended to support Eclipse .classpath files which points at class and
 *       source directories
 *******************************************************************************/
package net.sf.robocode.repository.root.handlers;


import net.sf.robocode.repository.Database;
import net.sf.robocode.repository.parsers.ClasspathFileParser;
import net.sf.robocode.repository.root.ClassPathRoot;
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
	public void visitDirectory(File dir, boolean isDevel, Map<String, IRepositoryRoot> newroots, Map<String, IRepositoryRoot> roots, Database db, boolean force) {	
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

						handleDirectory(classPathDir, dir, newroots, roots, db, force);			
					}

					for (String sourcePath : classpathParser.getSourcePaths()) {
						if (sourcePath != null) {
							File sourcePathDir = new File(dir, sourcePath);

							handleDirectory(sourcePathDir, dir, newroots, roots, db, force);			
						}
					}

					return; // we are done! 
				}
			}
		}
		handleDirectory(dir, null, newroots, roots, db, force);
	}

	private void handleDirectory(File dir, File projectDir, Map<String, IRepositoryRoot> newroots, Map<String, IRepositoryRoot> roots, Database db, boolean force) {
		String key;

		try {
			key = dir.toURI().toURL().toString();
		} catch (MalformedURLException e) {
			return;
		}

		IRepositoryRoot root = roots.get(key);

		if (root == null) {
			root = new ClassPathRoot(db, dir, projectDir);
		} else {
			roots.remove(key);
		}
		root.update(force);
		newroots.put(key, root);
	}
}
