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
	 * Creates a new BattleCompletedEvent.
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
	 * Returns the rules that was used in the battle.
	 *
	 * @return the rules of the battle.
	 */
	public BattleRules getBattleRules() {
		return battleRules;
	}

	/**
	 * Returns the battle results sorted on score, meaning that robot indexes cannot be used.
	 *
	 * @return a sorted array of BattleResults, where the results with the biggest score are placed first in the list.
	 */
	public BattleResults[] getSortedResults() {
		List<BattleResults> copy = new ArrayList<BattleResults>(Arrays.asList(results));

		Collections.sort(copy);
		Collections.reverse(copy);
		return copy.toArray(new BattleResults[copy.size()]);
	}

	/**
	 * Returns the unsorted battle results so that robot indexes can be used.
	 *
	 * @return an unsorted array of BattleResults, where each index matches an index of a specific robot.
	 */
	public BattleResults[] getIndexedResults() {
		BattleResults[] copy = new BattleResults[results.length];

		System.arraycopy(results, 0, copy, 0, results.length);	
		return copy;
	}
}
