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
import helpers.Assert;
import org.junit.Test;
import org.junit.After;
import robocode.battle.events.TurnEndedEvent;
import robocode.battle.snapshot.RobotSnapshot;


/**
 * @author Pavel Savara (original)
 */
public class TestPrivateConstructor extends RobotTestBed {
	boolean messaged;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public int getExpectedRobotCount(String list) {
		return 2;
	}

	public void onTurnEnded(TurnEndedEvent event) {
		final RobotSnapshot robot = event.getTurnSnapshot().getRobots().get(1);

		if (robot.getOutputStreamSnapshot().contains("SYSTEM: Is your constructor marked public?")) {
			messaged = true;
		}
		Assert.assertNear(0, robot.getEnergy());
	}

	@Override
	public String getRobotNames() {
		return "sample.Fire,testing.PrivateConstructor";
	}

	@After
	public void tearDownPrivate() {
		Assert.assertTrue(messaged);
	}
}

