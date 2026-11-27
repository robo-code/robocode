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
}
