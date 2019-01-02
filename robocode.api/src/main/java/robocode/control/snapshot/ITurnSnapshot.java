/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.snapshot;


/**
 * Interface of a battle turn snapshot at a specific time in a battle.
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public interface ITurnSnapshot {

	/**
	 * Returns a list of snapshots for the robots participating in the battle. 
	 *
	 * @return a list of snapshots for the robots participating in the battle. 
	 */
	IRobotSnapshot[] getRobots();

	/**
	 * Returns a list of snapshots for the bullets that are currently on the battlefield.
	 *
	 * @return a list of snapshots for the bullets that are currently on the battlefield.
	 */
	IBulletSnapshot[] getBullets();

	/**
	 * Returns the current TPS (turns per second) rate.
	 *
	 * @return the current TPS (turns per second) rate.
	 */
	int getTPS();

	/**
	 * Returns the current round of the battle.
	 *
	 * @return the current round of the battle.
	 */
	int getRound();

	/**
	 * Returns the current turn in the battle round.
	 *
	 * @return the current turn in the battle round.
	 */
	int getTurn();

	/**
	 * Returns an array of sorted scores grouped by teams, ordered by position.
	 * Note that the team index cannot be used to determine the score with the sorted scores.
	 *
	 * @return an array of sorted IScoreSnapshots, where the bigger scores are placed first in the list.
	 * 
	 * @see #getIndexedTeamScores()
	 */
	IScoreSnapshot[] getSortedTeamScores();

	/**
	 * Returns an array of indexed scores grouped by teams that can be used to determine the score
	 * for the individual team based on the team index.
	 *
	 * @return an array of indexed IScoreSnapshots, where each index matches an index of a specific team.
	 * 
	 * @see #getSortedTeamScores()
	 */
	IScoreSnapshot[] getIndexedTeamScores();
}
