/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;

import java.util.logging.Logger;


/**
 * Attempts to load a resource bundle with a subclass of Logger, which must be prevented by the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class LoggerBundleAttack extends AdvancedRobot {

	public void run() {
		new MyLogger();
	}

	public static class MyLogger extends Logger {
		MyLogger() {
			super("name", "some.bundle");
		}
	}
}
