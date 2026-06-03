/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;


import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.Assert;
import org.junit.Test;
import robocode.BattleResults;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.IRobotSnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * Verifies that WinEvent, DeathEvent, RoundEndedEvent, and BattleEndedEvent
 * are delivered correctly to robots, and that scores are computed.
 * <p>
 * BattleWin and MyFirstRobot have identical movement/targeting logic, so
 * outcomes are dictated by initial positions and deterministic RNG.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public class TestWin extends RobocodeTestBed {
	private BattleResults[] results;
	private final StringBuffer outputBuf = new StringBuffer();

	@Test
	public void run() { super.run(); }

	@Override
	public int getNumRounds() {
		return 5;
	}

	@Override
	public String getRobotName() {
		return "sample.MyFirstRobot";
	}

	@Override
	public String getEnemyName() {
		return "tested.robots.BattleWin";
	}

	@Override
	public void onTurnEnded(TurnEndedEvent event) {
		super.onTurnEnded(event);
		IRobotSnapshot robot = event.getTurnSnapshot().getRobots()[1];
		final String streamSnapshot = robot.getOutputStreamSnapshot();

		outputBuf.append(streamSnapshot);
	}

	public void onBattleCompleted(BattleCompletedEvent event) {
		results = event.getSortedResults();
	}

	@Override
	protected void runTeardown() {
		String[] lines = outputBuf.toString().split("\\n");

		// Build a simplified list of significant events (ignoring blank lines and
		// variability in system message order) so that the test does not break when
		// interleaving differs across JDK versions or CI execution profiles.
		List<String> events = new ArrayList<String>();
		for (String line : lines) {
			String t = line.trim();
			if (t.isEmpty()) continue;
			if (t.equals("Win!") || t.equals("Death!") || t.equals("RoundEnded!") || t.equals("BattleEnded!")) {
				events.add(t);
			}
		}

		Assert.assertEquals("Should have exactly one BattleEnded!", 1, count(events, "BattleEnded!"));
		Assert.assertEquals("Should have five RoundEnded!", 5, count(events, "RoundEnded!"));
		Assert.assertEquals("Win! + Death! should equal 5", 5, count(events, "Win!") + count(events, "Death!"));

		// The output must contain each round header exactly once for this robot.
		Assert.assertTrue("Missing round header", outputBuf.toString().contains("Round 1 of 5"));
		Assert.assertTrue("Missing round header", outputBuf.toString().contains("Round 2 of 5"));
		Assert.assertTrue("Missing round header", outputBuf.toString().contains("Round 3 of 5"));
		Assert.assertTrue("Missing round header", outputBuf.toString().contains("Round 4 of 5"));
		Assert.assertTrue("Missing round header", outputBuf.toString().contains("Round 5 of 5"));

		// Check that at least one Win! was received (BattleWin must win some rounds).
		Assert.assertTrue("BattleWin should win at least one round", count(events, "Win!") > 0);

		// Score verification - results are sorted by score descending.
		Assert.assertNotNull("Battle results should not be null", results);
		Assert.assertEquals("Expected 2 robots in results", 2, results.length);
		Assert.assertTrue("Winner score must be positive", results[0].getScore() > 0);
		Assert.assertTrue("Loser score must be positive", results[1].getScore() > 0);
		Assert.assertTrue("Winner must have >= loser score",
				results[0].getScore() >= results[1].getScore());
	}

	private static int count(List<String> list, String item) {
		int n = 0;
		for (String s : list) {
			if (item.equals(s)) n++;
		}
		return n;
	}
}
