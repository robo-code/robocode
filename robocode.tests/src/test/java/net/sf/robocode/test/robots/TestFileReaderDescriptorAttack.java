/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobotPunishedTestBed;




/**
 * Tests that a robot is stopped when it tries to read from a file descriptor with a FileReader.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestFileReaderDescriptorAttack extends RobotPunishedTestBed {

	@Override
	public String getRobotName() {
		return "tested.robots.FileReaderDescriptorAttack";
	}

	@Override
	protected String getExpectedMessage() {
		return "You may only read files in your own root package directory";
	}

}
