/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import robocode.Bullet;


/**
 * From Bug 2410856
 * @author Pavel Savara (original)
 */
public class BadFirePower extends AdvancedRobot {

	public void run() {
		while (true) {
			Bullet bullet = setFireBullet(getTime() - 12.0);

			if (bullet != null) {
				out.println(getTime() + " Bullet power: " + bullet.getPower());
			} else {
				out.println(getTime() + " No bullet");
			}
			execute();
		}
	}

}
