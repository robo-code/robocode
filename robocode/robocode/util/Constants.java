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
 *     - Rewritten to use the FileUtil class
 *******************************************************************************/
package robocode.util;


import java.io.IOException;
import java.io.File;

import robocode.io.FileUtil;


/**
 * The purpose of this class is to stay backward compatible with
 * RoboRumble@Home.
 * 
 * @author Mathew A. Nelson (original)
 */
public class Constants {
	public static File cwd() {
		return FileUtil.getCwd();	
	}

	public static boolean isIgnoreCase() {
		return (File.separatorChar == '\\');
	}

	public static File getWorkingDirectory() {
		return FileUtil.getCwd();	
	}

	public static void setWorkingDirectory(File newWorkingDirectory) throws IOException {
		FileUtil.setCwd(newWorkingDirectory);
	}
}
