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


/**
 * Tests that a robot cannot get the real System.in with reflection.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestSystemInReflection extends RobocodeTestBed {

	@Test
	public void run() {
		Assert.assertThrows(SecurityException.class, () -> {
			super.run();
		});
	}

	@Override
	public String getRobotName() {
		return "tested.robots.SystemInReflection";
	}
}
