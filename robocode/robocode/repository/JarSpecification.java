/*******************************************************************************
 * Copyright (c) 2001, 2006 Mathew Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.repository;


import java.io.*;
import robocode.util.*;


public class JarSpecification extends FileSpecification implements Serializable {

	// Used in FileSpecification
	protected JarSpecification(File f, File rootDir, String prefix, boolean developmentVersion) {
		this.rootDir = rootDir;
		this.developmentVersion = developmentVersion;
		valid = true;
		String filename = f.getName();
		String filepath = f.getPath();
		String fileType = Utils.getFileType(filename);

		if (fileType.equals(".jar") || fileType.equals(".zip")) {
			setFileLastModified(f.lastModified());
			setFileLength(f.length());
			setFileType(fileType);
			try {
				setFilePath(f.getCanonicalPath());
			} catch (IOException e) {
				log("Warning:  Unable to determine canonical path for " + f.getPath());
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
	
	private void log(String s) {
		Utils.log(s);
	}
	
	private void log(String s, Throwable t) {
		Utils.log(s, t);
	}
	
	private void log(Throwable e) {
		Utils.log(e);
	}
	
}

