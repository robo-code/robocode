/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.battle;


import net.sf.robocode.io.Logger;
import robocode.BattleResults;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;


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

	// The sum of the scores gathered by the robots.
	private final double totalScore;

	public BattleResultsTableModel(BattleResults[] results, int numRounds) {
		this.results = results;
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
			totalScore += result.getScore();
		}

		return totalScore;
	}

	public int getColumnCount() {
		return 12;
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
			return "Survival";

		case 4:
			return "Surv Bonus";

		case 5:
			return "Bullet Dmg";

		case 6:
			return "Bullet Bonus";

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

		switch (col) {
		case 0: {
			int place = row + 1;

			while (place < getRowCount() && statistics.getScore() == results[place].getScore()) {
				place++;
			}
			return getPlacementString(place);
		}

		case 1:
			return statistics.getTeamLeaderName();

		case 2:
			String percent = "";

			if (totalScore != 0) {
				percent = " (" + NumberFormat.getPercentInstance().format(statistics.getScore() / totalScore) + ")";
			}
			return "" + (int) (statistics.getScore() + 0.5) + percent;

		case 3:
			return "" + (int) (statistics.getSurvival() + 0.5);

		case 4:
			return "" + (int) (statistics.getLastSurvivorBonus() + 0.5);

		case 5:
			return "" + (int) (statistics.getBulletDamage() + 0.5);

		case 6:
			return "" + (int) (statistics.getBulletDamageBonus() + 0.5);

		case 7:
			return "" + (int) (statistics.getRamDamage() + 0.5);

		case 8:
			return "" + (int) (statistics.getRamDamageBonus() + 0.5);

		case 9:
			return "" + statistics.getFirsts();

		case 10:
			return "" + statistics.getSeconds();

		case 11:
			return "" + statistics.getThirds();

		default:
			return "";
		}
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
