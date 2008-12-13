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

import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public class TestFileAttack extends RobotTestBed {
	boolean messagedWrite;
	boolean messagedRead;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		final String out = event.getTurnSnapshot().getRobots()[1].getOutputStreamSnapshot();

		if (out.contains(
				"java.security.AccessControlException: Preventing testing.FileAttack from access: (java.io.FilePermission C:\\MSDOS.SYS read)")) {
			messagedRead = true;
		}
		if (out.contains(
				"java.security.AccessControlException: Preventing testing.FileAttack from access: (java.io.FilePermission C:\\Robocode.attack write)")) {
			messagedWrite = true;
		}
	}

	@Override
	public String getRobotNames() {
		return "sample.SittingDuck,testing.FileAttack";
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue(messagedRead);
		Assert.assertTrue(messagedWrite);
		Assert.assertFalse(new File("C:\\Robocode.attack").exists());
	}

}
