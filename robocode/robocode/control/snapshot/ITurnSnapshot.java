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
 *******************************************************************************/
package robocode.control.snapshot;


import java.util.ArrayList;
import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public interface ITurnSnapshot {

	/**
	 * Returns all robots participating in the battle.
	 *
	 * @return a list containing all robots participating in the battle.
	 */
	List<IRobotSnapshot> getRobots();

	/**
	 * Returns all bullets currently the battlefield.
	 *
	 * @return a list containing all bullets currently the battlefield.
	 */
	List<IBulletSnapshot> getBullets();

	/**
	 * Returns the current TPS (turns per second).
	 *
	 * @return the current TPS (turns per second).
	 */
	int getTPS();

	/**
	 * Returns the current turn.
	 *
	 * @return the current turn.
	 */
	int getRound();

	/**
	 * Returns the current turn.
	 *
	 * @return the current turn.
	 */
	int getTurn();

	/**
	 * @return scores grouped by teams, ordered by position
	 */
	List<IScoreSnapshot> getTeamScores();

	/**
	 * @return scores grouped by teams, in stable order
	 */
	ArrayList<IScoreSnapshot> getTeamScoresStable();
}
