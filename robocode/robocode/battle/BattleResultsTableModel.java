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
 *     - Ported to Java 5
 *     - Optimized
 *     - Code cleanup
 *******************************************************************************/
package robocode.battle;


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
	private Vector<ContestantPeer> orderedContestants;
	private String title;
	
	public BattleResultsTableModel(Battle battle) {
		super();
		this.battle = battle;
		orderedContestants = new Vector<ContestantPeer>(battle.getContestants()); 
		Collections.sort(orderedContestants);
	}

	public int getColumnCount() {
		return 11; 
	}

	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return "Robot Name";

		case 1:
			return "Total Score";

		case 2:
			return "Survival";

		case 3:
			return "Last Survivor Bonus";

		case 4:
			return "Bullet Dmg";

		case 5:
			return "Bonus";

		case 6:
			return "Ram Dmg * 2";

		case 7:
			return "Bonus";

		case 8:
			return "Survival 1sts";

		case 9:
			return "Survival 2nds";

		case 10:
			return "Survival 3rds";

		default:
			return "";
		}
	}

	public int getRowCount() {
		return orderedContestants.size();
	}

	public String getTitle() {
		if (title == null) {
			title = "Results for " + battle.getRoundNum() + " rounds";
		}
		return title;
	}

	public Object getValueAt(int row, int col) {
		ContestantPeer r = (ContestantPeer) orderedContestants.elementAt(row);

		ContestantStatistics statistics = r.getStatistics(); 
		
		switch (col) {
		case 0: {
			int place = row + 1;

			while (place < getRowCount()
					&& statistics.getTotalScore() == orderedContestants.elementAt(place).getStatistics().getTotalScore()) {
				place++;
			}
			String seperator = (r instanceof TeamPeer) ? ": Team: " : ": ";

			return Utils.getPlacementString(place) + seperator + r.getName();
		}

		case 1: {
			return "" + (int) (statistics.getTotalScore());
		}

		case 2: {
			return "" + (int) (statistics.getTotalSurvivalScore());
		}

		case 3: {
			return "" + (int) (statistics.getTotalWinnerScore());
		}

		case 4: {
			return "" + (int) (statistics.getTotalBulletDamageScore());
		}

		case 5: {
			return "" + (int) (statistics.getTotalKilledEnemyBulletScore());
		}

		case 6: {
			return "" + (int) (statistics.getTotalRammingDamageScore());
		}

		case 7: {
			return "" + (int) (statistics.getTotalKilledEnemyRammingScore());
		}

		case 8: {
			return "" + (int) (statistics.getTotalFirsts());
		}

		case 9: {
			return "" + (int) (statistics.getTotalSeconds());
		}

		case 10: {
			return "" + (int) (statistics.getTotalThirds());
		}

		default:
			return "";
		}
	}

	public void print(java.io.PrintStream out) {
		out.println(getTitle());

		for (int col = 0; col < getColumnCount(); col++) {
			out.print(getColumnName(col) + "\t");
		}

		out.println();

		for (int row = 0; row < getRowCount(); row++) {
			for (int col = 0; col < getColumnCount(); col++) {
				out.print(getValueAt(row, col) + "\t");
			}
			out.println();
		}
	}
}
