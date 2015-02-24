package net.sf.robocode.battle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

import net.sf.robocode.io.Logger;
import robocode.BattleResults;
import robocode.NavalBattleResults;

/**
 * Class that contains a Naval version of BattleResults.
 * @author Thales B.V. / Thomas Hakkers
 * @since 1.9.2.2
 */
@SuppressWarnings("serial")
public class NavalBattleResultsTableModel extends javax.swing.table.AbstractTableModel{
	private String title;
	private final NavalBattleResults[] results;
	private final int numRounds;

	// The sum of the scores gathered by the ships.
	private final double totalScore;

	public NavalBattleResultsTableModel(BattleResults[] results, int numRounds) {
		this.results = new NavalBattleResults[results.length];
		for(int i = 0; i < results.length; ++i){
			this.results[i] = (NavalBattleResults)results[i];
		}
		this.numRounds = numRounds;
		totalScore = countTotalScore();
	}

	/**
	 * Function for counting the sum of the scores gathered by the ships.
	 *
	 * @return The sum.
	 */
	private double countTotalScore() {

		double totalScore = 0;

		for (NavalBattleResults result : results) {
			totalScore += result.getScore();
		}

		return totalScore;
	}

	public int getColumnCount() {
		return 14;
	}

	@Override
	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return "Rank";

		case 1:
			return "Ship Name";

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
			return "Mine Damage";
			
		case 10:
			return "Mine Bonus";
			
		case 11:
			return " 1sts ";

		case 12:
			return " 2nds ";

		case 13:
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

		NavalBattleResults statistics = results[row];

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
			return "" + (int) (statistics.getMineDamage() + 0.5);
			
		case 10:
			return "" + (int) (statistics.getMineDamageBonus() + 0.5);
			
		case 11:
			return "" + statistics.getFirsts();

		case 12:
			return "" + statistics.getSeconds();

		case 13:
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
