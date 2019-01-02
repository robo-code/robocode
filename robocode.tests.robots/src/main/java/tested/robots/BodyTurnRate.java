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
public class BodyTurnRate extends robocode.AdvancedRobot {

	public void run() {
		// Test turn rate at all speed moving ahead and turning left
		
		setAhead(1000);
		setTurnLeft(360);
		executeAndDumpTurnRate9turns();

		stopMoving();

		// Test turn rate at all speed moving ahead and turning right

		setAhead(1000);
		setTurnRight(360);
		executeAndDumpTurnRate9turns();

		stopMoving();

		// Test turn rate at all speed moving back and turning right

		setBack(1000);
		setTurnRight(360);
		executeAndDumpTurnRate9turns();

		stopMoving();

		// Test turn rate at all speed moving back and turning left

		setBack(1000);
		setTurnLeft(360);
		executeAndDumpTurnRate9turns();

		stopMoving();
	}

	private void executeAndDumpTurnRate9turns() {
		for (int i = 0; i < 9; i++) {
			executeAndDumpTurnRate();
		}
	}

	private void executeAndDumpTurnRate() {
		double lastHeading = getHeading();
		double lastVelocity = getVelocity();

		execute();

		double turnRate = robocode.util.Utils.normalRelativeAngleDegrees(getHeading() - lastHeading);

		out.println(getTime() + ": " + lastVelocity + ", " + turnRate);
	}

	private void stopMoving() {
		setAhead(0);
		setTurnLeft(0);
		
		for (int i = 0; i < 6; i++) {
			executeAndDumpTurnRate();
		}
	}
}
