/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.util;


/**
 * Utility class used for working on URLs. 
 * 
 * @author Flemming N. Larsen (original)
 */
public class UrlUtil {

	/**
	 * Removes a file extension from a string containing a file name, e.g. the string "filename.ext" would become using
	 * this method "filename".
	 *
	 * @param fileName is a string containing a file name with an extension to remove.
	 * @return a new string where the file extension has been removed from the file name.
	 *         If no extension is found, the input file name is returned.
	 */
	public static String removeFileExtension(String fileName) {
		if (fileName != null) {
			int index = fileName.lastIndexOf('.');
			if (index > 0 && index < (fileName.length() - 1)) {
				return fileName.substring(0, index);
			}
		}
		return fileName;
	}	
}
