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
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobotTestBed;

import robocode.control.events.TurnEndedEvent;
import robocode.util.Utils;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestReverseDirection extends RobotTestBed {

	@Override
	public String getRobotNames() {
		return "tested.robots.ReverseDirection,sample.Target";        
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
		case 1:
			Assert.assertTrue(Utils.isNear(velocity, 1.0));
			break;

		case 2:
			Assert.assertTrue(Utils.isNear(velocity, -0.5));
			break;
		}
	}
}
