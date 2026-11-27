/*
 * Copyright (c) 2001-2026 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.core;

import net.sf.robocode.battle.BattleResultsTableModel;
import robocode.BattleResults;

import java.io.IOException;
import java.io.Writer;

final class BattleResultsCsv {
	private BattleResultsCsv() {}

	/**
	 * Writes the results as CSV.
	 *
	 * @param output the writer to write the CSV to.
	 * @param sortedResults the results sorted by score, with the highest score first.
	 * @throws IOException if the CSV could not be written.
	 */
	static void write(Writer output, BattleResults[] sortedResults) throws IOException {
		output.write("rank,robot_name,score,survival,last_survivor_bonus,bullet_damage,bullet_damage_bonus,"
				+ "ram_damage,ram_damage_bonus,firsts,seconds,thirds\r\n");
		for (int i = 0; i < sortedResults.length; i++) {
			BattleResults result = sortedResults[i];
			// The rank of the battle results is not the placement, so the placement is found as in the results table
			int rank = BattleResultsTableModel.getPlace(sortedResults, i);
			String name = result.getTeamLeaderName().replace("\"", "\"\"");
			output.write(rank + ",\"" + name + "\"," + result.getScore() + ","
					+ result.getSurvival() + "," + result.getLastSurvivorBonus() + ","
					+ result.getBulletDamage() + "," + result.getBulletDamageBonus() + ","
					+ result.getRamDamage() + "," + result.getRamDamageBonus() + ","
					+ result.getFirsts() + "," + result.getSeconds() + "," + result.getThirds() + "\r\n");
		}
	}
}
