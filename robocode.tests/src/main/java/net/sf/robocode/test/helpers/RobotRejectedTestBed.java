/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.helpers;


import org.junit.Test;


/**
 * Test bed for robots that must be rejected when loaded, because they use something that robots are not allowed to
 * use, e.g. sockets, process execution, or reflection. Only the enemy robot is loaded into the battle.
 *
 * @author Flemming N. Larsen (original)
 */
public abstract class RobotRejectedTestBed extends RobocodeTestBed {

	@Test
	public void run() {
		super.run();
	}

	@Override
	public int getExpectedRobotCount(String list) {
		return 1;
	}

	@Override
	protected int getExpectedErrors() {
		// At least one error, as the robot cannot be loaded. Depending on whether the robot was already registered as
		// a valid robot in the robot database, it is rejected when the repository is updated, or when the battle starts.
		return Math.max(1, errors);
	}

	@Override
	protected void runTeardown() {
		// The error is e.g. "Can't load 'x.Robot' because it is an invalid robot or team." or "Could not load robot: x.Robot"
		Assert.assertTrue("Robot must be rejected: " + getRobotName() + "\n" + errorText,
				errorText.toString().contains(getRobotName()));
	}
}
