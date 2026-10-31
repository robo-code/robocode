/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 * Attempts to read a file outside the robot's directories, which must be prevented by the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class FileReadAttack extends AdvancedRobot {

	public static final String VICTIM_FILE_NAME = "robocode-file-read.victim";

	public void run() {
		// The data directory is <robots>/tested/robots/FileReadAttack.data,
		// so this file is placed in the parent directory of <robots>.
		File victim = new File(getDataDirectory(), ".." + File.separator + ".." + File.separator + ".."
				+ File.separator + ".." + File.separator + VICTIM_FILE_NAME);

		try {
			BufferedReader reader = new BufferedReader(new FileReader(victim));
			out.println("Read: " + reader.readLine());
			reader.close();
		} catch (IOException e) {
			e.printStackTrace(out);
		}
	}
}
