/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
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
 *******************************************************************************/
package robocode.battle;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import robocode.manager.RobocodeManager;
import robocode.peer.ContestantPeer;
import robocode.peer.ContestantStatistics;
import robocode.peer.TeamPeer;
import robocode.text.StringUtil;


/**
 * This table model extracts the robot ranking from the current battle,
 * in order to be displayed by the RankingDialog.
 *
 * @author Luis Crespo (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
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
		return 12;
	}

	public int getRowCount() {
		List<ContestantPeer> contestants = getContestants();

		return (contestants != null) ? contestants.size() : 0;
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
			return "     Survival     ";

		case 4:
			return "Surv Bonus";

		case 5:
			return "    Bullet Dmg    ";

		case 6:
			return " Bullet Bonus ";

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

	public Object getValueAt(int row, int col) {
		List<ContestantPeer> contestants = new ArrayList<ContestantPeer>(getContestants());

		Collections.sort(contestants);

		ContestantPeer r = contestants.get(row);
		ContestantStatistics statistics = r.getStatistics();

		switch (col) {
		case 0:
			return StringUtil.getPlacementString(row + 1);

		case 1:
			return ((r instanceof TeamPeer) ? "Team: " : "") + r.getName();

		case 2: {
			double current = battle.isRunning() ? statistics.getCurrentScore() : 0;

			return (int) (current + 0.5) + " / " + (int) (statistics.getTotalScore() + current + 0.5);
		}
		case 3: {
			double current = battle.isRunning() ? statistics.getCurrentSurvivalScore() : 0;

			return (int) (current + 0.5) + " / " + (int) (statistics.getTotalSurvivalScore() + current + 0.5);
		}
		case 4:
			return (int) (statistics.getTotalLastSurvivorBonus() + 0.5);

		case 5: {
			double current = battle.isRunning() ? statistics.getCurrentBulletDamageScore() : 0;

			return (int) (current + 0.5) + " / " + (int) (statistics.getTotalBulletDamageScore() + current + 0.5);
		}
		case 6: {
			double current = battle.isRunning() ? statistics.getCurrentBulletKillBonus() : 0;

			return (int) (current + 0.5) + " / " + (int) (statistics.getTotalBulletKillBonus() + current + 0.5);
		}
		case 7: {
			double current = battle.isRunning() ? statistics.getCurrentRammingDamageScore() : 0;

			return (int) (current + 0.5) + " / " + (int) (statistics.getTotalRammingDamageScore() + current + 0.5);
		}
		case 8: {
			double current = battle.isRunning() ? statistics.getCurrentRammingKillBonus() : 0;

			return (int) (current + 0.5) + " / " + (int) (statistics.getTotalRammingKillBonus() + current + 0.5);
		}
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

	private List<ContestantPeer> getContestants() {
		if (manager == null) {
			return null;
		}
		battle = manager.getBattleManager().getBattle();

		return (battle != null) ? battle.getContestants() : null;
	}
}
