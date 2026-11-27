/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.jupiter.api.Test;
import robocode.control.events.TurnEndedEvent;


/**
 * Tests that a robot starting too slowly skips turns in later rounds too, not only in the first round.
 *
 * @author Julian Kent (original)
 */
public class TestSkippedTurnsAtRoundStart extends RobocodeTestBed {
	private boolean skippedInSecondRound;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.SkipTurnsAtRoundStart";
	}

	@Override
	public int getNumRounds() {
		return 2;
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		if (event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot().contains(" in round 1")) {
			skippedInSecondRound = true;
		}
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(skippedInSecondRound, "The robot did not skip turns when starting too slowly in round 2");
	}
}
