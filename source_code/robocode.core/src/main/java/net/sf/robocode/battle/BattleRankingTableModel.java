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
 *     Endre Palatinus, Eniko Nagy, Attila Csizofszki and Laszlo Vigh
 *     - Score with % (percentage) in the table view
 *******************************************************************************/
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

	IScoreSnapshot[] scoreSnapshotList;

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
			currentSum += score.getCurrentCombinedScore();
			totalSum += score.getCombinedScore();
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
		if (col < 3) {
			if (col == 0) {
				return "Rank";
			} else if (col == 1) {
				return "Robot Name";
			} else {
				return "          Total Score          ";
			}
		} else if (scoreSnapshotList != null && scoreSnapshotList.length > 0
				&& col - 3 >= scoreSnapshotList[0].getScoreNames().size()) {
			int size = scoreSnapshotList[0].getScoreNames().size();

			if (col == size - 3) {
				return " 1sts ";
			} else if (col == size - 2) {
				return " 2nds ";
			} else if (col == size - 1) {
				return " 3rds ";
			}
		} else if (scoreSnapshotList != null && scoreSnapshotList.length > 0) {
			return scoreSnapshotList[0].getScoreNames().get(col - 3);
		}
		return "";
	}

	public Object getValueAt(int row, int col) {

		final IScoreSnapshot statistics = scoreSnapshotList[row];

		int size = statistics.getTotalScores().size();

		if (col < size + 6) {
			if (col == 0) {
				return getPlacementString(row + 1);
			} else if (col == 1) {
				return statistics.getName();
			} else if (col == 2) {
				final double current = statistics.getCurrentCombinedScore();
				final double total = statistics.getCombinedScore();

				return (int) (current + 0.5) + " / " + (int) (total + current + 0.5) + "  ("
						+ (int) (current / currentSum * 100) + " / "
						+ (int) ((total + current) / (totalSum + currentSum) * 100) + "%)";
			} else if (col == size - 3) {
				return "" + statistics.getTotalFirsts();
			} else if (col == size - 2) {
				return "" + statistics.getTotalSeconds();
			} else if (col == size - 1) {
				return "" + statistics.getTotalThirds();
			} else {
				final double current = statistics.getCurrentScores().get(col - 3);
				final double total = statistics.getTotalScores().get(col - 3);

				return (int) (current + 0.5) + " / " + (int) (total + current + 0.5);
			}
		}
		
		return "";
		
	}

	public static String getPlacementString(int i) {
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
