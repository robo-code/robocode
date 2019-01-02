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
import org.junit.Ignore;
import robocode.control.events.TurnEndedEvent;


/**
 * @author Pavel Savara (original)
 */
@Ignore("is very timing sensitive test, so it usually fails on different machines, please run explicitly if you did something to security or timing")
public class TestSkippedTurns extends RobocodeTestBed {
	boolean messagedBattle;
	boolean messagedEvent;

	@Test
	public void run() {
		super.run();
	}

	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final String out = event.getTurnSnapshot().getRobots()[1].getOutputStreamSnapshot();

		if (out.contains("Skipped!!!")) {
			messagedEvent = true;
		}
		if (out.contains("not performed any actions in a reasonable")) {
			messagedBattle = true;
		}
	}

	@Override
	public String getRobotNames() {
		return "SampleCs.TrackFire,tested.robotscs.SkipTurns";
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(messagedEvent);
		Assert.assertTrue(messagedBattle);
	}
}
