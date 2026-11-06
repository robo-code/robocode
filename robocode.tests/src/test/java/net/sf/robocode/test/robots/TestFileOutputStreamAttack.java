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
 * Tests that a robot is stopped when it tries to write a file with a FileOutputStream, even in its data directory.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestFileOutputStreamAttack extends RobotPunishedTestBed {

	@Override
	public String getRobotName() {
		return "tested.robots.FileOutputStreamAttack";
	}

	@Override
	protected String getExpectedMessage() {
		return "You must use a RobocodeFileOutputStream or RobocodeFileWriter";
	}
}
