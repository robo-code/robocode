/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package tested.robots;


import robocode.AdvancedRobot;
import robocode.Bullet;


/**
 * @author Pavel Savara (original)
 *         From Bug 2410856
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
