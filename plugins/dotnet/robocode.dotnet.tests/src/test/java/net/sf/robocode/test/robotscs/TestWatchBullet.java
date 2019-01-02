/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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

		test(out, time, 33, "33 487.43878 119.51132 347.66745 True");
		test(out, time, 45, "45 459.24551 248.46534 347.66745 True");
		test(out, time, 52, "52 442.79943 323.68851 347.66745 True");
		test(out, time, 53, "53 440.44999 334.43468 347.66745 False");

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
