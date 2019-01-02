/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Test;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * @author Pavel Savara (original)
 */
public class TestBulletPower extends RobocodeTestBed {
	@Test
	public void run() {
		super.run();
	}

	public String getRobotNames() {
		return "sample.Target,tested.robots.BadFirePower";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		IRobotSnapshot bp = event.getTurnSnapshot().getRobots()[1];

		final int time = event.getTurnSnapshot().getTurn();

		switch (time) {
		case 30:
		case 46:
		case 62:
		case 78:
		case 94:
		case 110:
			test(bp, "Bullet power: 3.0");
			break;

		default:
			if (time > 1 && time < 115) {
				test(bp, "No bullet");
			}
			break;
		}
	}

	private void test(IRobotSnapshot gh, String s) {
		Assert.assertTrue(gh.getOutputStreamSnapshot() + " expected " + s, gh.getOutputStreamSnapshot().contains(s));
	}
}
