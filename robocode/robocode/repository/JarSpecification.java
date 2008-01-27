/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated to use methods from FileUtil and Logger, which replaces methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Code cleanup
 *******************************************************************************/
package robocode.repository;


import java.io.File;
import java.io.IOException;

import robocode.io.FileUtil;
import robocode.io.Logger;
import robocode.manager.RobotRepositoryManager;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class JarSpecification extends FileSpecification {

	// Used in FileSpecification
	protected JarSpecification(File f, File rootDir, String prefix, boolean developmentVersion, RobotRepositoryManager repositoryManager) {
		this.rootDir = rootDir;
		this.developmentVersion = developmentVersion;
		valid = true;
		String filename = f.getName();
		String fileType = FileUtil.getFileType(filename);

		if (fileType.equals(".jar") || fileType.equals(".zip")) {
			setFileLastModified(f.lastModified());
			setFileLength(f.length());
			setFileType(fileType);
			try {
				setFilePath(f.getCanonicalPath());
			} catch (IOException e) {
				Logger.log("Warning:  Unable to determine canonical path for " + f.getPath());
				setFilePath(f.getPath());
			}
			setFileName(f.getName());
			storeJarFile(repositoryManager.getRobotsDirectory(), repositoryManager.getRobotCache());
		} else {
			throw new RuntimeException("JarSpecification can only be constructed from a .jar file");
		}
	}

	// Create and store this new specification as a jar file in the directory containing
	// the robots as a robot package
	private void storeJarFile(File robotDir, File robotCacheDir) {
		File src = null;

		if (rootDir.getName().indexOf(".jar_") == rootDir.getName().length() - 5
				|| rootDir.getName().indexOf(".zip_") == rootDir.getName().length() - 5) {
			if (rootDir.getParentFile().equals(robotCacheDir)) {
				src = new File(robotDir, rootDir.getName().substring(0, rootDir.getName().length() - 1));
			} else if (rootDir.getParentFile().getParentFile().equals(robotCacheDir)) {
				src = new File(rootDir.getParentFile(), rootDir.getName().substring(0, rootDir.getName().length() - 1));
			}
		}
		if (src != null && !src.exists()) {
			src = null;
		}
		packageFile = src;
	}

	@Override
	public String getUid() {
		return getFilePath();
	}
}
