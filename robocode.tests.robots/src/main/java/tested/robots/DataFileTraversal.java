/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;


/**
 * Asks for data files outside the robot's data directory in different ways, which must be prevented by the robot
 * security, also when the name only becomes a relative path after Robocode has processed it.
 *
 * @author Flemming N. Larsen (original)
 */
public class DataFileTraversal extends AdvancedRobot {

	public static final String[] VICTIMS = {
			"robocode-datafile-1.victim", "robocode-datafile-2.victim", "robocode-datafile-3.victim"};

	public void run() {
		String up = ".*." + java.io.File.separator + ".*." + java.io.File.separator + ".*." + java.io.File.separator + ".*."
				+ java.io.File.separator;
		String plain = ".." + java.io.File.separator + ".." + java.io.File.separator + ".." + java.io.File.separator + ".."
				+ java.io.File.separator;

		attempt(up + VICTIMS[0]);
		attempt(plain + VICTIMS[1]);
		attempt(new java.io.File(getDataDirectory(), plain + VICTIMS[2]).getAbsolutePath());
	}

	private void attempt(String name) {
		try {
			out.println("Got: " + getDataFile(name));
		} catch (SecurityException e) {
			out.println("Denied: " + e.getMessage());
		}
	}
}
