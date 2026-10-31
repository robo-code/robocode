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
import org.junit.Test;
import robocode.control.events.TurnEndedEvent;
import tested.robots.FileReadAttack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Tests that a robot cannot read a file outside its directories.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestFileReadAttack extends RobocodeTestBed {
	private static final String SECRET = "secret-file-content";

	private final StringBuilder output = new StringBuilder();

	@Test(expected = SecurityException.class)
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.FileReadAttack";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		output.append(event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot());
	}

	private File getVictimFile() {
		return new File(new File(robotsPath).getParentFile(), FileReadAttack.VICTIM_FILE_NAME);
	}

	@Override
	protected void runSetup() {
		try {
			FileWriter writer = new FileWriter(getVictimFile());
			writer.write(SECRET);
			writer.close();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	protected void runTeardown() {
		getVictimFile().delete();
		Assert.assertFalse("Robot read a file outside its directories", output.toString().contains(SECRET));
	}
}
