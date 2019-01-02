/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robotscs;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
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
		return "SampleCs.Crazy,SampleCs.Target";
	}

	@Override
	public void onRoundStarted(final RoundStartedEvent event) {
		super.onRoundStarted(event);
		if (event.getRound() == 0) {
			IRobotSnapshot crazy = event.getStartSnapshot().getRobots()[0];
			IRobotSnapshot target = event.getStartSnapshot().getRobots()[1];

			Assert.assertNear(568.1445492, crazy.getX());
			Assert.assertNear(163.0032275, crazy.getY());
			Assert.assertNear(436.7181397, target.getX());
			Assert.assertNear(351.5039067, target.getY());
		}
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		lastTurn = event.getTurnSnapshot().getTurn();

		IRobotSnapshot crazy = event.getTurnSnapshot().getRobots()[0];
		IRobotSnapshot target = event.getTurnSnapshot().getRobots()[1];

		if (lastTurn == 1) {
			Assert.assertNear(567.2831835, crazy.getX());
			Assert.assertNear(162.4952421, crazy.getY());
			Assert.assertNear(436.7181397, target.getX());
			Assert.assertNear(351.5039067, target.getY());
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
		Assert.assertTrue(lastTurn == 2080 || lastTurn == 2296);
	}
}
