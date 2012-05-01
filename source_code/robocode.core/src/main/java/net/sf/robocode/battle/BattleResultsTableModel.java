/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Replaced ContestantPeerVector with plain Vector
 *     - Added Rank column
 *     - Ported to Java 5
 *     - Optimized
 *     - Code cleanup
 *     - Updated to use methods from the Logger and StringUtil, which
 *       replaces methods that have been (re)moved from the robocode.util.Utils
 *     - Changed the column names to be more informative and equal in width
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Nathaniel Troutman
 *     - Added sanity check on battle object in getRowCount()
 *     Endre Palatinus, Eniko Nagy, Attila Csizofszki and Laszlo Vigh
 *     - Score with % (percentage) in the table view
 *******************************************************************************/
package net.sf.robocode.battle;


import net.sf.robocode.io.Logger;
import robocode.BattleResults;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 * @author Endre Palatinus, Eniko Nagy, Attila Csizofszki and Laszlo Vigh (contributors)
 */
@SuppressWarnings("serial")
public class BattleResultsTableModel extends javax.swing.table.AbstractTableModel {
	private String title;
	private final BattleResults[] results;
	private final int numRounds;
	private List<String> columnNames;

	// The sum of the scores gathered by the robots.
	private final double totalScore;

	public BattleResultsTableModel(BattleResults[] results, int numRounds) {
		this.results = results;
		columnNames = new ArrayList<String>();
		columnNames.add("Rank");
		columnNames.add("Robot Name");
		columnNames.add("Total Score");
		
		if (results != null) {
			columnNames.addAll(results[0].getScoreNames());
		}
		
		columnNames.add("1sts");
		columnNames.add("2nds");
		columnNames.add("3rds");
		
		this.numRounds = numRounds;
		totalScore = countTotalScore();
	}

	/**
	 * Function for counting the sum of the scores gathered by the robots.
	 *
	 * @return The sum.
	 */
	private double countTotalScore() {

		double totalScore = 0;

		for (BattleResults result : results) {
			totalScore += result.getCombinedScore();
		}

		return totalScore;
	}

	public int getColumnCount() {
		return 12;
	}

	@Override
	public String getColumnName(int col) {
		if (columnNames != null && col < columnNames.size()) {
			return columnNames.get(col);
		}
		return "";
	}

	public int getRowCount() {
		return results.length;
	}

	public String getTitle() {
		if (title == null) {
			int round = numRounds;

			title = "Results for " + round + " round";
			if (round > 1) {
				title += 's';
			}
		}
		return title;
	}

	public Object getValueAt(int row, int col) {

		BattleResults statistics = results[row];

		if (col < columnNames.size()) {
			if (col == 0) {
				int place = row + 1;
	
				while (place < getRowCount() && statistics.getCombinedScore() == results[place].getCombinedScore()) {
					place++;
				}
				return getPlacementString(place);
			} else if (col == 1) {
				return statistics.getTeamName();
			} else if (col == 2) {
				String percent = "";

				if (totalScore != 0) {
					percent = " ("
							+ NumberFormat.getPercentInstance().format(statistics.getCombinedScore() / totalScore) + ")";
				}
				return "" + (int) (statistics.getCombinedScore() + 0.5) + percent;
			} else if (col == columnNames.size() - 3) {
				return "" + statistics.getFirsts();
			} else if (col == columnNames.size() - 2) {
				return "" + statistics.getSeconds();
			} else if (col == columnNames.size() - 1) {
				return "" + statistics.getThirds();
			} else {
				return "" + statistics.getScores().get(col - 3);
			}
		}
		return "";
	}

	// Used for printing to the console only
	public void print(PrintStream out) {
		out.println(getTitle());

		for (int col = 1; col < getColumnCount(); col++) {
			out.print(getColumnName(col) + "\t");
		}

		out.println();

		for (int row = 0; row < getRowCount(); row++) {
			out.print(getValueAt(row, 0) + ": ");
			for (int col = 1; col < getColumnCount(); col++) {
				out.print(getValueAt(row, col) + "\t");
			}
			out.println();
		}
	}

	public void saveToFile(String filename, boolean append) {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(filename, append));

			out.println(DateFormat.getDateTimeInstance().format(new Date()));

			out.println(getTitle());

			for (int col = 0; col < getColumnCount(); col++) {
				if (col > 0) {
					out.print(',');
				}
				out.print(getColumnName(col));
			}

			out.println();

			for (int row = 0; row < getRowCount(); row++) {
				for (int col = 0; col < getColumnCount(); col++) {
					if (col > 0) {
						out.print(',');
					}
					out.print(getValueAt(row, col));
				}
				out.println();
			}

			out.println("$");

			out.close();

		} catch (IOException e) {
			Logger.logError(e);
		}
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
