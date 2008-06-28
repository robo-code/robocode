/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Luis Crespo
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Added independent Rank column
 *     - Various optimizations
 *     - Ported to Java 5
 *     - Updated to use the getPlacementString() methods from the StringUtil,
 *       which replaces the same method from robocode.util.Utils
 *     - Added additional scores, so that the rankings are similar to the battle
 *       results
 *     - Updated to contain both current and total scores in the columns where it
 *       makes sense
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.ui;


import robocode.battle.snapshot.ScoreSnapshot;
import robocode.battle.snapshot.TurnSnapshot;
import robocode.text.StringUtil;

import javax.swing.table.AbstractTableModel;
import java.util.List;


/**
 * This table model extracts the robot ranking from the current battle,
 * in order to be displayed by the RankingDialog.
 *
 * @author Luis Crespo (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class BattleRankingTableModel extends AbstractTableModel {

	List<ScoreSnapshot> scoreSnapshotList;

	public void updateSource(TurnSnapshot snapshot) {
		if (snapshot != null) {
			scoreSnapshotList = snapshot.getTeamScores();
		} else {
			scoreSnapshotList = null;
		}
	}

	public int getColumnCount() {
		return 12;
	}

	public int getRowCount() {
		return scoreSnapshotList == null ? 0 : scoreSnapshotList.size();
	}

	@Override
	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return "Rank";

		case 1:
			return "Robot Name";

		case 2:
			return "    Total Score    ";

		case 3:
			return "     Survival     ";

		case 4:
			return "Surv Bonus";

		case 5:
			return "    Bullet Dmg    ";

		case 6:
			return " Bullet Bonus ";

		case 7:
			return "Ram Dmg * 2";

		case 8:
			return "Ram Bonus";

		case 9:
			return " 1sts ";

		case 10:
			return " 2nds ";

		case 11:
			return " 3rds ";

		default:
			return "";
		}
	}

	public Object getValueAt(int row, int col) {

		final ScoreSnapshot statistics = scoreSnapshotList.get(row);

		switch (col) {
		case 0:
			return StringUtil.getPlacementString(row + 1);

		case 1:
			return statistics.getName();

		case 2: {
			final double current = statistics.getCurrentScore();
			final double total = statistics.getTotalScore();

			return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
		}

		case 3: {
			final double current = statistics.getCurrentSurvivalScore();
			final double total = statistics.getTotalSurvivalScore();

			return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
		}

		case 4:
			return (int) (statistics.getTotalLastSurvivorBonus() + 0.5);

		case 5: {
			final double current = statistics.getCurrentBulletDamageScore();
			final double total = statistics.getTotalBulletDamageScore();

			return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
		}

		case 6: {
			final double current = statistics.getCurrentBulletKillBonus();
			final double total = statistics.getTotalBulletKillBonus();

			return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
		}

		case 7: {
			final double current = statistics.getCurrentRammingDamageScore();
			final double total = statistics.getTotalRammingDamageScore();

			return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
		}

		case 8: {
			final double current = statistics.getCurrentRammingKillBonus();
			final double total = statistics.getTotalRammingKillBonus();

			return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
		}

		case 9:
			return "" + statistics.getTotalFirsts();

		case 10:
			return "" + statistics.getTotalSeconds();

		case 11:
			return "" + statistics.getTotalThirds();

		default:
			return "";
		}
	}
}
