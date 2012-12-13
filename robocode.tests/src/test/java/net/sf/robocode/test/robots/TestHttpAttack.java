/*******************************************************************************
 * Copyright (c) 2001-2012 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Assert;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * @author Flemming N. Larsen (original)
 */
public class TestHttpAttack extends RobocodeTestBed {

	private boolean messagedAccessDenied;
	
	@Override
	public String getRobotNames() {
		return "tested.robots.HttpAttack,sample.Target";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);

		String out = "";
		for (IRobotSnapshot robot : event.getTurnSnapshot().getRobots()) {
			if (robot.getName().equals("tested.robots.HttpAttack")) {
				out = robot.getOutputStreamSnapshot();
				break;
			}
		}
		if (out.contains("access denied (java.net.SocketPermission")) {
			messagedAccessDenied = true;	
		}	
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue("HTTP connection is not allowed", messagedAccessDenied);
	}

	@Override
	protected int getExpectedErrors() {
		return 1; // Security error must be reported as an error
	}
}
