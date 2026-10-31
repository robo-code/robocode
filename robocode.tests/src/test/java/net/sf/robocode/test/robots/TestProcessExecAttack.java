/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobotRejectedTestBed;
import robocode.control.events.TurnEndedEvent;
import tested.robots.ProcessExecAttack;


/**
 * Tests that a robot cannot execute host commands.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestProcessExecAttack extends RobotRejectedTestBed {
	private boolean executed;

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		for (robocode.control.snapshot.IRobotSnapshot robot : event.getTurnSnapshot().getRobots()) {
			String out = robot.getOutputStreamSnapshot();

			if (out != null && out.contains(ProcessExecAttack.EXECUTED_MESSAGE)) {
				executed = true;
			}
		}
	}

	@Override
	public String getRobotName() {
		return "tested.robots.ProcessExecAttack";
	}

	@Override
	protected void runTeardown() {
		super.runTeardown();
		Assert.assertFalse("Robot executed a host command", executed);
	}
}
