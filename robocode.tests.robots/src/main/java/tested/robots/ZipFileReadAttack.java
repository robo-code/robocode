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
import java.io.IOException;
import java.util.zip.ZipFile;


/**
 * Attempts to read a file outside the robot's directories with a ZipFile, which must be prevented by the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class ZipFileReadAttack extends AdvancedRobot {

	public void run() {
		// The data directory is <robots>/tested/robots/ZipFileReadAttack.data,
		// so this file is placed in the parent directory of <robots>.
		File victim = new File(getDataDirectory(), ".." + File.separator + ".." + File.separator + ".."
				+ File.separator + ".." + File.separator + "robocode-zip-read.victim");

		try {
			new ZipFile(victim).close();
		} catch (IOException e) {
			e.printStackTrace(out);
		}
	}
}
