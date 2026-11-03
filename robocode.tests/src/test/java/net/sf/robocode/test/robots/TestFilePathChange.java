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
import robocode.control.events.TurnEndedEvent;
import tested.robots.FilePathChange;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Tests that the robot security checks and uses the same path of a file, even if the path of a File subclass changes.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestFilePathChange extends RobocodeTestBed {
	private static final String SECRET = "secret-file-content";

	private final StringBuilder output = new StringBuilder();

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "tested.robots.FilePathChange";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		output.append(event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot());
	}

	private File victim(String name) {
		return new File(new File(robotsPath).getParentFile(), name);
	}

	@Override
	protected void runSetup() {
		try {
			for (String name : new String[] {FilePathChange.READ_VICTIM_FILE_NAME, FilePathChange.DELETE_VICTIM_FILE_NAME}) {
				FileWriter writer = new FileWriter(victim(name));

				writer.write(SECRET);
				writer.close();
			}
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	protected void runTeardown() {
		boolean deleted = !victim(FilePathChange.DELETE_VICTIM_FILE_NAME).exists();

		victim(FilePathChange.READ_VICTIM_FILE_NAME).delete();
		victim(FilePathChange.DELETE_VICTIM_FILE_NAME).delete();
		Assert.assertFalse(output.toString().contains(SECRET), "Robot read a file outside its directories:\n" + output);
		Assert.assertFalse(deleted, "Robot deleted a file outside its data directory:\n" + output);
	}
}
