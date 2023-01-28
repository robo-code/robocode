/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
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
public class TestConstructorThreadAttack extends RobocodeTestBed {
	boolean messagedUnknown;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final String out = event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot();

		if (out.contains("Preventing Thread-") && out.contains("from access to MyAttack")) {
			messagedUnknown = true;
		}
	}

	@Override
	public String getRobotName() {
		return "tested.robots.ConstructorThreadAttack";
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(messagedUnknown);
	}
}
