/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobotPunishedTestBed;
import tested.robots.FileWriterAttack;

import java.io.File;


/**
 * Tests that a robot is stopped when it tries to write a file with a FileWriter.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestFileWriterAttack extends RobotPunishedTestBed {

	@Override
	public String getRobotName() {
		return "tested.robots.FileWriterAttack";
	}

	@Override
	protected String getExpectedMessage() {
		return "You must use a RobocodeFileOutputStream or RobocodeFileWriter";
	}

	@Override
	protected void runTeardown() {
		File victim = new File(new File(robotsPath).getParentFile(), FileWriterAttack.VICTIM_FILE_NAME);
		boolean exists = victim.exists();

		victim.delete();
		super.runTeardown();
		Assert.assertFalse(exists, "Robot wrote a file with a FileWriter: " + victim);
	}
}
