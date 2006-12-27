/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
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
 *******************************************************************************/
package robocode.battle;


import java.io.*;
import java.util.*;
import robocode.peer.*;
import robocode.util.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
@SuppressWarnings("serial")
public class BattleResultsTableModel extends javax.swing.table.AbstractTableModel {
	private Battle battle;
	private String title;
	
	public BattleResultsTableModel(Battle battle) {
		super();
		this.battle = battle;
	}

	public int getColumnCount() {
		return 12; 
	}

	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return "Rank";

		case 1:
			return "Robot Name";

		case 2:
			return "Total Score";

		case 3:
			return "Survival";

		case 4:
			return "Last Survivor Bonus";

		case 5:
			return "Bullet Dmg";

		case 6:
			return "Bonus";

		case 7:
			return "Ram Dmg * 2";

		case 8:
			return "Bonus";

		case 9:
			return "Survival 1sts";

		case 10:
			return "Survival 2nds";

		case 11:
			return "Survival 3rds";

		default:
			return "";
		}
	}

	public int getRowCount() {
		return battle.getContestants().size();
	}

	public String getTitle() {
		if (title == null) {
			int round = battle.getRoundNum();

			title = "Results for " + round + " round";
			if (round > 1) {
				title += 's';
			}
		}
		return title;
	}

	public Object getValueAt(int row, int col) {
		Vector<ContestantPeer> orderedContestants = new Vector<ContestantPeer>(battle.getContestants()); 

		Collections.sort(orderedContestants);

		ContestantPeer r = orderedContestants.elementAt(row);
		ContestantStatistics statistics = r.getStatistics(); 

		switch (col) {
		case 0: { 
			int place = row + 1;

			while (place < getRowCount()
					&& statistics.getTotalScore() == orderedContestants.elementAt(place).getStatistics().getTotalScore()) {
				place++;
			}
			return Utils.getPlacementString(place);
		}

		case 1:
			return ((r instanceof TeamPeer) ? "Team: " : "") + r.getName();

		case 2:
			return "" + (int) (statistics.getTotalScore() + 0.5);

		case 3:
			return "" + (int) (statistics.getTotalSurvivalScore() + 0.5);

		case 4:
			return "" + (int) (statistics.getTotalWinnerScore() + 0.5);

		case 5:
			return "" + (int) (statistics.getTotalBulletDamageScore() + 0.5);

		case 6:
			return "" + (int) (statistics.getTotalKilledEnemyBulletScore() + 0.5);

		case 7:
			return "" + (int) (statistics.getTotalRammingDamageScore() + 0.5);

		case 8:
			return "" + (int) (statistics.getTotalKilledEnemyRammingScore() + 0.5);

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

			out.println(java.text.SimpleDateFormat.getDateTimeInstance().format(new Date()));
			
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
			Utils.log(e);
			return;
		}
	}
}
