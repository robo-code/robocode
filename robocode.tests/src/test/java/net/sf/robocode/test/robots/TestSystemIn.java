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
import tested.robots.SystemInUse;

import java.io.IOException;


/**
 * Tests that the program using Robocode can read from System.in, which robots cannot with Java 24+.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestSystemIn extends RobocodeTestBed {
	private final StringBuilder output = new StringBuilder();

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.SystemInUse";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		output.append(event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot());
	}

	@Override
	protected void runTeardown() {
		try {
			System.in.available();
		} catch (IOException ignore) { // There might be no input in the test, which is fine. It must only be allowed.
		}
		if (Runtime.version().feature() >= 24) {
			Assert.assertTrue(output.toString().contains(SystemInUse.DENIED_MESSAGE), "Robot could read System.in:\n" + output);
		}
	}
}
