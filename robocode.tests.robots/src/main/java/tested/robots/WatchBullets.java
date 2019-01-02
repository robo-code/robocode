/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.*;
import static robocode.util.Utils.normalRelativeAngle;


/**
 * @author Pavel Savara (original)
 */
public class WatchBullets extends AdvancedRobot {
	public void run() {
		while (true) {
			ahead(100);
			turnGunRight(360);
			back(100);
			turnGunRight(360);
		}
	}

	Bullet bullet;

	public void onStatus(StatusEvent event) {
		dump();
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		// Calculate exact location of the robot
		double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngle(absoluteBearing - getGunHeading());

		// If it's close enough, fire!
		if (Math.abs(bearingFromGun) <= 3) {
			turnGunRight(bearingFromGun);
			// We check gun heat here, because calling fire()
			// uses a turn, which could cause us to lose track
			// of the other robot.
			if (getGunHeat() == 0 && bullet == null) {
				final Bullet lbullet = fireBullet(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));

				bullet = lbullet;
			}
		} // otherwise just set the gun to turn.
		// Note:  This will have no effect until we call scan()
		else {
			turnGunRight(bearingFromGun);
		}
		// Generates another scan event if we see a robot.
		// We only need to call this if the gun (and therefore radar)
		// are not turning.  Otherwise, scan is called automatically.
		if (bearingFromGun == 0) {
			scan();
		}
	}

	private void dump() {
		if (bullet != null) {
			out.println(
					getTime() + " " + bullet.getX() + " " + bullet.getY() + " " + bullet.getHeading() + " "
					+ bullet.isActive());
		}
	}
}
