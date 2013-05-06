/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package net.sf.robocode.test.robotscs;


import net.sf.robocode.test.helpers.RobocodeTestBed;

import org.junit.Assert;
import org.junit.Test;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * @author Pavel Savara (original)
 */
public class TestWatchBullet extends RobocodeTestBed {
	boolean messagedIdentity;

	@Test
	public void run() {
		super.run();
	}

	public String getRobotNames() {
		return "tested.robotscs.WatchBullets,SampleCs.SittingDuck";
	}

	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final IRobotSnapshot robot = event.getTurnSnapshot().getRobots()[0];

		final String out = robot.getOutputStreamSnapshot();
		final int time = event.getTurnSnapshot().getTurn();

		test(out, time, 33, "33 485.65820 121.60160 347.84644 True");
		test(out, time, 45, "45 457.86791 250.64306 347.84644 True");
		test(out, time, 52, "52 441.65691 325.91725 347.84644 True");
		test(out, time, 53, "53 439.34105 336.67071 347.84644 False");

		if (out.contains("Failed bullet identity")) {
			messagedIdentity = true;
		}
	}

	@Override
	protected void runTeardown() {
		net.sf.robocode.test.helpers.Assert.assertFalse(messagedIdentity);
	}

	private void test(String out, int timecurr, int timeWatch, String in) {
		if (timecurr == timeWatch) {
			Assert.assertTrue("got: " + out + ", expected: " + in, out.contains(in));
		}
	}
}
