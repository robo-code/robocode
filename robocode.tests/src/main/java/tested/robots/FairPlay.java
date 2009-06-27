/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Nat Pavasant
 *     - Initial implementation
 *******************************************************************************/
package tested.robots;


import robocode.*;


/**
 * @author Nat Pavasant (original)
 */
public class FairPlay extends AdvancedRobot {
	public void run() {
		setTurnRadarRightRadians(1d / 0d);
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		setTurnRadarLeft(getRadarTurnRemaining());
		setTurnGunRightRadians(
				robocode.util.Utils.normalRelativeAngle(e.getBearingRadians() + getHeadingRadians() - getGunHeadingRadians()));
		setFire(3);
	}
}
