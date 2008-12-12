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


import helpers.Assert;
import helpers.RobotTestBed;
import org.junit.Test;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * @author Pavel Savara (original)
 */
public class TestPrivateConstructor extends RobotTestBed {
	boolean messaged;

	@Test
	public void run() {
		super.run();
	}

	public void onTurnEnded(final TurnEndedEvent event) {
		super.onTurnEnded(event);                          
		final IRobotSnapshot robot = event.getTurnSnapshot().getRobots()[1];

		if (robot.getOutputStreamSnapshot().contains("SYSTEM: Is your constructor marked public?")) {
			messaged = true;
		}
		Assert.assertNear(0, robot.getEnergy());
	}

	@Override
	public String getRobotNames() {
		return "sample.Fire,testing.PrivateConstructor";
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(messaged);
	}
}

