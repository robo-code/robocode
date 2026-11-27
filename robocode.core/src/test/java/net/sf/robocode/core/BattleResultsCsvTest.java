/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.core;

import org.junit.jupiter.api.Test;
import robocode.BattleResults;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BattleResultsCsvTest {
	@Test
	void writesStableHeadersAndQuotesRobotNames() throws Exception {
		BattleResults result = new BattleResults("Robô, \"Azul\"\nLab", 1, 42, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		StringWriter output = new StringWriter();

		BattleResultsCsv.write(output, new BattleResults[] { result });

		assertEquals("rank,robot_name,score,survival,last_survivor_bonus,bullet_damage,bullet_damage_bonus,"
				+ "ram_damage,ram_damage_bonus,firsts,seconds,thirds\r\n"
				+ "1,\"Robô, \"\"Azul\"\"\nLab\",42,2,3,4,5,6,7,8,9,10\r\n", output.toString());
	}

	@Test
	void writesThePlacementAsRankAndNotTheRankOfTheResults() throws Exception {
		// The ranks given here are wrong on purpose, like the ones from BattleCompletedEvent
		BattleResults[] sortedResults = {
				new BattleResults("a", 4, 300, 0, 0, 0, 0, 0, 0, 0, 0, 0),
				new BattleResults("b", 3, 200, 0, 0, 0, 0, 0, 0, 0, 0, 0),
				new BattleResults("c", 2, 200, 0, 0, 0, 0, 0, 0, 0, 0, 0),
				new BattleResults("d", 1, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0)
		};
		StringWriter output = new StringWriter();

		BattleResultsCsv.write(output, sortedResults);

		String[] lines = output.toString().split("\r\n");
		assertEquals(5, lines.length);
		assertEquals("1,\"a\",300,0,0,0,0,0,0,0,0,0", lines[1]);
		assertEquals("3,\"b\",200,0,0,0,0,0,0,0,0,0", lines[2]);
		assertEquals("3,\"c\",200,0,0,0,0,0,0,0,0,0", lines[3]);
		assertEquals("4,\"d\",100,0,0,0,0,0,0,0,0,0", lines[4]);
	}
}
