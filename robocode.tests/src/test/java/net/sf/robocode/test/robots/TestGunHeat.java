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
public class TestGunHeat extends RobocodeTestBed {
	@Test
	public void run() {
		super.run();
	}

	public String getRobotNames() {
		return "sample.Target,tested.robots.GunHeat";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		IRobotSnapshot gh = event.getTurnSnapshot().getRobots()[1];

		switch (event.getTurnSnapshot().getTurn()) {
		case 1:
			test(gh, "3.0");
			break;

		case 2:
			test(gh, "2.8");
			break;

		case 3:
			test(gh, "2.699999");
			break;

		case 4:
			test(gh, "2.599999");
			break;

		case 9:
			test(gh, "2.09999999");
			break;

		case 20:
			test(gh, "0.9999999");
			break;

		case 28:
			test(gh, "0.19999999");
			break;

		case 29:
			test(gh, "0.09999999");
			break;

		case 30:
			test(gh, "1.6");
			break;

		case 570:
			test(gh, "2.77555");
			break;

		case 571:
			test(gh, "0.0");
			break;

		default:
			break;
		}
	}

	private void test(IRobotSnapshot gh, String s) {
		Assert.assertTrue(gh.getOutputStreamSnapshot() + " expected " + s,
				gh.getOutputStreamSnapshot().contains("after fire: " + s));
	}
}
