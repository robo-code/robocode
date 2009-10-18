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


/**
 * @author Flemming N. Larsen (original)
 */
public class GunTurnRate extends robocode.AdvancedRobot {
	public void run() {
		setMaxTurnRate(5);

		setTurnGunLeft(Double.POSITIVE_INFINITY);
		executeAndDumpTurnRate();
		executeAndDumpTurnRate();

		setTurnGunRight(Double.POSITIVE_INFINITY);
		executeAndDumpTurnRate();
		executeAndDumpTurnRate();

		setTurnLeft(Double.POSITIVE_INFINITY);
		setTurnGunLeft(Double.POSITIVE_INFINITY);
		executeAndDumpTurnRate();
		executeAndDumpTurnRate();

		setTurnRight(Double.POSITIVE_INFINITY);
		setTurnGunRight(Double.POSITIVE_INFINITY);
		executeAndDumpTurnRate();
		executeAndDumpTurnRate();
		
		setMaxTurnRate(20);

		setTurnLeft(Double.POSITIVE_INFINITY);
		setTurnGunLeft(Double.POSITIVE_INFINITY);
		executeAndDumpTurnRate();
		executeAndDumpTurnRate();

		setTurnRight(Double.POSITIVE_INFINITY);
		setTurnGunRight(Double.POSITIVE_INFINITY);
		executeAndDumpTurnRate();
		executeAndDumpTurnRate();
		
		setAdjustGunForRobotTurn(false);

		setTurnRight(Double.POSITIVE_INFINITY);
		setTurnGunLeft(Double.POSITIVE_INFINITY);
		executeAndDumpTurnRate();
		executeAndDumpTurnRate();

		setTurnLeft(Double.POSITIVE_INFINITY);
		setTurnGunRight(Double.POSITIVE_INFINITY);
		executeAndDumpTurnRate();
		executeAndDumpTurnRate();
	}

	private void executeAndDumpTurnRate() {
		double lastHeading = getGunHeading();

		execute();

		double turnRate = robocode.util.Utils.normalRelativeAngleDegrees(getGunHeading() - lastHeading);

		lastHeading = getGunHeading();

		out.println(getTime() + ": " + turnRate);
	}
}
