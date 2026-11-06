/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.jupiter.api.Test;

import java.io.File;


/**
 * Tests that a robot is stopped when it tries to read and write files outside its directories, even if it catches the
 * exceptions.
 *
 * @author Pavel Savara (original)
 */
public class TestFileAttack extends RobocodeTestBed {

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.FileAttack";
	}

	@Override
	protected int getExpectedErrors() {
		return 2; // One for reading the file, and one for writing the file
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
		super.runTeardown();
		Assert.assertTrue(errorText.toString().contains("You must use a RobocodeFileOutputStream or RobocodeFileWriter"),
				"Robot must be told why:\n" + errorText);
		Assert.assertFalse(new File("C:\\Robocode.attack").exists(), "Found attack file");
	}
}
