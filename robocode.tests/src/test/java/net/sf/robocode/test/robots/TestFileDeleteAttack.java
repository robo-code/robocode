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
import tested.robots.FileDeleteAttack;

import java.io.File;
import java.io.IOException;


/**
 * Tests that a robot cannot delete a file outside its data directory.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestFileDeleteAttack extends RobocodeTestBed {

	@Test
	public void run() {
		Assert.assertThrows(SecurityException.class, () -> {
			super.run();
		});
	}

	@Override
	public String getRobotName() {
		return "tested.robots.FileDeleteAttack";
	}

	private File getVictimFile() {
		return new File(new File(robotsPath).getParentFile(), FileDeleteAttack.VICTIM_FILE_NAME);
	}

	@Override
	protected void runSetup() {
		try {
			File victim = getVictimFile();
			Assert.assertTrue(victim.exists() || victim.createNewFile(), "Could not create victim file");
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	protected void runTeardown() {
		File victim = getVictimFile();
		boolean exists = victim.exists();

		victim.delete();
		Assert.assertTrue(exists, "Robot deleted a file outside its data directory: " + victim);
	}
}
