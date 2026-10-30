/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobotRejectedTestBed;

import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public class TestFileAttack extends RobotRejectedTestBed {

	@Override
	public String getRobotName() {
		return "tested.robots.FileAttack";
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
		Assert.assertFalse("Found attack file", new File("C:\\Robocode.attack").exists());
	}
}
