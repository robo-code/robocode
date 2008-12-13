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
package sampleex;


import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;
import static robocode.util.Utils.normalRelativeAngle;

import java.awt.*;


/**
 * @author Pavel Savara (original)
 */
public class TrackFireRadians extends AdvancedRobot {

	/**
	 * TrackFire's run method
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.pink);
		setGunColor(Color.pink);
		setRadarColor(Color.pink);
		setScanColor(Color.pink);
		setBulletColor(Color.pink);

		// Loop forever
		while (true) {
			turnGunRightRadians(0.1745); // Scans automatically
		}
	}

	/**
	 * onScannedRobot:  We have a target.  Go get it.
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Calculate exact location of the robot
		double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
		double bearingFromGun = normalRelativeAngle(absoluteBearing - getGunHeadingRadians());

		// If it's close enough, fire!
		if (Math.abs(bearingFromGun) <= 3) {
			turnGunRightRadians(bearingFromGun);
			// We check gun heat here, because calling fire()
			// uses a turn, which could cause us to lose track
			// of the other robot.
			if (getGunHeat() == 0) {
				fire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
			}
		} // otherwise just set the gun to turn.
		// Note:  This will have no effect until we call scan()
		else {
			turnGunRightRadians(bearingFromGun);
		}
		// Generates another scan event if we see a robot.
		// We only need to call this if the gun (and therefore radar)
		// are not turning.  Otherwise, scan is called automatically.
		if (bearingFromGun == 0) {
			scan();
		}
	}
}
