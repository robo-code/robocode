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
import org.junit.Test;
import robocode.control.events.TurnEndedEvent;
import tested.robots.ReflectionOnOwnClasses;


/**
 * Tests that a robot can use reflection on its own classes and on allowed Java classes.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestReflectionOnOwnClasses extends RobocodeTestBed {
	private final StringBuilder output = new StringBuilder();

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.ReflectionOnOwnClasses";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		output.append(event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot());
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue("Robot could not use reflection on its own classes:\n" + output,
				output.toString().contains(ReflectionOnOwnClasses.OK_MESSAGE));
	}
}
