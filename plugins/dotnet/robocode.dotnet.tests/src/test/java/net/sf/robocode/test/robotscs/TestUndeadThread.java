/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robotscs;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Test;
import robocode.control.events.BattleErrorEvent;


/**
 * @author Pavel Savara (original)
 */
public class TestUndeadThread extends RobocodeTestBed {
	boolean messagedStop;

	@Test
	public void run() {
		super.run();
	}

	public void onBattleError(BattleErrorEvent event) {
		super.onBattleError(event);
		final String error = event.getError();

		if (error.contains("Unable to stop thread")) {
			messagedStop = true;
		}
	}

	@Override
	protected int getExpectedErrors() {
		return 1;
	}

	@Override
	public String getRobotNames() {
		return "SampleCs.SittingDuck,tested.robotscs.UndeadThread";
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(messagedStop);
	}

}
