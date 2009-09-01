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


import java.util.List;


/**
 * Interface of a score snapshot at a specific time instant in a battle.
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public interface IScoreSnapshot extends Comparable<IScoreSnapshot> {

	/**
	 * Returns the name of the contestant, i.e. a robot or team.
	 *
	 * @return the name of the contestant, i.e. a robot or team.
	 */
	String getName();

	/**
	 * Returns the total score.
	 *
	 * @return the total score.
	 */
	double getCombinedScore();

	/**
	 * Returns the list of totalled scores.
	 *
	 * @return the list of totalled scores.
	 */
	List<Double> getTotalScores();

	/**
	 * Returns the list of current scores.
	 *
	 * @return the list of current scores.
	 */
	List<Double> getCurrentScores();

	/**
	 * Returns the list of score categories.
	 *
	 * @return the list of score categories.
	 */
	List<String> getScoreNames();
	
	/**
	 * Returns the total number of first places.
	 *
	 * @return the total number of first places.
	 */
	int getTotalFirsts();

	/**
	 * Returns the total number of second places.
	 *
	 * @return the total number of second places.
	 */
	int getTotalSeconds();

	/**
	 * Returns the total number of third places.
	 *
	 * @return the total number of third places.
	 */
	int getTotalThirds();

	/**
	 * Returns the current score.
	 *
	 * @return the current score.
	 */
	double getCurrentCombinedScore();
}
