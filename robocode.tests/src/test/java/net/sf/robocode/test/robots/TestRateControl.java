/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import static robocode.util.Utils.normalRelativeAngle;

import org.junit.Test;

import robocode.control.events.RoundStartedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;

import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;


/**
 * @author Joshua Galecki (original)
 */
public class TestRateControl extends RobocodeTestBed {
	int turnNumber;
	double originalHeading;
	double originalX;
	double originalGunHeading;
	double originalRadarHeading;

	@Test
	public void run() {
		super.run();
	}
	
	@Override
	public String getRobotNames() {
		return "tested.robots.RateControl,sample.Target";
	}
	
	@Override
	public String getInitialPositions() {
		return "(320,220,0), (50,50,0)"; // Make sure the robot doesn't start too near to a wall
	}

	@Override
	public void onRoundStarted(final RoundStartedEvent event) {
		super.onRoundStarted(event);
		IRobotSnapshot rate = event.getStartSnapshot().getRobots()[0]; 

		originalHeading = rate.getBodyHeading();
		originalX = rate.getX();
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		turnNumber = event.getTurnSnapshot().getTurn();
		
		IRobotSnapshot rate = event.getTurnSnapshot().getRobots()[0];
		
		// Test turnRate
		if (turnNumber == 10) {
			Assert.assertNear(Math.PI / 4, normalRelativeAngle(rate.getBodyHeading() - originalHeading));
		}
		if (turnNumber == 20) {
			Assert.assertNear(-Math.PI / 4, normalRelativeAngle(rate.getBodyHeading() - originalHeading));
		}
		if (turnNumber == 25) {
			// Test that turnRate was set to zero
			Assert.assertNear(-Math.PI / 4, normalRelativeAngle(rate.getBodyHeading() - originalHeading));
			
			// Test velocityRate
			double theta = rate.getBodyHeading();
			double deltaX = rate.getX() - originalX;
			double distanceTravelled = deltaX / Math.sin(theta);

			Assert.assertNear(9, distanceTravelled);
		}
		if (turnNumber == 35) {
			double theta = rate.getBodyHeading();
			double deltaX = rate.getX() - originalX;
			double distanceTravelled = deltaX / Math.sin(theta);

			Assert.assertNear(-35, distanceTravelled);
			originalGunHeading = rate.getGunHeading();
		}
		if (turnNumber == 45) {
			// Test that velocityRate was set to zero
			double theta = rate.getBodyHeading();
			double deltaX = rate.getX() - originalX;
			double distanceTravelled = deltaX / Math.sin(theta);

			Assert.assertNear(-47, distanceTravelled);

			// test gunRotationRate
			Assert.assertNear(Math.PI / 2, normalRelativeAngle(rate.getGunHeading() - originalGunHeading));
		}
		if (turnNumber == 55) {
			Assert.assertNear(Math.PI / 4, normalRelativeAngle(rate.getGunHeading() - originalGunHeading));
			originalRadarHeading = rate.getRadarHeading();
		}
		
		if (turnNumber == 65) {
			// test that gunRotationRate was set to zero
			Assert.assertNear(Math.PI / 4, normalRelativeAngle(rate.getGunHeading() - originalGunHeading));	

			// test radarRotationRate
			Assert.assertNear(Math.PI / 2, normalRelativeAngle(rate.getRadarHeading() - originalRadarHeading));
		}
		if (turnNumber == 75) {
			Assert.assertNear(Math.PI / 4, normalRelativeAngle(rate.getRadarHeading() - originalRadarHeading));
		}
		if (turnNumber == 76) {
			// test that radarRotationRate was set to zero
			Assert.assertNear(Math.PI / 4, normalRelativeAngle(rate.getRadarHeading() - originalRadarHeading));		
		}	
	}
}
