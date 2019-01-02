/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;

import robocode.control.events.TurnEndedEvent;
import robocode.util.Utils;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestMaxVelocity extends RobocodeTestBed {

	@Override
	public String getRobotNames() {
		return "tested.robots.MaxVelocity,sample.Target";        
	}

	@Override
	public String getInitialPositions() {
		return "(50,50,0), (600,50,0)"; // Make sure the robots do not collide!
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		final int time = (int) event.getTurnSnapshot().getTurn();
		final double velocity = event.getTurnSnapshot().getRobots()[0].getVelocity();

		switch (time) {
		case 10:
		case 20:
		case 100:
			Assert.assertTrue(Utils.isNear(velocity, 8));
			break;

		case 32:
		case 40:
		case 120:
			Assert.assertTrue(Utils.isNear(velocity, -8));
			break;

		case 50:
		case 60:
			Assert.assertTrue(Utils.isNear(velocity, 4.2));
			break;

		case 70:
		case 80:
			Assert.assertTrue(Utils.isNear(velocity, -4.2));
			break;

		case 130:
		case 140:
			Assert.assertTrue(Utils.isNear(velocity, 2));
			break;

		case 150:
		case 160:
			Assert.assertTrue(Utils.isNear(velocity, -2));
			break;

		case 170:
		case 180:
		case 190:
		case 199:
			Assert.assertTrue(Utils.isNear(velocity, 0));
			break;
		}
	}
}
