/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;


import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.RateControlRobot;
import robocode.ScannedRobotEvent;


/**
 * VelociRobot - a sample robot that demonstrates rate-controlled movement patterns.
 * This robot uses the RateControlRobot class to alternate between forward and backward
 * movement while responding to events.
 * 
 * @author Joshua Galecki (original)
 */
public class VelociRobot extends RateControlRobot {

	int turnCounter;
	public void run() {

		turnCounter = 0;
		setGunRotationRate(15);
		
		while (true) {
			if (turnCounter % 64 == 0) {
				// Straighten out if we were hit by a bullet and are turning
				setTurnRate(0);
				// Go forward with a velocity of 4
				setVelocityRate(4);
			}
			if (turnCounter % 64 == 32) {
				// Go backwards, faster
				setVelocityRate(-6);
			}
			turnCounter++;
			execute();
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		fire(1);
	}

	public void onHitByBullet(HitByBulletEvent e) {
		// Turn to confuse the other robot
		setTurnRate(5);
	}
	
	public void onHitWall(HitWallEvent e) {
		// Move away from the wall
		setVelocityRate(-1 * getVelocityRate());
	}
}
