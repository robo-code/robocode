/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.DeathEvent;
import robocode.RobotDeathEvent;
import robocode.AdvancedRobot;


/**
 * @author Flemming N. Larsen (original)
 */
public class RobotDeathEvents extends AdvancedRobot {
	private boolean dead;
	private long enemyCount;

	public void run() {
		enemyCount = getOthers();
		while (!dead) {
			if (enemyCount != getOthers()) {
				throw new RuntimeException("enemyCount != getOthers()");
			}
			execute();
		}
	}

	public void onRobotDeath(RobotDeathEvent e) {
		enemyCount--;
	}

	public void onDeath(DeathEvent e) {
		dead = true;
	}
}
