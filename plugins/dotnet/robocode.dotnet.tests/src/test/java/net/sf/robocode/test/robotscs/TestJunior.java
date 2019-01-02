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
public class TestJunior extends RobocodeTestBed {
	@Test
	public void run() {
		super.run();
	}

	public String getRobotNames() {
		return "tested.robotscs.JuniorEvents,SampleCs.SittingDuck";
	}

	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		IRobotSnapshot robot = event.getTurnSnapshot().getRobots()[0];

		if (event.getTurnSnapshot().getTurn() == 590) {
			String out = robot.getOutputStreamSnapshot();

			test(out, "Robocode.BulletMissedEvent 5");
			test(out, "Robocode.ScannedRobotEvent 100");
			test(out, "Robocode.BulletHitEvent 24");
			test(out, "Robocode.StatusEvent 590");
			test(out, "Robocode.WinEvent 1");
			test(out, "last bullet heading 6.06794");
		}
	}

	private void test(String out, String in) {
		Assert.assertTrue(in, out.contains(in));
	}
}
