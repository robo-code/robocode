/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;

import java.io.File;


/**
 * Attempts to delete a file outside the robot's data directory, which must be prevented by the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class FileDeleteAttack extends AdvancedRobot {

	public static final String VICTIM_FILE_NAME = "robocode-file-delete.victim";

	public void run() {
		// The data directory is <robots>/tested/robots/FileDeleteAttack.data,
		// so this file is placed in the parent directory of <robots>.
		File victim = new File(getDataDirectory(), ".." + File.separator + ".." + File.separator + ".."
				+ File.separator + ".." + File.separator + VICTIM_FILE_NAME);

		if (victim.delete()) {
			out.println("Deleted: " + victim);
		}
	}
}
