/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;


/**
 * Attempts to write a file with a subclass of FileOutputStream, which must be prevented by the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class FileOutputStreamSubclassAttack extends AdvancedRobot {

	public void run() {
		try {
			new MyFileOutputStream("robocode-file-output-subclass.victim");
		} catch (FileNotFoundException e) {
			e.printStackTrace(out);
		}
	}

	public static class MyFileOutputStream extends FileOutputStream {
		public MyFileOutputStream(String name) throws FileNotFoundException {
			super(name);
		}
	}
}
