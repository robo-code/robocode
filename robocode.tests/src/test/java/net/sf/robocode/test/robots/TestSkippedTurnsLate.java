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
 * Tests that a robot running out of time gets SkippedTurnEvents for exactly the turns it skipped.
 *
 * @author Julian Kent (original)
 */
public class TestSkippedTurnsLate extends RobocodeTestBed {
	private int lateTurns;
	private int missedWakeups;
	private final StringBuilder wrongSkips = new StringBuilder();

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.SkipTurnsLate";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		for (String line : event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot().split("\n")) {
			if (line.startsWith("Late turn")) {
				lateTurns++;
			} else if (line.startsWith("Missed wake-up")) {
				missedWakeups++;
			} else if (line.startsWith("Wrong skipped turns")) {
				wrongSkips.append(line).append('\n');
			}
		}
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(wrongSkips.length() == 0, "The skipped turns were wrong:\n" + wrongSkips);
		Assert.assertTrue(lateTurns > 0, "A late turn was not skipped when the robot was asleep before the next turn");
		Assert.assertTrue(missedWakeups > 0, "The robot never missed a wake-up");
	}
}
