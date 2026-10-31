/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;

import java.io.File;


/**
 * Attempts to list the files of a directory outside the robot's directories, which must be prevented by the robot
 * security.
 *
 * @author Flemming N. Larsen (original)
 */
public class FileListAttack extends AdvancedRobot {

	public static final String LISTED_MESSAGE = "Listed files: ";

	public void run() {
		// The data directory is <robots>/tested/robots/FileListAttack.data,
		// so this is the parent directory of <robots>.
		File directory = new File(getDataDirectory(), ".." + File.separator + ".." + File.separator + ".."
				+ File.separator + "..");

		String[] files = directory.list();
		out.println(LISTED_MESSAGE + (files == null ? 0 : files.length));
	}
}
