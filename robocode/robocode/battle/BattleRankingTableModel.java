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
 *     - Integration and minor corrections
 *******************************************************************************/
package robocode.battle;


import java.util.*;

import javax.swing.table.AbstractTableModel;

import robocode.manager.RobocodeManager;
import robocode.peer.ContestantPeer;


/**
 * This table model extracts the robot ranking from the current battle,
 * in order to be displayed by the RankingDialog.
 * 
 * @author Luis Crespo
 */
public class BattleRankingTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
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
		Vector contestants = getContestants();

		if (contestants == null) {
			return 0;
		} else {
			return contestants.size();
		}
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
		ContestantPeer cp = (ContestantPeer) contestants.elementAt(row);

		switch (col) {
		case 0:
			String name = cp.getName(); 

			// TODO: this is a hack and fails in case that version numbers are used
			// A getShortName() method should be added to ContestantPeer instead
			int pos = name.lastIndexOf('.');

			if (pos >= 0 && pos < name.length()) {
				name = name.substring(pos + 1);
			}
			// return Utils.getPlacementString(row+1) + ": " + name;	 --> Discarded: space is very limited
			return "" + (row + 1) + ": " + name;

		case 1:
			return (int) cp.getStatistics().getTotalScore();

		case 2:
			if (!battle.isRunning()) {
				return 0;
			} else {
				return (int) cp.getStatistics().getCurrentScore();
			}

		default:
			return "";
		}
	}
	
	private Vector<ContestantPeer> getContestants() {
		if (manager == null) {
			return null;
		}
		battle = manager.getBattleManager().getBattle();
		if (battle == null) {
			return null;
		}
		return battle.getContestants();
	}

	// TODO: Fix the @SuppressWarnings
	@SuppressWarnings("unchecked")
	private Vector getSortedContestants() {
		Vector contestants = getContestants();

		if (contestants == null) {
			return null;
		}
		Vector sc = new Vector(contestants);

		Collections.sort(sc, new Comparator() {
			public int compare(Object o1, Object o2) {
				double score1, score2;
				ContestantPeer cp1, cp2;

				cp1 = (ContestantPeer) o1;
				cp2 = (ContestantPeer) o2;
				score1 = cp1.getStatistics().getTotalScore();
				score2 = cp2.getStatistics().getTotalScore();
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