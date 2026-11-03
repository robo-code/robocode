/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;

import java.awt.AWTKeyStroke;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Uses the Java classes that existing robots use, like Logger and AWTKeyStroke, which must keep working with the robot
 * security.
 *
 * @author Flemming N. Larsen (original)
 */
public class LegacyApiUse extends AdvancedRobot {

	public static final String OK_MESSAGE = "Legacy API use OK";

	public void run() {
		Logger.getLogger(LegacyApiUse.class.getName()).log(Level.FINEST, null, new CloneNotSupportedException());

		AWTKeyStroke keyStroke = AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_A, 0);

		if (keyStroke.getKeyCode() == KeyEvent.VK_A && keyStroke.getModifiers() == 0) {
			out.println(OK_MESSAGE);
		}
		for (int i = 0; i < 10; i++) {
			turnLeft(10);
		}
	}
}
