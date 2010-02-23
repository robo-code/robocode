/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.test.robotscs;


import junit.framework.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
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
		final IRobotSnapshot robot = event.getTurnSnapshot().getRobots()[0];

		final String out = robot.getOutputStreamSnapshot();

		if (event.getTurnSnapshot().getTurn() == 589) {
			// if (out.contains("robocode.BulletMissedEvent")) {

			test(out, "Robocode.BulletMissedEvent 5");
			test(out, "Robocode.ScannedRobotEvent 100");
			test(out, "Robocode.BulletHitEvent 24");
			test(out, "Robocode.StatusEvent 589");
			test(out, "Robocode.WinEvent 1");
			test(out, "last bullet heading 6.0710");
		}
	}

	private void test(String out, String in) {
		Assert.assertTrue(in, out.contains(in));
	}
}
