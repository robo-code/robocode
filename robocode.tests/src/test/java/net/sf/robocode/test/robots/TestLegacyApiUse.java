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
import tested.robots.LegacyApiUse;


/**
 * Tests that robots can use the Java classes that existing robots use, without being punished.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestLegacyApiUse extends RobocodeTestBed {
	private final StringBuilder output = new StringBuilder();

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.LegacyApiUse";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		output.append(event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot());
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(output.toString().contains(LegacyApiUse.OK_MESSAGE),
				"Robot could not use the Java classes:\n" + output);
		Assert.assertFalse(output.toString().contains("SYSTEM: Preventing"), "Robot was punished:\n" + output);
	}
}
