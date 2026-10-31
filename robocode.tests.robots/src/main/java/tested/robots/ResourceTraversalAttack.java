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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Attempts to read a file outside the robot's class path as a resource, which must be prevented by the robot
 * security.
 *
 * @author Flemming N. Larsen (original)
 */
public class ResourceTraversalAttack extends AdvancedRobot {

	public static final String VICTIM_FILE_NAME = "robocode-resource.victim";
	public static final String READ_MESSAGE = "Read: ";

	public void run() {
		// The robot's class path is <robots>, so this file is placed in the parent directory of <robots>.
		InputStream is = getClass().getResourceAsStream("/../" + VICTIM_FILE_NAME);

		if (is == null) {
			out.println("Resource not found");
			return;
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			out.println(READ_MESSAGE + reader.readLine());
			reader.close();
		} catch (IOException e) {
			e.printStackTrace(out);
		}
	}
}
