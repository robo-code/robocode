/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Assert;
import org.junit.Test;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;


/**
 * @author Pavel Savara (original)
 */
public class TestLost extends RobocodeTestBed {
	private int lost = 0;
	private int end = 0;
	private int skip = 0;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public int getNumRounds() {
		return 5;
	}

	@Override
	public String getRobotName() {
		return "tested.robots.BattleLost";
	}

	@Override
	public String getEnemyName() {
		return "sample.Fire";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		IRobotSnapshot robot = event.getTurnSnapshot().getRobots()[0];
		final String streamSnapshot = robot.getOutputStreamSnapshot();

		if (streamSnapshot.contains("Death!")) {
			lost++;
		}
		if (streamSnapshot.contains("BattleEnded!")) {
			end++;
		}
		if (streamSnapshot.contains("Skipped!")) {
			skip++;
		}
	}

	@Override
	protected void runTeardown() {
		Assert.assertEquals("should not get SkippedTurn event", 0, skip);
		Assert.assertEquals("always should loose", getNumRounds(), lost);
		Assert.assertEquals("should get BattleEnded event", 1, end);
	}
}
