/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.battle;

import org.junit.jupiter.api.Test;
import robocode.BattleResults;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BattleResultsTableModelTest {

	// Scores sorted with the highest first, where two robots share the same score
	private static final BattleResults[] SORTED_RESULTS = {
			result("a", 300), result("b", 200), result("c", 200), result("d", 100)
	};

	@Test
	void placesFollowTheScoresAndTiesShareThePlace() {
		assertEquals(1, BattleResultsTableModel.getPlace(SORTED_RESULTS, 0));
		assertEquals(3, BattleResultsTableModel.getPlace(SORTED_RESULTS, 1));
		assertEquals(3, BattleResultsTableModel.getPlace(SORTED_RESULTS, 2));
		assertEquals(4, BattleResultsTableModel.getPlace(SORTED_RESULTS, 3));
	}

	@Test
	void resultsTableShowsThePlaces() {
		BattleResultsTableModel table = new BattleResultsTableModel(SORTED_RESULTS, 10);

		assertEquals("1st", table.getValueAt(0, 0));
		assertEquals("3rd", table.getValueAt(1, 0));
		assertEquals("3rd", table.getValueAt(2, 0));
		assertEquals("4th", table.getValueAt(3, 0));
	}

	private static BattleResults result(String name, double score) {
		// The rank given here is wrong on purpose, as the placement must not depend on it
		return new BattleResults(name, 99, score, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}
}
