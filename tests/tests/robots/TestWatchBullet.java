/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robots;


import helpers.RobotTestBed;
import org.junit.Test;
import robocode.battle.events.TurnEndedEvent;
import robocode.battle.snapshot.RobotSnapshot;
import junit.framework.Assert;


/**
 * @author Pavel Savara (original)
 */
public class TestWatchBullet extends RobotTestBed {
	boolean messagedIdentity;

	@Test
	public void run() {
		super.run();
	}

	public String getRobotNames() {
		return "testing.WatchBullets,sample.SittingDuck";
	}

	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final RobotSnapshot robot = event.getTurnSnapshot().getRobots().get(0);

		final String out = robot.getOutputStreamSnapshot();
		final int time = event.getTurnSnapshot().getTurn();

		test(out, time, 32, "485.6582012541023 121.60159872168872 347.8464407127698 true");
		test(out, time, 44, "457.86791268289903 250.6430643873004 347.8464407127698 true");
		test(out, time, 51, "441.6569110163638 325.91725269224054 347.8464407127698 true");
		test(out, time, 52, "439.34105363543017 336.67070816437484 347.8464407127698 false");

		if (out.contains("Failed bullet identity")) {
			messagedIdentity = true;
		}
	}

	@Override
	protected void runTeardown() {
		helpers.Assert.assertFalse(messagedIdentity);
	}

	private void test(String out, int timecurr, int timeWatch, String in) {
		if (timecurr == timeWatch) {
			Assert.assertTrue("Expected: " + in + " was: " + out, out.contains(in));
		}
	}
}
