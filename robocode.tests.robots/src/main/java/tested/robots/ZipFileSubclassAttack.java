/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;

import java.io.IOException;
import java.util.zip.ZipFile;


/**
 * Attempts to read a file with a subclass of ZipFile, which must be prevented by the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class ZipFileSubclassAttack extends AdvancedRobot {

	public void run() {
		try {
			new MyZipFile("robocode-zip-subclass.victim").close();
		} catch (IOException e) {
			e.printStackTrace(out);
		}
	}

	public static class MyZipFile extends ZipFile {
		public MyZipFile(String name) throws IOException {
			super(name);
		}
	}
}
