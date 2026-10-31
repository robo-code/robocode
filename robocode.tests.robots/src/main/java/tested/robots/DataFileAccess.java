/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import robocode.RobocodeFileOutputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;


/**
 * Uses its own data file in all the ways that robots are allowed to, which must keep working with the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class DataFileAccess extends AdvancedRobot {

	public static final String OK_MESSAGE = "Data file access OK";

	public void run() {
		try {
			File dir = getDataDirectory(); // created by Robocode
			File file = getDataFile("data.txt");
			PrintStream ps = new PrintStream(new RobocodeFileOutputStream(file));
			ps.println("hello");
			ps.close();

			check(file.exists(), "exists");
			check(file.isFile(), "isFile");
			check(dir.isDirectory(), "isDirectory");
			check(file.length() > 0, "length");
			check(file.lastModified() > 0, "lastModified");
			check(file.canRead(), "canRead");

			BufferedReader reader = new BufferedReader(new FileReader(file));
			check("hello".equals(reader.readLine()), "FileReader");
			reader.close();

			FileInputStream fis = new FileInputStream(file.getPath());
			check(fis.read() == 'h', "FileInputStream");
			fis.close();

			String[] names = dir.list();
			check(names != null && names.length > 0, "list");
			File[] files = dir.listFiles();
			check(files != null && files.length > 0, "listFiles");

			check(file.delete(), "delete");
			check(!file.exists(), "deleted");

			out.println(OK_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace(out);
		}
	}

	private void check(boolean condition, String what) {
		if (!condition) {
			out.println("Failed: " + what);
			throw new IllegalStateException("Failed: " + what);
		}
	}
}
