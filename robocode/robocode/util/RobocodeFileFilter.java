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
 *******************************************************************************/
package robocode.util;


/**
 * @author Mathew A. Nelson (original)
 */
public class RobocodeFileFilter implements java.io.FileFilter {
	String fileTypes[] = null;

	/**
	 * RobocodeFilenameFilter constructor
	 */
	public RobocodeFileFilter(String fileTypes[]) {
		super();
		this.fileTypes = fileTypes;
	}

	/**
	 * Tests if a specified file should be included in a file list.
	 *
	 * @param   dir    the directory in which the file was found.
	 * @param   name   the name of the file.
	 * @return  <code>true</code> if and only if the name should be
	 * included in the file list; <code>false</code> otherwise.
	 */
	public boolean accept(java.io.File file) {
		if (file.isDirectory()) {
			return true;
		}

		for (int i = 0; i < fileTypes.length; i++) {
			if (file.getName().length() > fileTypes[i].length()) {
				if (file.getName().substring(file.getName().length() - fileTypes[i].length()).equals(fileTypes[i])) {
					return true;
				}
			}
		}
		return false;
	}
}
