/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;


/**
 * Uses Class.forName(String) like the code generated for class literals by old Java compilers, which robots are
 * allowed to use.
 *
 * @author Flemming N. Larsen (original)
 */
public class ClassForName extends AdvancedRobot {

	public static final String OK_MESSAGE = "Class found: ";

	public void run() {
		try {
			out.println(OK_MESSAGE + Class.forName("tested.robots.Ahead").getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace(out);
		}
	}
}
