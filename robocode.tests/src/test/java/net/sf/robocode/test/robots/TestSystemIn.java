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

import java.io.IOException;


/**
 * Tests that a robot is stopped when it tries to read from System.in, while the program using Robocode can read from it,
 * also while the battle is running.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestSystemIn extends RobocodeTestBed {
	private SecurityException hostFailure;
	private boolean hostReadDuringBattle;

	@Test
	public void run() {
		Assert.assertThrows(SecurityException.class, () -> {
			super.run();
		});
	}

	@Override
	public String getRobotName() {
		return "tested.robots.SystemInUse";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		hostReadDuringBattle = true;
		readAsHost();
	}

	private void readAsHost() {
		try {
			System.in.available();
		} catch (IOException ignore) { // There might be no input in the test, but it must be allowed
		} catch (SecurityException e) {
			hostFailure = e;
		}
	}

	@Override
	protected void runTeardown() {
		readAsHost();
		Assert.assertTrue(hostFailure == null, "The program could not read from System.in: " + hostFailure);
		Assert.assertTrue(hostReadDuringBattle, "The program did not try to read during the battle");
		Assert.assertTrue(errorText.toString().contains("from reading from System.in"), "Robot must be stopped:\n" + errorText);
	}
}
