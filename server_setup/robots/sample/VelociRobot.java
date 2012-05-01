/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Joshua Galecki
 *     - Initial implementation
 *******************************************************************************/

package sample;


import robocode.HitByBulletEvent;
import robocode.HitObjectEvent;
import robocode.HitObstacleEvent;
import robocode.HitWallEvent;
import robocode.RateControlRobot;
import robocode.ScannedObjectEvent;
import robocode.ScannedRobotEvent;
import robocode.robotinterfaces.IObjectEvents;
import robocode.robotinterfaces.IObjectRobot;


/**
 * This is a sample of a robot using the RateControlRobot class
 * 
 * @author Joshua Galecki
 */
public class VelociRobot extends RateControlRobot implements IObjectEvents, IObjectRobot {

	int turnCounter;
	public void run() {

		turnCounter = 0;
		setGunRotationRate(15);
		
		while (true) {
			if (turnCounter % 64 == 0) {
				// Straighten out, if we were hit by a bullet and are turning
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

	public void onHitObject(HitObjectEvent e) {		setVelocityRate(-1 * getVelocityRate());}

	public void onHitObstacle(HitObstacleEvent e) {		setVelocityRate(-1 * getVelocityRate());}

	public void onScannedObject(ScannedObjectEvent e) {
		if (e.getObjectType().equals("flag")) {
			e.getBearing();
		}
	}
	
	public IObjectEvents getObjectEventListener() {
		return this;
	}
	
}
