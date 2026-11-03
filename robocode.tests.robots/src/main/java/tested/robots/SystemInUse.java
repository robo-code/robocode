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


/**
 * Tries to read from System.in, which must be prevented by the robot security. The robot must still be loaded, as
 * existing robots may have code using System.in that never runs.
 *
 * @author Flemming N. Larsen (original)
 */
public class SystemInUse extends AdvancedRobot {

	public void run() {
		// Let the battle run for a while, so the program can read from System.in during the battle
		for (int i = 0; i < 100; i++) {
			doNothing();
		}
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace(out);
		}
	}
}
