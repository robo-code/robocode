/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;

import java.io.InputStream;


/**
 * Tries to get the real System.in with reflection, which must be prevented by the robot security.
 *
 * @author Flemming N. Larsen (original)
 */
public class SystemInReflection extends AdvancedRobot {

	public void run() {
		try {
			InputStream in = (InputStream) System.class.getField("in").get(null);

			out.println("Got: " + in);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace(out);
		}
	}
}
