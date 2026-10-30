/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import robocode.RobocodeFileOutputStream;

import java.io.File;
import java.io.IOException;


/**
 * Attempts to use a RobocodeFileOutputStream to write a file outside the robot's data directory,
 * which must be prevented by the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class RobocodeFileOutputStreamAttack extends AdvancedRobot {

	public static final String ATTACK_FILE_NAME = "robocode-file-output-stream.attack";

	public void run() {
		// The data directory is <robots>/tested/robots/RobocodeFileOutputStreamAttack.data,
		// so this file is placed in the parent directory of <robots>.
		File attackFile = new File(getDataDirectory(), ".." + File.separator + ".." + File.separator + ".."
				+ File.separator + ".." + File.separator + ATTACK_FILE_NAME);

		RobocodeFileOutputStream os = null;
		try {
			os = new RobocodeFileOutputStream(attackFile.getPath());
			os.write(1);
		} catch (IOException e) {
			e.printStackTrace(out);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ignore) {}
			}
		}
	}
}
