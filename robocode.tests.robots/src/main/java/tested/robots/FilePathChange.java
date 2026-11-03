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
 * Uses a File subclass with a path that changes after the path has been checked by the robot security, which must not
 * give access to anything but the path that was checked.
 *
 * @author Flemming N. Larsen (original)
 */
public class FilePathChange extends AdvancedRobot {

	public static final String READ_VICTIM_FILE_NAME = "robocode-file-path-read.victim";
	public static final String DELETE_VICTIM_FILE_NAME = "robocode-file-path-delete.victim";

	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(changingFile(READ_VICTIM_FILE_NAME)));

			out.println("Read: " + reader.readLine());
			reader.close();
		} catch (IOException e) {
			out.println("Could not read: " + e.getMessage());
		}
		out.println("Deleted: " + changingFile(DELETE_VICTIM_FILE_NAME).delete());
	}

	private File changingFile(String victimName) {
		// The data directory is <robots>/tested/robots/FilePathChange.data,
		// so the victim file is placed in the parent directory of <robots>.
		File victim = new File(getDataDirectory(), ".." + File.separator + ".." + File.separator + ".."
				+ File.separator + ".." + File.separator + victimName);

		return new ChangingFile(getDataFile("own.txt").getPath(), victim.getPath());
	}

	private static class ChangingFile extends File {
		private static final long serialVersionUID = 1L;

		private final String firstPath;
		private final String laterPath;
		private boolean first = true;

		ChangingFile(String firstPath, String laterPath) {
			super(firstPath);
			this.firstPath = firstPath;
			this.laterPath = laterPath;
		}

		@Override
		public String getPath() {
			// Also called by the constructor of File, before the fields are set
			if (firstPath == null || first) {
				first = firstPath == null;
				return firstPath != null ? firstPath : super.getPath();
			}
			return laterPath;
		}
	}
}
