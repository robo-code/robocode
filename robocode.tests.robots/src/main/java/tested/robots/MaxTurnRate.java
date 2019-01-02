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
public class MaxTurnRate extends robocode.AdvancedRobot {
	public void run() {

		setTurnLeft(Double.POSITIVE_INFINITY);
		
		for (int i = 0; i < 13; i++) {		
			setMaxTurnRate(i);
			executeAndDumpTurnRate();
		}

		setTurnRight(Double.POSITIVE_INFINITY);
		
		for (int i = 0; i < 13; i++) {		
			setMaxTurnRate(i);
			executeAndDumpTurnRate();
		}
	}

	private void executeAndDumpTurnRate() {
		double lastHeading = getHeading();

		execute();

		double turnRate = robocode.util.Utils.normalRelativeAngleDegrees(getHeading() - lastHeading);

		out.println(getTime() + ": " + getVelocity() + ", " + turnRate);
	}
}
