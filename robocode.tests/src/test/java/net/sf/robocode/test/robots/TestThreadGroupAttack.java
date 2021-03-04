/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
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


/**
 * This test was made due to hacker.Destroyer 1.3, which proved a security breach in
 * Robocode 1.7.2.1 Beta. The security breach was reported with:
 * Bug [3021140] Possible for robot to kill other robot threads.
 *
 * The security manager of Robocode must make sure that unsafe (robot) threads cannot
 * access thread groups other than its own thread group within checkAccess(Thread).
 *
 * @author Flemming N. Larsen (original)
 */
public class TestThreadGroupAttack extends RobocodeTestBed {
	boolean messagedInterrupted;
	boolean messagedPreventing;

	@Test
	public void run() { super.run(); }

	@Override
	public String getRobotName() {
		return "tested.robots.ThreadGroupAttack";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final String out = event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot();

		if (out.contains("Interrupted: sample.Target")) {
			messagedInterrupted = true;
		}

		if (out.contains("Preventing tested.robots.ThreadGroupAttack from access to sample.Target")) {
			messagedPreventing = true;
		}
	}

	@Override
	protected void runTeardown() {
		Assert.assertFalse(messagedInterrupted);
		Assert.assertTrue(messagedPreventing);
	}

	@Override
	protected int getExpectedErrors() {
		return 3; // Security error must be reported as an error
	}
}
