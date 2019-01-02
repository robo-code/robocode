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
 * Repeatable random test
 *
 * @author Pavel Savara (original)
 */
public class TestRandom extends RobocodeTestBed {
	@Test
	public void run() {
		super.run();
	}

	public String getRobotNames() {
		return "sample.Fire,tested.robots.Random";
	}

	@Override
	public boolean isCheckOnBattleStart() {
		return true;
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		Assert.assertTrue(event.getTurnSnapshot().getTurn() <= 1407);
		IRobotSnapshot fire = event.getTurnSnapshot().getRobots()[0];
		IRobotSnapshot random = event.getTurnSnapshot().getRobots()[1];

		if (event.getTurnSnapshot().getTurn() == 1241) {
			Assert.assertNear(213.18621928, fire.getX());
			Assert.assertNear(371.45706118, fire.getY());
			Assert.assertNear(782.0, random.getX());
			Assert.assertNear(230.95479253, random.getY());
		}
	}
}
