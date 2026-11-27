/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.test.robots;

import net.sf.robocode.test.helpers.RobocodeTestBed;
import org.junit.jupiter.api.Test;
import robocode.BattleResults;
import robocode.control.events.BattleCompletedEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests that the rank in the battle results is the placement by score, and not the order of the robots in the battle.
 * The first robot loses, so a rank taken from the order of the robots would be caught.
 */
public class TestBattleResultsRank extends RobocodeTestBed {

	private BattleResults[] indexedResults;
	private BattleResults[] sortedResults;

	@Test
	public void run() {
		super.run();
	}

	@Override
	public String getRobotName() {
		return "sample.SittingDuck";
	}

	@Override
	public String getEnemyName() {
		return "sample.Walls,sample.Fire,sample.Corners";
	}

	@Override
	public int getNumRounds() {
		return 3;
	}

	@Override
	public void onBattleCompleted(BattleCompletedEvent event) {
		indexedResults = event.getIndexedResults();
		sortedResults = event.getSortedResults();
	}

	@Override
	protected void runTeardown() {
		assertNotNull(sortedResults, "The battle did not complete");
		assertEquals(4, sortedResults.length);

		for (BattleResults result : sortedResults) {
			// The placement, where results with the same score share the placement of the last of them
			int place = 0;
			for (BattleResults other : sortedResults) {
				if (other.getScore() >= result.getScore()) {
					place++;
				}
			}
			assertEquals(place, result.getRank(), "Rank of " + result.getTeamLeaderName() + " with score "
					+ result.getScore() + "\n" + describe(sortedResults));
		}

		// The indexed results are still in the order of the robots in the battle
		assertEquals("sample.SittingDuck", indexedResults[0].getTeamLeaderName());
		assertEquals(4, indexedResults[0].getRank(), describe(sortedResults));
	}

	private static String describe(BattleResults[] results) {
		StringBuilder text = new StringBuilder();
		for (BattleResults result : results) {
			text.append(result.getRank()).append(' ').append(result.getTeamLeaderName()).append(' ')
					.append(result.getScore()).append('\n');
		}
		return text.toString();
	}
}
