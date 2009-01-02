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
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobotTestBed;
import org.junit.Test;
import robocode.control.events.TurnEndedEvent;


/**
 * @author Pavel Savara (original)
 */
public class TestEventPriorityFilter extends RobotTestBed {
	boolean messagedScanned;

	@Test
	public void run() {
		super.run();
	}

	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final String out = event.getTurnSnapshot().getRobots()[1].getOutputStreamSnapshot();

		if (out.contains("Scanned!!!")) {
			messagedScanned = true;
		}
	}

	@Override
	public String getRobotNames() {
		return "tested.robots.EventPriorityFilter,tested.robots.EventPriorityFilter";
	}

	@Override
	protected void runTeardown() {
		Assert.assertFalse(messagedScanned);
	}
}

