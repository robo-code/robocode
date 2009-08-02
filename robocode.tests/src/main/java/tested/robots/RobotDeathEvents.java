/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package tested.robots;


import robocode.RobotDeathEvent;
import robocode.AdvancedRobot;


/**
 * @author Flemming N. Larsen (original)
 */
public class RobotDeathEvents extends AdvancedRobot {
	private long enemyCount;

	public void run() {
		enemyCount = getOthers();
		while (true) {
			if (enemyCount != getOthers()) {
				throw new RuntimeException("enemyCount != getOthers()");
			}
			execute();
		}
	}

	public void onRobotDeath(RobotDeathEvent e) {
		enemyCount--;
	}
}
