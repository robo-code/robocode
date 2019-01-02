/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobocodeTestBed;

import org.junit.Assert;
import org.junit.Test;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * @author Pavel Savara (original)
 */
public class TestWatchBullet extends RobocodeTestBed {
	@Test
	public void run() {
		super.run();
	}

	public String getRobotNames() {
		return "tested.robots.WatchBullets,sample.SittingDuck";
	}

	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final IRobotSnapshot robot = event.getTurnSnapshot().getRobots()[0];

		final String out = robot.getOutputStreamSnapshot();
		final int time = event.getTurnSnapshot().getTurn();

		test(out, time, 33, "33 487.438779505497 119.51131582855926 347.66745091446046 true");
		test(out, time, 45, "45 459.24550664858225 248.46533618316306 347.66745091446046 true");
		test(out, time, 52, "52 442.799430815382 323.6885147233484 347.66745091446046 true");
		test(out, time, 53, "53 440.4499914106391 334.43468308623204 347.66745091446046 false");
	}

	private void test(String out, int timecurr, int timeWatch, String in) {
		if (timecurr == timeWatch) {
			Assert.assertTrue("got: " + out + ", expected: " + in, out.contains(in));
		}
	}
}
