/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Test;
import robocode.control.events.TurnEndedEvent;

import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public class TestFileAttack extends RobocodeTestBed {
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

		if (out.contains("Preventing tested.robots.FileAttack from access: (java.io.FilePermission C:\\MSDOS.SYS read)")
				|| out.contains(
						"Preventing tested.robots.FileAttack from access: (\"java.io.FilePermission\" \"C:\\MSDOS.SYS\" \"read\")")) {
			messagedRead = true;
		}
		if (out.contains(
				"Preventing tested.robots.FileAttack from access: (java.io.FilePermission C:\\Robocode.attack write)")
						|| out.contains(
								"Preventing tested.robots.FileAttack from access: (\"java.io.FilePermission\" \"C:\\Robocode.attack\" \"write\")")) {
			messagedWrite = true;
		}
	}

	@Override
	public String getRobotNames() {
		return "sample.Fire,tested.robots.FileAttack";
	}

	@Override
	protected void runSetup() {
		File attack = new File("C:\\Robocode.attack");

		if (attack.exists()) {
			Assert.assertTrue(attack.delete());
		}
	}

	@Override
	protected void runTeardown() {
		Assert.assertTrue("Didn't seen preventing read", messagedRead);
		Assert.assertTrue("Didn't seen preventing write", messagedWrite);
		Assert.assertFalse("Found attack file", new File("C:\\Robocode.attack").exists());
	}

	@Override
	protected int getExpectedErrors() {
		return 2; // Security error must be reported as an error
	}
}
