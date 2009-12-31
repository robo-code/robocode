/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.test.robots;


import junit.framework.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
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
		return "tested.robots.WatchBullets,sample.SittingDuck";
	}

	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final IRobotSnapshot robot = event.getTurnSnapshot().getRobots()[0];

		final String out = robot.getOutputStreamSnapshot();
		final int time = event.getTurnSnapshot().getTurn();

		test(out, time, 100, "100 93.36993230503418 517.0756615447694 286.3676624291699 true");
		test(out, time, 105, "105 15.09577408618466 540.0650026159357 286.3676624291699 true");
		test(out, time, 106, "-0.5590575575852448 544.6628708301689 286.3676624291699 false");
		test(out, time, 110, "-0.5590575575852448 544.6628708301689 286.3676624291699 false");

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
