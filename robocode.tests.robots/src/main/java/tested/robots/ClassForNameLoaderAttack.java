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
 * Attempts to load a class with another class loader than the robot's class loader, i.e. the bootstrap class loader.
 *
 * @author Flemming N. Larsen (original)
 */
public class ClassForNameLoaderAttack extends AdvancedRobot {

	public void run() {
		try {
			Class.forName("java.lang.Runtime", true, null);
		} catch (ClassNotFoundException e) {
			e.printStackTrace(out);
		}
	}
}
