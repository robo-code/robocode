/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
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


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import robocode.control.events.RoundStartedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * Repeatable robot possition test
 *
 * @author Pavel Savara (original)
 */
public class TestPosition extends RobocodeTestBed {
	int lastTurn;

	@Test
	public void run() {
		super.run();
	}

	public String getRobotNames() {
		return "sample.Crazy,sample.Target";
	}

	@Override
	public void onRoundStarted(final RoundStartedEvent event) {
		super.onRoundStarted(event);
		if (event.getRound() == 0) {
			IRobotSnapshot crazy = event.getStartSnapshot().getRobots()[0];
			IRobotSnapshot target = event.getStartSnapshot().getRobots()[1];

			Assert.assertNear(641.2890398, crazy.getX());
			Assert.assertNear(356.1502740, crazy.getY());
			Assert.assertNear(121.1808112, target.getX());
			Assert.assertNear(448.6502857, target.getY());
		}
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		lastTurn = event.getTurnSnapshot().getTurn();

		// System.out.println(event.getTurnSnapshot().getTurn());
		IRobotSnapshot crazy = event.getTurnSnapshot().getRobots()[0];
		IRobotSnapshot target = event.getTurnSnapshot().getRobots()[1];

		if (lastTurn == 1) {
			Assert.assertNear(642.1438774, crazy.getX());
			Assert.assertNear(355.6313784, crazy.getY());
			Assert.assertNear(121.1808112, target.getX());
			Assert.assertNear(448.6502857, target.getY());
		}

		if (lastTurn == 1700) {
			Assert.assertNear(373.9958377, crazy.getX());
			Assert.assertNear(196.1380677, crazy.getY());
			Assert.assertNear(340.3212984, target.getX());
			Assert.assertNear(456.5502002, target.getY());
		}
	}

	@Override
	protected void runTeardown() {
		Assert.assertThat(lastTurn, is(1342));
	}
}
