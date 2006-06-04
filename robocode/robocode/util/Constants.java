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


import java.io.*;


/**
 * @author Mathew A. Nelson (original)
 */
public class Constants {
	private static File workingDirectory = getDefaultWorkingDirectory();
	private static boolean isIgnoreCase = (File.separatorChar == '\\');

	private static File getDefaultWorkingDirectory() {
		try {
			return new File("").getCanonicalFile();
		} catch (IOException e) {
			return null;
		}
	}

	public static File cwd() {
		return workingDirectory;	
	}

	public static boolean isIgnoreCase() {
		return isIgnoreCase;
	}

	public static File getWorkingDirectory() {
		return workingDirectory;	
	}

	public static void setWorkingDirectory(File newWorkingDirectory) throws IOException {
		Utils.checkAccess("Change Working Directory");
		workingDirectory = newWorkingDirectory.getCanonicalFile();
	}
}
