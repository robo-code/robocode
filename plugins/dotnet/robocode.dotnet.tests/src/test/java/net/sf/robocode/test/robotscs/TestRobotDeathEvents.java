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
package net.sf.robocode.test.robotscs;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;

import robocode.control.events.TurnEndedEvent;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestRobotDeathEvents extends RobocodeTestBed {

	@Override
	public String getRobotNames() {
		return "tested.robotscs.RobotDeathEvents,SampleCs.Crazy,SampleCs.Target,SampleCs.Target,SampleCs.Target";
	}

	@Override
	public int getNumRounds() {
		return 5;
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		final String out = event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot();

		if (out.contains("enemyCount != getOthers()")) {
			Assert.fail("Robot is missing RobotDeathEvent");
		}	
	}
}
