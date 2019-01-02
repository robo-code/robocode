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
 * This test robot is based on Eric Simonton's 3 caveats on the RoboWiki
 * where Robocode did not work as expected when updating the robot movements
 * with deceleration. 
 *
 * @author Flemming N. Larsen (original)
 */
public class DecelerationCaveat2 extends AdvancedRobot {

	@Override
	public void run() {
		setAhead(100);
		for (int i = 1; i <= 8; i++) {
			execute();
		}

		setAhead(22.5);
		for (int i = 1; i <= 6; i++) {
			execute();
		}
	}
}
