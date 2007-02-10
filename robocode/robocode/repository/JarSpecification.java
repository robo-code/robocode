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
import java.io.Serializable;

import robocode.io.FileUtil;
import robocode.io.Logger;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class JarSpecification extends FileSpecification implements Serializable {

	// Used in FileSpecification
	protected JarSpecification(File f, File rootDir, String prefix, boolean developmentVersion) {
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
		} else {
			throw new RuntimeException("JarSpecification can only be constructed from a .jar file");
		}
	}

	@Override
	public String getUid() {
		return getFilePath();
	}
}
