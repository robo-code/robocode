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
 * Tries to read from System.in, which is not possible for robots with Java 24+.
 *
 * @author Flemming N. Larsen (original)
 */
public class SystemInUse extends AdvancedRobot {

	public static final String DENIED_MESSAGE = "System.in was denied";

	public void run() {
		try {
			System.in.available();
		} catch (SecurityException e) {
			out.println(DENIED_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace(out);
		}
		for (int i = 0; i < 10; i++) {
			turnLeft(10);
		}
	}
}
