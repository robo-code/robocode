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
 * Attempts to delete a file with the delete mode of ZipFile, which must be prevented by the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class ZipFileDeleteAttack extends AdvancedRobot {

	public void run() {
		try {
			new ZipFile(new File("robocode-zip-delete.victim"), ZipFile.OPEN_READ | ZipFile.OPEN_DELETE).close();
		} catch (IOException e) {
			e.printStackTrace(out);
		}
	}
}
