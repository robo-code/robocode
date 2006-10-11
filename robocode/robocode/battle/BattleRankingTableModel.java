/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Luis Crespo
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Removed of the @SuppressWarnings in getSortedContestants()
 *     - Various optimizations
 *******************************************************************************/
package robocode.battle;


import java.util.*;

import javax.swing.table.AbstractTableModel;

import robocode.manager.RobocodeManager;
import robocode.peer.ContestantPeer;
import robocode.peer.RobotPeer;


/**
 * This table model extracts the robot ranking from the current battle,
 * in order to be displayed by the RankingDialog.
 * 
 * @author Luis Crespo
 */
@SuppressWarnings("serial")
public class BattleRankingTableModel extends AbstractTableModel {

	private RobocodeManager manager;
	private Battle battle;

	public BattleRankingTableModel(RobocodeManager manager) {
		super();
		this.manager = manager;
	}

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		Vector<ContestantPeer> contestants = getContestants();

		return (contestants != null) ? contestants.size() : 0;
	}

	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return "Name";

		case 1:
			return "Total";

		case 2:
			return "Current";

		default:
			return "";
		}
	}

	public Object getValueAt(int row, int col) {
		Vector<ContestantPeer> contestants = getSortedContestants();

		if (contestants == null) {
			return "";
		}
		ContestantPeer cp = contestants.elementAt(row);

		switch (col) {
		case 0:
			String name = (cp instanceof RobotPeer) ? ((RobotPeer) cp).getVeryShortName() : cp.getName(); 

			return "" + (row + 1) + ": " + name;

		case 1:
			return (int) cp.getStatistics().getTotalScore();

		case 2:
			return battle.isRunning() ? (int) cp.getStatistics().getCurrentScore() : 0;

		default:
			return "";
		}
	}
	
	private Vector<ContestantPeer> getContestants() {
		if (manager == null) {
			return null;
		}
		battle = manager.getBattleManager().getBattle();
		
		return (battle != null) ? battle.getContestants() : null;
	}

	private Vector<ContestantPeer> getSortedContestants() {
		Vector<ContestantPeer> contestants = getContestants();

		if (contestants == null) {
			return null;
		}

		Vector<ContestantPeer> sc = new Vector<ContestantPeer>(contestants);

		Collections.sort(sc, new Comparator<ContestantPeer>() {
			public int compare(ContestantPeer cp1, ContestantPeer cp2) {
				double score1 = cp1.getStatistics().getTotalScore();
				double score2 = cp2.getStatistics().getTotalScore();

				if (battle.isRunning()) {
					score1 += cp1.getStatistics().getCurrentScore();
					score2 += cp2.getStatistics().getCurrentScore();
				}
				return (int) (score2 - score1);
			}			
		});
		return sc;
	}
}