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
import java.nio.file.Path;
import java.util.Scanner;


/**
 * Attempts to read a file with a Scanner using a Path, which must be prevented by the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class ScannerPathAttack extends AdvancedRobot {

	public void run() {
		Path path = null;

		try {
			new Scanner(path).close();
		} catch (IOException e) {
			e.printStackTrace(out);
		}
	}
}
