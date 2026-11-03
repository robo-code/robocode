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
import tested.robots.FileOutputStreamReflectionAttack;

import java.io.File;


/**
 * Tests that a robot cannot create a FileOutputStream with reflection.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestFileOutputStreamReflectionAttack extends RobotPunishedTestBed {

	@Override
	public String getRobotName() {
		return "tested.robots.FileOutputStreamReflectionAttack";
	}

	@Override
	protected String getExpectedMessage() {
		return "FileOutputStream";
	}

	@Override
	protected void runTeardown() {
		File victim = new File(new File(robotsPath).getParentFile(), FileOutputStreamReflectionAttack.VICTIM_FILE_NAME);
		boolean exists = victim.exists();

		victim.delete();
		super.runTeardown();
		Assert.assertFalse(exists, "Robot wrote a file via reflection: " + victim);
	}
}
