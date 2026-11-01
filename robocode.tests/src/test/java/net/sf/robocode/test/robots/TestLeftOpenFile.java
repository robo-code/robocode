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
import robocode.control.events.BattleMessageEvent;
import robocode.control.events.TurnEndedEvent;

import java.io.File;


/**
 * A file that a robot leaves open must be closed when the battle ends, and the author must be told. On Windows, an
 * open file cannot be deleted, which would keep the RoboRumble client from clearing the robot data.
 *
 * @author Flemming N. Larsen (original)
 */
public class TestLeftOpenFile extends RobocodeTestBed {
	private static final String DATA_FILE = "Data file: ";

	private String dataFile;
	private boolean warned;

	@Test
	public void run() {
		super.run();
		Assert.assertNotNull(dataFile, "The robot did not write its file");
		Assert.assertTrue(warned, "There was no warning about the open file");
		Assert.assertTrue(new File(dataFile).delete(), "The file could not be deleted after the battle");
	}

	@Override
	public String getRobotName() {
		return "tested.robots.LeavesFileOpen";
	}

	@Override
	public String getEnemyName() {
		return "sample.Target";
	}

	@Override
	public int getNumRounds() {
		return 1;
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		for (String line : event.getTurnSnapshot().getRobots()[0].getOutputStreamSnapshot().split("\\R")) {
			if (line.startsWith(DATA_FILE)) {
				dataFile = line.substring(DATA_FILE.length()).trim();
			}
		}
	}

	@Override
	public void onBattleMessage(BattleMessageEvent event) {
		super.onBattleMessage(event);
		String message = event.getMessage();

		if (message.contains("tested.robots.LeavesFileOpen") && message.contains("open.dat")) {
			warned = true;
		}
	}
}
