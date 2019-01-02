/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;


/**
 * @author Pavel Savara (original)
 * From bug 2405844
 */
public class GunHeat extends AdvancedRobot {

	public void run() {
		while (true) {
			setFireBullet(3.0);
			out.println(getTime() + " gunHeat after fire: " + getGunHeat());
			execute();
		}
	}
}
