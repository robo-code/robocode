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
import robocode.battle.snapshot.RobotSnapshot;
import robocode.battle.events.TurnEndedEvent;
import org.junit.Test;


/**
 * @author Pavel Savara (original)
 */
public class TestAcceleration extends RobotTestBed {
	@Test
	public void run() {
		super.run();
	}

	public String getRobotNames() {
		return "sample.Target,testing.Ahead";        
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
        super.onTurnEnded(event);
		RobotSnapshot ahead = event.getTurnSnapshot().getRobots().get(1);

		switch (event.getTurnSnapshot().getTurn()) {
		case 1:
			Assert.assertNear(1.0, ahead.getVelocity());
			break;

		case 2:
			Assert.assertNear(2.0, ahead.getVelocity());
			break;

		case 3:
			Assert.assertNear(3.0, ahead.getVelocity());
			break;

		case 4:
			Assert.assertNear(4.0, ahead.getVelocity());
			break;

		case 5:
			Assert.assertNear(5.0, ahead.getVelocity());
			break;

		case 6:
			Assert.assertNear(6.0, ahead.getVelocity());
			break;

		case 7:
			Assert.assertNear(7.0, ahead.getVelocity());
			break;

		case 8:
			Assert.assertNear(8.0, ahead.getVelocity());
			break;

		case 9:
			Assert.assertNear(8.0, ahead.getVelocity());
			break;

		case 14:
			Assert.assertNear(8.0, ahead.getVelocity());
			break;

		case 15:
			Assert.assertNear(7.0, ahead.getVelocity());
			break;

		case 16:
			Assert.assertNear(5.0, ahead.getVelocity());
			break;

		case 17:
			Assert.assertNear(3.0, ahead.getVelocity());
			break;

		case 18:
			Assert.assertNear(1.0, ahead.getVelocity());
			break;

		case 19:
			Assert.assertNear(0.0, ahead.getVelocity());
			break;

		default:
			break;
		}
	}
}

