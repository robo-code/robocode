/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.events;


import robocode.BattleResults;
import robocode.BattleRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * A BattleCompletedEvent is sent to {@link IBattleListener#onBattleCompleted(BattleCompletedEvent)
 * onBattleCompleted()} when the battle is completed successfully and results are available. This event
 * will not occur if the battle is terminated or aborted by the user before the battle is completed.
 *
 * @see IBattleListener
 * @see IBattleListener#onBattleCompleted(BattleCompletedEvent)
 * @see BattleStartedEvent
 * @see BattleFinishedEvent
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public class BattleCompletedEvent extends BattleEvent {
	private final BattleRules battleRules;
	private final BattleResults[] results;

	/**
	 * Called by the game to create a new BattleCompletedEvent.
	 * Please don't use this constructor as it might change.
	 *
	 * @param battleRules the rules that was used in the battle.
	 * @param results the indexed results of the battle. These are unsorted, but using robot indexes.
	 */
	public BattleCompletedEvent(BattleRules battleRules, BattleResults[] results) {
		super();
		this.battleRules = battleRules;
		this.results = results;
	}

	/**
	 * Returns the rules used in the battle.
	 *
	 * @return the rules used in the battle.
	 */
	public BattleRules getBattleRules() {
		return battleRules;
	}

	/**
	 * Returns the battle results sorted on score.
	 * Note that the robot index cannot be used to determine the score with the sorted results.
	 *
	 * @return an array of sorted BattleResults, where the results with the bigger score are placed first in the list.
	 * @see #getIndexedResults()
	 */
	public BattleResults[] getSortedResults() {
		List<BattleResults> copy = new ArrayList<BattleResults>(Arrays.asList(results));

		Collections.sort(copy);
		Collections.reverse(copy);
		return copy.toArray(new BattleResults[copy.size()]);
	}

	/**
	 * Returns the battle results that can be used to determine the score for the individual robot based
	 * on the robot index.
	 *
	 * @return an array of indexed BattleResults, where each index matches an index of a specific robot.
	 * @see #getSortedResults()
	 */
	public BattleResults[] getIndexedResults() {
		BattleResults[] copy = new BattleResults[results.length];

		System.arraycopy(results, 0, copy, 0, results.length);	
		return copy;
	}
}
