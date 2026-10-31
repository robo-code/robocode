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
import tested.robots.DeserializationAttack;


/**
 * Tests that a robot can deserialize its own data, but not classes that could be misused for attacking the host.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestDeserializationAttack extends RobocodeTestBed {
	private final StringBuilder output = new StringBuilder();

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.DeserializationAttack";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		output.append(event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot());
	}

	@Override
	protected void runTeardown() {
		String out = output.toString();

		Assert.assertTrue("Robot could not deserialize its own data", out.contains(DeserializationAttack.OWN_DATA_MESSAGE));
		Assert.assertFalse("Robot deserialized a class that is not allowed",
				out.contains(DeserializationAttack.DESERIALIZED_MESSAGE));
		Assert.assertTrue("Deserialization was not prevented", out.contains(DeserializationAttack.PREVENTED_MESSAGE));
	}
}
