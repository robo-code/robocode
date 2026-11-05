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
import tested.robots.DataFileTraversal;

import java.io.File;


/**
 * Tests that a robot cannot get a data file outside its data directory, which also creates the file.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestDataFileTraversal extends RobocodeTestBed {

	@Test
	public void run() {
		try {
			super.run();
		} catch (SecurityException ignore) { // The robot is punished, which is fine
		}
	}

	@Override
	public String getRobotName() {
		return "tested.robots.DataFileTraversal";
	}

	@Override
	protected int getExpectedErrors() {
		return errors; // The robot is punished for each attempt
	}

	private File victim(String name) {
		return new File(new File(robotsPath).getParentFile(), name);
	}

	@Override
	protected void runSetup() {
		for (String name : DataFileTraversal.VICTIMS) {
			victim(name).delete();
		}
	}

	@Override
	protected void runTeardown() {
		StringBuilder created = new StringBuilder();

		for (String name : DataFileTraversal.VICTIMS) {
			if (victim(name).exists()) {
				created.append(name).append(' ');
				victim(name).delete();
			}
		}
		Assert.assertEquals("", created.toString(), "The robot created files outside its data directory");
	}
}
