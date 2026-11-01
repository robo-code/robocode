/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;


/**
 * Reports the enemy names it sees, and whether each name is the same object as the last one.
 *
 * @author Robert Maupin (original) 
 */
public class HiddenNames extends AdvancedRobot {
	private String lastName;

	@Override
	public void run() {
		while (true) {
			turnRadarRight(360);
		}
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		String name = e.getName();

		out.println("Enemy name: " + name);
		if (lastName != null && name != lastName) {
			out.println("The name is a new object");
		}
		lastName = name;
	}
}
