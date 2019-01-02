/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


/**
 * @author Flemming N. Larsen (original)
 */
public class RadarTurnRateAndSetAdjust extends robocode.AdvancedRobot {
	public void run() {
		// -- Turn 1 --
		setTurnRadarRight(1000);
		executeAndDumpTurnRate();
		// Expected turn rate: max. radar turn rate = 45

		// -- Turn 2 --
		setTurnGunRight(1000);
		executeAndDumpTurnRate();
		// Expected turn rate: max. gun + radar turn rate = 20 + 45 = 65

		// -- Turn 3 --
		setTurnRight(1000);
		executeAndDumpTurnRate();
		// Expected turn rate: max. robot + gun + radar turn rate = 10 + 20 + 45 = 75
								
		// -- Turn 4 --
		setTurnRadarLeft(1000);
		executeAndDumpTurnRate();
		// Expected turn rate: max. robot + gun - radar turn rate = 10 + 20 - 45 = -15

		// -- Turn 5 --
		setTurnGunLeft(1000);
		executeAndDumpTurnRate();
		// Expected turn rate: max. robot + gun - radar turn rate = 10 - 20 - 45 = -55

		// -- Turn 6 --
		setTurnLeft(1000);
		executeAndDumpTurnRate();
		// Expected turn rate: max. robot + gun - radar turn rate = -10 - 20 - 45 = -75

		// -- Turn 7 --
		setAdjustRadarForGunTurn(false);
		setTurnRight(14);
		setTurnGunRight(15);
		setTurnRadarRight(7);
		executeAndDumpTurnRate();
		// Expected turn rate: robot + gun + radar turn rate = 14 + 15 + 7 = 32
		
		// -- Turn 8 --
		setAdjustGunForRobotTurn(false);
		setAdjustRadarForRobotTurn(false);
		setAdjustRadarForGunTurn(true);
		setTurnRight(14);
		setTurnGunLeft(15);
		setTurnRadarRight(7);
		executeAndDumpTurnRate();
		// Expected turn rate: robot (max) + radar turn rate (ignoring gun turn rate, but not robot turn rate) = 10 + 7 = 17

		// -- Turn 9 --
		setAdjustGunForRobotTurn(false);
		setAdjustRadarForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setTurnRight(14);
		setTurnGunLeft(15);
		setTurnRadarRight(35);
		executeAndDumpTurnRate();
		// Expected turn rate: robot turn rate (ignoring both gun and body turn rate) = 35

		// -- Turn 10 --
		setAdjustGunForRobotTurn(false);
		setAdjustRadarForRobotTurn(false);
		setAdjustRadarForGunTurn(true);
		setTurnRight(14);
		setTurnGunLeft(15);
		setTurnRadarLeft(7);
		executeAndDumpTurnRate();
		// Expected turn rate: robot (max) + radar turn rate (ignoring gun turn rate, but not robot turn rate) = 10 - 7 = 3

		// -- Turn 11 --
		setAdjustGunForRobotTurn(false);
		setAdjustRadarForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setTurnRight(4);
		setTurnGunRight(60);
		setTurnRadarLeft(100);
		executeAndDumpTurnRate();
		// Expected turn rate: robot (max) turn rate (ignoring both gun and body turn rate) = -20

		// -- Turn 12 --
		setAdjustGunForRobotTurn(false);
		setAdjustRadarForRobotTurn(false);
		setAdjustRadarForGunTurn(true);
		setTurnRight(Double.POSITIVE_INFINITY);
		setTurnGunRight(Double.POSITIVE_INFINITY);
		setTurnRadarRight(Double.POSITIVE_INFINITY);
		executeAndDumpTurnRate();
		// Expected turn rate: setAdjusts are all ignored, max. robot + gun + radar turn rate = 10 + 20 + 45 = 75

		// -- Turn 13 --
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForRobotTurn(false);
		setAdjustRadarForGunTurn(true);
		setTurnRight(Double.NEGATIVE_INFINITY);
		setTurnGunRight(Double.NEGATIVE_INFINITY);
		setTurnRadarRight(Double.NEGATIVE_INFINITY);
		executeAndDumpTurnRate();
		// Expected turn rate: setAdjusts are all ignored, max. robot + gun + radar turn rate = -10 - 20 - 45 = -75

		// -- Turn 14 --
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setTurnLeft(Double.NEGATIVE_INFINITY);
		setTurnGunLeft(Double.NEGATIVE_INFINITY);
		setTurnRadarLeft(Double.POSITIVE_INFINITY);
		executeAndDumpTurnRate();
		// Expected turn rate: setAdjusts are all ignored, max. robot + gun + radar turn rate = -(-10) - (-20) - 45 = -15
	}

	private void executeAndDumpTurnRate() {
		double lastHeading = getRadarHeading();

		execute();

		double turnRate = robocode.util.Utils.normalRelativeAngleDegrees(getRadarHeading() - lastHeading);

		out.println(getTime() + ": " + turnRate);
	}
}
