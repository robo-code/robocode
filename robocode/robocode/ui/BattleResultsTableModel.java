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
 *******************************************************************************/
package robocode.ui;


import robocode.io.Logger;
import robocode.peer.ContestantPeer;
import robocode.peer.ContestantStatistics;
import robocode.peer.TeamPeer;
import robocode.text.StringUtil;
import robocode.manager.RobocodeManager;
import robocode.battle.Battle;
import robocode.battle.events.BattleCompletedEvent;
import robocode.BattleResults;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 * @author Nathaniel Troutman (contributor)
 */
@SuppressWarnings("serial")
public class BattleResultsTableModel extends javax.swing.table.AbstractTableModel {
	private String title;
    private final BattleResults[] results;
    private final int numRounds;

    public BattleResultsTableModel(BattleResults[] results, int numRounds) {
		this.results=results;
        this.numRounds=numRounds;
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
			return "Total Score";

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
		if (title == null ) {
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
			return StringUtil.getPlacementString(place);
		}

		case 1:
			return statistics.getTeamLeaderName();

		case 2:
			return "" + (int) (statistics.getScore() + 0.5);

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
}
