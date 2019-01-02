/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import org.junit.Ignore;

import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import robocode.control.events.TurnEndedEvent;


/**
 * @author Pavel Savara (original)
 */
@Ignore("Please run explicitly if you did something to security or timing")
public class TestThreadAttack extends RobocodeTestBed {
	boolean messagedMax;
	boolean messagedUnknown;

	@Override
	public String getRobotNames() {
		return "tested.robots.ThreadAttack,sample.SittingDuck";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final String out = event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot();

		if (out.contains("Robots are only allowed to create up to 5 threads!")) {
			messagedMax = true;
		}

		if (out.contains("Preventing Thread-") && out.contains("from access to MyAttack")) {
			messagedUnknown = true;
		}
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(messagedMax);
		Assert.assertTrue(messagedUnknown);
	}

	@Override
	protected int getExpectedErrors() {
		return 1; // Security error must be reported as an error
	}
}
