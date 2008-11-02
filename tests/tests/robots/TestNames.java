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


import helpers.RobotTestBed;
import helpers.Assert;
import org.junit.Test;
import robocode.battle.events.TurnEndedEvent;

import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public class TestNames extends RobotTestBed {
	boolean messagedNamespace;
	boolean messagedName;
	boolean messagedRobocode;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public int getExpectedRobotCount(String list) {
		return 3;
	}

	@Override
	public String getRobotNames() {
		return "sample.Fire,robocode.BadNamespace,testing.TooLongNameThisIsReallyTooLongName,tooLongNamespaceThisIsReallyTooLongNamespace.TooLongNamespace";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		final String out1 = event.getTurnSnapshot().getRobots().get(1).getOutputStreamSnapshot();

		if (out1.contains("classname is too long")) {
			messagedName = true;
		}
		final String out2 = event.getTurnSnapshot().getRobots().get(2).getOutputStreamSnapshot();

		if (out2.contains("package name is too long")) {
			messagedNamespace = true;
		}
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(messagedNamespace);
		Assert.assertTrue(messagedName);
	}

}
