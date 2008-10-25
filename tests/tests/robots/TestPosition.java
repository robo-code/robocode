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


import robocode.battle.snapshot.RobotSnapshot;
import robocode.battle.events.TurnEndedEvent;
import helpers.RobotTestBed;
import helpers.Assert;
import org.junit.Test;


/**
 * Repeatable robot possition test
 *
 * @author Pavel Savara (original)
 */
public class TestPosition extends RobotTestBed {

	@Test
	public void run() {
		super.run();
	}

	public String getRobotNames() {
		return "sample.Crazy,sample.Target";
	}

	public void onTurnEnded(TurnEndedEvent event) {
		Assert.assertTrue(event.getTurnSnapshot().getTurn() <= 2572);
		RobotSnapshot crazy = event.getTurnSnapshot().getRobots().get(0);
		RobotSnapshot target = event.getTurnSnapshot().getRobots().get(1);

		if (event.getTurnSnapshot().getTurn() == 2572) {
			Assert.assertNear(280.5541067939999, crazy.getX());
			Assert.assertNear(467.00715337600445, crazy.getY());
			Assert.assertNear(495.85159572106136, target.getX());
			Assert.assertNear(288.2519884518413, target.getY());
		}
	}
}
