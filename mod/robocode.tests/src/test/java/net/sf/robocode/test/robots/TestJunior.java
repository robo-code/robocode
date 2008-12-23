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
package net.sf.robocode.test.robots;


import junit.framework.Assert;
import net.sf.robocode.test.helpers.RobotTestBed;
import org.junit.Test;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * @author Pavel Savara (original)
 */
public class TestJunior extends RobotTestBed {
	@Test
	public void run() {
		super.run();
	}

	public String getRobotNames() {
		return "tested.robots.JuniorEvents,sample.SittingDuck";
	}

	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final IRobotSnapshot robot = event.getTurnSnapshot().getRobots()[0];

		final String out = robot.getOutputStreamSnapshot();

		if (event.getTurnSnapshot().getTurn() == 589) {
			// if (out.contains("robocode.BulletMissedEvent")) {

			test(out, "robocode.BulletMissedEvent 5");
			test(out, "robocode.ScannedRobotEvent 100");
			test(out, "robocode.BulletHitEvent 24");
			test(out, "robocode.StatusEvent 589");
			test(out, "robocode.WinEvent 1");
			test(out, "last bullet heading 6.0710656");
		}
	}

	private void test(String out, String in) {
		Assert.assertTrue(in, out.contains(in));
	}
}
