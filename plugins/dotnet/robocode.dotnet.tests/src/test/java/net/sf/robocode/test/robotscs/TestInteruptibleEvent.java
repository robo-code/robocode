/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robotscs;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Test;
import robocode.control.events.TurnEndedEvent;


/**
 * Test if onScannedRobot() is called when the radar is turned from within a onHitWall() event.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestInteruptibleEvent extends RobocodeTestBed {
	boolean messagedScanned;

	@Test
	public void run() {
		super.run();
	}

	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final String out = event.getTurnSnapshot().getRobots()[1].getOutputStreamSnapshot();

		if (out.contains("Scanned!!!")) {
			messagedScanned = true;
		}
	}

	@Override
	public String getRobotNames() {
		return "tested.robotscs.InteruptibleEvent,tested.robotscs.InteruptibleEvent";
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(messagedScanned);
	}
}
