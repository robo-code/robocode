/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robots;


import helpers.Assert;
import helpers.RobotTestBed;
import org.junit.Test;
import robocode.control.events.TurnEndedEvent;


/**
 * @author Pavel Savara (original)
 */
public class TestSkippedTurns extends RobotTestBed {
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
		return "sample.TrackFire,testing.SkipTurns";
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(messagedEvent);
		Assert.assertTrue(messagedBattle);
	}
}
