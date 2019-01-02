/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.snapshot;


/**
 * Interface of a score snapshot at a specific time in a battle.
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public interface IScoreSnapshot extends Comparable<Object> {

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
	double getTotalScore();

	/**
	 * Returns the total survival score.
	 *
	 * @return the total survival score.
	 */
	double getTotalSurvivalScore();

	/**
	 * Returns the total last survivor score.
	 *
	 * @return the total last survivor score.
	 */
	double getTotalLastSurvivorBonus();

	/**
	 * Returns the total bullet damage score.
	 *
	 * @return the total bullet damage score.
	 */
	double getTotalBulletDamageScore();

	/**
	 * Returns the total bullet kill bonus.
	 *
	 * @return the total bullet kill bonus.
	 */
	double getTotalBulletKillBonus();

	/**
	 * Returns the total ramming damage score.
	 *
	 * @return the total ramming damage score.
	 */
	double getTotalRammingDamageScore();

	/**
	 * Returns the total ramming kill bonus.
	 *
	 * @return the total ramming kill bonus.
	 */
	double getTotalRammingKillBonus();

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
	double getCurrentScore();

	/**
	 * Returns the current survival score.
	 *
	 * @return the current survival score.
	 */
	double getCurrentSurvivalScore();

	/**
	 * Returns the current survival bonus.
	 *
	 * @return the current survival bonus.
	 */
	double getCurrentSurvivalBonus();

	/**
	 * Returns the current bullet damage score.
	 *
	 * @return the current bullet damage score.
	 */
	double getCurrentBulletDamageScore();

	/**
	 * Returns the current bullet kill bonus.
	 *
	 * @return the current bullet kill bonus.
	 */
	double getCurrentBulletKillBonus();

	/**
	 * Returns the current ramming damage score.
	 *
	 * @return the current ramming damage score.
	 */
	double getCurrentRammingDamageScore();

	/**
	 * Returns the current ramming kill bonus.
	 *
	 * @return the current ramming kill bonus.
	 */
	double getCurrentRammingKillBonus();
}
