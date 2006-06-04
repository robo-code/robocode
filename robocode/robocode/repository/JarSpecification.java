/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode.repository;


import java.io.*;
import robocode.util.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class JarSpecification extends FileSpecification implements Serializable {

	// Used in FileSpecification
	protected JarSpecification(File f, File rootDir, String prefix, boolean developmentVersion) {
		this.rootDir = rootDir;
		this.developmentVersion = developmentVersion;
		valid = true;
		String filename = f.getName();
		String fileType = Utils.getFileType(filename);

		if (fileType.equals(".jar") || fileType.equals(".zip")) {
			setFileLastModified(f.lastModified());
			setFileLength(f.length());
			setFileType(fileType);
			try {
				setFilePath(f.getCanonicalPath());
			} catch (IOException e) {
				Utils.log("Warning:  Unable to determine canonical path for " + f.getPath());
				setFilePath(f.getPath());
			}
			setFileName(f.getName());
		} else {
			throw new RuntimeException("JarSpecification can only be constructed from a .jar file");
		}
	}

	public String getUid() {
		return getFilePath();
	}
}
