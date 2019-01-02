/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.RateControlRobot;


/**
 * @author Joshua Galecki (original)
 */
public class RateControl extends RateControlRobot {
	@Override
	public void run() {
		for (int turnNumber = 0; turnNumber < 80; turnNumber++) {
			if (turnNumber == 0) {
				setTurnRate(4.5);
			} else if (turnNumber == 10) {
				setTurnRate(-9);
			} else if (turnNumber == 20) {
				setTurnRate(0);
				setVelocityRate(2);
			} else if (turnNumber == 25) {
				setVelocityRate(-8);
			} else if (turnNumber == 35) {
				setVelocityRate(0);
				setGunRotationRate(9);
			} else if (turnNumber == 45) {
				setGunRotationRate(-4.5);
			} else if (turnNumber == 55) {
				setGunRotationRate(0);
				setRadarRotationRate(9);
			} else if (turnNumber == 65) {
				setRadarRotationRate(-4.5);
			} else if (turnNumber == 75) {
				setRadarRotationRate(0);				
			}
			execute();
		}
	}
}
