/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobotTestBed;
import robocode.control.events.TurnEndedEvent;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestConstructorThreadAttack extends RobotTestBed {
	boolean messagedUnknown;

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final String out = event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot();

		if (out.contains("Preventing thread Thread-") && out.contains("with unknown thread group MyAttack from access")) {
			messagedUnknown = true;
		}
	}

	@Override
	public String getRobotNames() {
		return "tested.robots.ConstructorThreadAttack,sample.Target";
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(messagedUnknown);
	}
}
