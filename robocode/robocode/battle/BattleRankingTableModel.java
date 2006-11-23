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
 *     - Added independent Rank column
 *     - Various optimizations
 *     - Ported to Java 5
 *******************************************************************************/
package robocode.battle;


import java.util.*;

import javax.swing.table.AbstractTableModel;

import robocode.manager.RobocodeManager;
import robocode.peer.*;
import robocode.util.Utils;


/**
 * This table model extracts the robot ranking from the current battle,
 * in order to be displayed by the RankingDialog.
 * 
 * @author Luis Crespo (original)
 * @author Flemming N. Larsen (current)
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
		return 4;
	}

	public int getRowCount() {
		Vector<ContestantPeer> contestants = getContestants();

		return (contestants != null) ? contestants.size() : 0;
	}

	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return "Rank";

		case 1:
			return "Robot Name";

		case 2:
			return "Total";

		case 3:
			return "Current";

		default:
			return "";
		}
	}

	public Object getValueAt(int row, int col) {
		Vector<ContestantPeer> contestants = new Vector<ContestantPeer>(getContestants());

		Collections.sort(contestants);
		
		if (contestants == null) {
			return "";
		}
		ContestantPeer r = contestants.elementAt(row);

		switch (col) {
		case 0:
			return Utils.getPlacementString(row + 1);

		case 1:
			return ((r instanceof TeamPeer) ? "Team: " : "") + r.getName();

		case 2:
			return (int) (r.getStatistics().getTotalScore()
					+ (battle.isRunning() ? r.getStatistics().getCurrentScore() : 0));

		case 3:
			return battle.isRunning() ? (int) r.getStatistics().getCurrentScore() : 0;

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
}
