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


/**
 * @author Flemming N. Larsen (original)
 */
public class TestConstructorReflectionAttack extends RobocodeTestBed {
	private final StringBuilder output = new StringBuilder();

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.ConstructorReflectionAttack";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		output.append(event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot());
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue("Reflection is not allowed:\n" + output,
				output.toString().contains("java.lang.System.out cannot be made accessible"));
		Assert.assertFalse("Robot used reflection", output.toString().contains("Hello World"));
	}

	@Override
	protected int getExpectedErrors() {
		return 1; // Security error must be reported as an error
	}
}
