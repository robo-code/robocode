/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.Assert;
import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Test;
import tested.robots.RobocodeFileOutputStreamAttack;

import java.io.File;


/**
 * Tests that a robot cannot use a RobocodeFileOutputStream to write outside its data directory.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestRobocodeFileOutputStreamAttack extends RobocodeTestBed {

	@Test(expected = SecurityException.class)
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.RobocodeFileOutputStreamAttack";
	}

	private File getAttackFile() {
		return new File(new File(robotsPath).getParentFile(), RobocodeFileOutputStreamAttack.ATTACK_FILE_NAME);
	}

	@Override
	protected void runSetup() {
		File attack = getAttackFile();

		if (attack.exists()) {
			Assert.assertTrue(attack.delete());
		}
	}

	@Override
	protected void runTeardown() {
		File attack = getAttackFile();
		boolean found = attack.exists();

		if (found) {
			attack.delete();
		}
		Assert.assertFalse("Found attack file outside the robot data directory: " + attack, found);
	}
}
