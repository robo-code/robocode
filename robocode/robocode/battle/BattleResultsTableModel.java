/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.battle;

import robocode.peer.*;
import robocode.util.*;

/**
 * Insert the type's description here.
 * Creation date: (10/30/2001 4:21:31 PM)
 * @author: Administrator
 */
public class BattleResultsTableModel extends javax.swing.table.AbstractTableModel {
	Battle battle = null;
	ContestantPeerVector orderedContestants = null;
	String title = null;
	
/**
 * BattleResultsTableModel constructor comment.
 */
public BattleResultsTableModel(Battle battle) {
	super();
	this.battle = battle;
	orderedContestants = (ContestantPeerVector)battle.getContestants().clone();
	orderedContestants.sort();
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return 11; 
}
		public String getColumnName(int col) {
			switch (col) {
				case 0: return "Robot Name";
				case 1: return "Total Score";
				case 2: return "Survival";
				case 3: return "Last Survivor Bonus";
				case 4: return "Bullet Dmg";
				case 5: return "Bonus";
				case 6: return "Ram Dmg * 2";
				case 7: return "Bonus";
				case 8: return "Survival 1sts";
				case 9: return "Survival 2nds";
				case 10: return "Survival 3rds";
//				case 10: return "Total\nbullet\ndamage\ndealt";
//				case 11: return "Total ramming damage dealt";
//				case 12: return "Total bullet damage received";
//				case 13: return "Total ramming damage received";
				default: return "";
			}
		}
/**
 * getRowCount method comment.
 */
public int getRowCount() {
	return orderedContestants.size();
}
/**
 * getColumnCount method comment.
 */
public String getTitle() {
	if (title == null)
	{
		//if (battle.isDeterministic())
			title="Results for " + battle.getRoundNum() + " rounds";
		//else
		//	title="Results for " + battle.getNumRounds() + " rounds (Not deterministic - " + battle.getNonDeterministicRobots() + " skipped turns.)";
	}
	return title;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) {
			ContestantPeer r = orderedContestants.elementAt(row);
			switch (col) {
				case 0: 
				{
					int place = row + 1;
					int checkRow = row + 1;
					while (checkRow < getRowCount() && r.getStatistics().getTotalScore() == ((ContestantPeer)orderedContestants.elementAt(checkRow)).getStatistics().getTotalScore())
					{
						place++;
						checkRow++;
					}
					if (r instanceof TeamPeer)
						return Utils.getPlacementString(place) + ": Team: " + r.getName(); //getRobotClassManager().getUniqueFullClassNameWithVersion();
					else
						return Utils.getPlacementString(place) + ": " + r.getName(); //getRobotClassManager().getUniqueFullClassNameWithVersion();
				}
				case 1:
				{
					return "" + (int)(r.getStatistics().getTotalScore());
				}
				case 2:
				{
					return "" + (int)(r.getStatistics().getTotalSurvivalScore());
				}
				case 3:
				{
					return "" + (int)(r.getStatistics().getTotalWinnerScore());
				}
				case 4:
				{
					return "" + (int)(r.getStatistics().getTotalBulletDamageScore());
				}
				case 5:
				{
					return "" + (int)(r.getStatistics().getTotalKilledEnemyBulletScore());
				}
				case 6:
				{
					return "" + (int)(r.getStatistics().getTotalRammingDamageScore());
				}
				case 7:
				{
					return "" + (int)(r.getStatistics().getTotalKilledEnemyRammingScore());
				}
				case 8:
				{
					return "" + (int)(r.getStatistics().getTotalFirsts());
				}
				case 9:
				{
					return "" + (int)(r.getStatistics().getTotalSeconds());
				}
				case 10:
				{
					return "" + (int)(r.getStatistics().getTotalThirds());
				}
/*				case 10:
				{
					return "" + (int)(r.getStatistics().getTotalBulletDamageDealt());
				}
				case 11:
				{
					return "" + (int)(r.getStatistics().getTotalRammingDamageDealt());
				}
				case 12:
				{
					return "" + (int)(r.getStatistics().getTotalBulletDamageReceived());
				}
				case 13:
				{
					return "" + (int)(r.getStatistics().getTotalRammingDamageReceived());
				}*/
				default:
					return "";
			}
		}
/**
 * Insert the method's description here.
 * Creation date: (10/30/2001 4:28:21 PM)
 * @param out java.io.PrintStream
 */
public void print(java.io.PrintStream out) {
	out.println(getTitle());
	for (int col = 0; col < getColumnCount(); col++)
	{
		out.print(getColumnName(col) + "\t");
	}
	out.println();
	for (int row = 0; row < getRowCount(); row++)
	{
		for (int col = 0; col < getColumnCount(); col++)
		{
			out.print(getValueAt(row,col) + "\t");
		}
		out.println();
	}
}
}
