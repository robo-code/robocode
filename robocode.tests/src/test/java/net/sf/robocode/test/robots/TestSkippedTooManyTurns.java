/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
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
import robocode.control.snapshot.RobotState;


/**
 * Tests that a robot that skips too many turns is stopped without errors, as its thread cannot stop itself.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestSkippedTooManyTurns extends RobocodeTestBed {
	private boolean messagedNoActions;
	private boolean died;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.SkipTooManyTurns";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final IRobotSnapshot robot = event.getTurnSnapshot().getRobots()[0];

		if (robot.getOutputStreamSnapshot().contains("has not performed any actions in a reasonable amount of time")) {
			messagedNoActions = true;
		}
		if (robot.getState() == RobotState.DEAD) {
			died = true;
		}
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue("The robot was not told that it has skipped too many turns", messagedNoActions);
		Assert.assertTrue("The robot was not killed for skipping too many turns", died);
	}
}
