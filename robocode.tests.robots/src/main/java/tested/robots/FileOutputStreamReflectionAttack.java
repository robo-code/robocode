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
import java.io.FileOutputStream;
import java.io.OutputStream;


/**
 * Attempts to create a FileOutputStream with reflection, which must be prevented by the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class FileOutputStreamReflectionAttack extends AdvancedRobot {

	public static final String VICTIM_FILE_NAME = "robocode-file-output-reflection.victim";

	public void run() {
		// The data directory is <robots>/tested/robots/FileOutputStreamReflectionAttack.data,
		// so this file is placed in the parent directory of <robots>.
		File victim = new File(getDataDirectory(), ".." + File.separator + ".." + File.separator + ".."
				+ File.separator + ".." + File.separator + VICTIM_FILE_NAME);

		try {
			OutputStream output = FileOutputStream.class.getConstructor(File.class).newInstance(victim);
			output.write(1);
			output.close();
		} catch (ReflectiveOperationException e) {
			e.printStackTrace(out);
		} catch (java.io.IOException e) {
			e.printStackTrace(out);
		}
	}
}
