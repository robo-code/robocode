/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.repository;


import robocode.ui.IRepositoryPlugin;
import robocode.manager.RobotRepositoryManager;
import robocode.manager.RobocodeManager;
import robocode.io.FileUtil;

import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public class JavaRepositoryPlugin implements IRepositoryPlugin {
	public String[] getExtensions() {
		return new String[] { ".class", ".jar", ".team", ".jar.zip" };
	}

	public IFileSpecification createSpecification(RobotRepositoryManager repositoryManager, File f, File rootDir, String prefix, boolean developmentVersion) {
		// Get the file name for the new file specification to create
		String filename = f.getName();

		// Get the file type (i.e. extension).
		// This is used to decide upon which type of file specification to create and return
		String fileType = FileUtil.getFileType(filename);

		// Create the new file specification based on the input parameters

		FileSpecification newSpec = null;

		if (fileType.equals(".team")) {
			// If the specification is a robot team package then
			// return the file specification as a TeamSpecification
			newSpec = new TeamSpecification(f, rootDir, prefix, developmentVersion);
		} else if (fileType.equals(".jar") || fileType.equals(".zip")) {
			// If the specification is a robot package (jar or zip file) then
			// return the file specification as a JarSpecification
			newSpec = new JarSpecification(f, rootDir, prefix, developmentVersion, repositoryManager);

		} else if (fileType.equals(".java") || (fileType.equals(".class") || fileType.equals(".properties"))) {
			// Otherwise, create a plain RobotSpecification as a ClassSpecification,
			// that is a specification based on the robot classes
			newSpec = new RobotSpecification(f, rootDir, prefix, developmentVersion);
			if (!(developmentVersion || newSpec.getValid())) {
				newSpec = new ClassSpecification((RobotSpecification) newSpec);
			}
		}

		return newSpec;
	}

	public void setRobocodeManager(RobocodeManager root) {// TODO ZAMO ?
	}
}
