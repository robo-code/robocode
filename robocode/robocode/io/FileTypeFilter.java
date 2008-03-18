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
 *     - Moved the original robocode.util.RobocodeFileFilter into this new class
 *       and added javadoc comments
 *     - Changed the constructor to make a deep copy of the file types
 *******************************************************************************/
package robocode.io;


import java.io.File;
import java.io.FileFilter;


/**
 * A filter for filtering out files in a file list based on file extensions.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class FileTypeFilter implements FileFilter {

	// Array of file types
	private String[] fileTypes;

	/**
	 * Creates a new file type filter.
	 *
	 * @param fileTypes an array of file extensions that is accepted for this
	 *    file filter, e.g. ".class", ".jar", ".zip" etc.
	 */
	public FileTypeFilter(String[] fileTypes) {
		super();
		if (fileTypes == null) {
			this.fileTypes = null;
		} else {
			this.fileTypes = new String[fileTypes.length];
			System.arraycopy(fileTypes, 0, this.fileTypes, 0, fileTypes.length);
		}
	}

	/**
	 * Tests if a specified file should be included in a file list.
	 *
	 * @param file the file that must be tested against this file filter.
	 * @return {@code true} if the file is accepted for the file list;
	 *    {@code false} otherwise.
	 */
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}

		String filename = file.getName();

		for (String fileType : fileTypes) {
			if (filename.length() > fileType.length()) {
				if (filename.substring(filename.length() - fileType.length()).equals(fileType)) {
					return true;
				}
			}
		}
		return false;
	}
}
