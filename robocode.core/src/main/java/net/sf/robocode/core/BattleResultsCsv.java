/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.core;

import robocode.BattleResults;

import java.io.IOException;
import java.io.Writer;

final class BattleResultsCsv {
	private BattleResultsCsv() {}

	static void write(Writer output, BattleResults[] results) throws IOException {
		output.write("rank,robot_name,score,survival,last_survivor_bonus,bullet_damage,bullet_damage_bonus,"
				+ "ram_damage,ram_damage_bonus,firsts,seconds,thirds\r\n");
		for (BattleResults result : results) {
			String name = result.getTeamLeaderName().replace("\"", "\"\"");
			output.write(result.getRank() + ",\"" + name + "\"," + result.getScore() + ","
					+ result.getSurvival() + "," + result.getLastSurvivorBonus() + ","
					+ result.getBulletDamage() + "," + result.getBulletDamageBonus() + ","
					+ result.getRamDamage() + "," + result.getRamDamageBonus() + ","
					+ result.getFirsts() + "," + result.getSeconds() + "," + result.getThirds() + "\r\n");
		}
	}
}
