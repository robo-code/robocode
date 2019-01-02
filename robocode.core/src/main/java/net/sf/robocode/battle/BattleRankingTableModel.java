/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.battle;


import robocode.control.snapshot.IScoreSnapshot;
import robocode.control.snapshot.ITurnSnapshot;

import javax.swing.table.AbstractTableModel;


/**
 * This table model extracts the robot ranking from the current battle,
 * in order to be displayed by the RankingDialog.
 *
 * @author Luis Crespo (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Endre Palatinus, Eniko Nagy, Attila Csizofszki and Laszlo Vigh (contributors)
 */
@SuppressWarnings("serial")
public class BattleRankingTableModel extends AbstractTableModel {

	private IScoreSnapshot[] scoreSnapshotList;

	// The sum of the scores gathered by the robots in the actual round
	private double currentSum;

	// The sum of the scores gathered by the robots in the previous rounds
	private double totalSum;

	/**
	 * Function for counting the sum of the scores gathered by the robots.
	 */
	private void countTotalScores() {
		currentSum = 0;
		totalSum = 0;

		for (IScoreSnapshot score : scoreSnapshotList) {
			currentSum += score.getCurrentScore();
			totalSum += score.getTotalScore();
		}
	}

	public void updateSource(ITurnSnapshot snapshot) {
		if (snapshot != null) {
			scoreSnapshotList = snapshot.getSortedTeamScores();
			countTotalScores();
		} else {
			scoreSnapshotList = null;
		}
	}

	public int getColumnCount() {
		return 12;
	}

	public int getRowCount() {
		return scoreSnapshotList == null ? 0 : scoreSnapshotList.length;
	}

	@Override
	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return "Rank";

		case 1:
			return "Robot Name";

		case 2:
			return "          Total Score          ";

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

		final IScoreSnapshot statistics = scoreSnapshotList[row];

		switch (col) {
		case 0:
			return getPlacementString(row + 1);

		case 1:
			return statistics.getName();

		case 2: {
			final double current = statistics.getCurrentScore();
			final double total = statistics.getTotalScore();

			return (int) (current + 0.5) + " / " + (int) (total + current + 0.5) + "  ("
					+ (int) (current / currentSum * 100) + " / " + (int) ((total + current) / (totalSum + currentSum) * 100)
					+ "%)";
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

	private static String getPlacementString(int i) {
		String result = "" + i;

		if (i > 3 && i < 20) {
			result += "th";
		} else if (i % 10 == 1) {
			result += "st";
		} else if (i % 10 == 2) {
			result += "nd";
		} else if (i % 10 == 3) {
			result += "rd";
		} else {
			result += "th";
		}
		return result;
	}
}
