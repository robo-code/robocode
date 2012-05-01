/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
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
import robocode.control.events.TurnEndedEvent;


/**
 * @author Pavel Savara (original)
 */
public class TestThreadAttack extends RobotTestBed {
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

		if (out.contains("You may only create 5 threads")) {
			messagedMax = true;
		}

		if (out.contains("Preventing thread Thread-") && out.contains("with unknown thread group MyAttack from access")) {
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
