/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Javadocs
 *******************************************************************************/
package robocode.control.snapshot;


/**
 * Interface of a battle turn snapshot at a specific time instant in a battle.
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
	 * Returns a list of snapshots for the objects that are currently on the battlefield
	 * 
	 * @return a list of snapshots for the objects that are currently on the battlefield
	 */
	IRobjectSnapshot[] getRobjects();
	
	/**
	 * Returns the current TPS (turns per second).
	 *
	 * @return the current TPS (turns per second).
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
	 * Returns a list of sorted scores grouped by teams, ordered by position.
	 *
	 * @return a list of sorted scores grouped by teams, ordered by position.
	 */
	IScoreSnapshot[] getSortedTeamScores();

	/**
	 * Returns a list of indexed scores grouped by teams, i.e. unordered.
	 *
	 * @return a list of indexed scores grouped by teams, i.e. unordered.
	 */
	IScoreSnapshot[] getIndexedTeamScores();
}
