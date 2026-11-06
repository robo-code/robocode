/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.helpers;


import org.junit.jupiter.api.Test;


/**
 * Test bed for robots that can be loaded, but must be stopped by the robot security when they do something that robots
 * are not allowed to do, e.g. opening a FileOutputStream.
 *
 * @author Flemming N. Larsen (original)
 */
public abstract class RobotPunishedTestBed extends RobocodeTestBed {

	@Test
	public void run() {
		Assert.assertThrows(SecurityException.class, () -> {
			super.run();
		});
	}

	/**
	 * Returns the text that the robot must be told when it is stopped.
	 *
	 * @return the text.
	 */
	protected abstract String getExpectedMessage();

	@Override
	protected void runTeardown() {
		Assert.assertTrue(errorText.toString().contains("Preventing"), "Robot must be stopped:\n" + errorText);
		Assert.assertTrue(errorText.toString().contains(getExpectedMessage()), "Robot must be told why:\n" + errorText);
	}
}
