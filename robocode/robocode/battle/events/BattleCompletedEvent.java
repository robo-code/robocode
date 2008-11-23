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
package robocode.battle.events;


import robocode.BattleResults;
import robocode.BattleRules;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


/**
 * @author Pavel Savara (original)
 * @since 1.6.1
 */
public class BattleCompletedEvent extends BattleEvent {
	private final BattleRules battleRules;
	private final List<BattleResults> results;

	public BattleCompletedEvent(BattleRules battleRules, List<BattleResults> results) {
		this.battleRules = battleRules;
		this.results = results;
	}

	public BattleRules getBattleRules() {
		return battleRules;
	}

	public BattleResults[] getResults() {
		List<BattleResults> cpy = new ArrayList<BattleResults>(results);

		Collections.sort(cpy);
		Collections.reverse(cpy);
		return cpy.toArray(new BattleResults[1]);
	}

	// stable order
	public List<BattleResults> getResultsStable() {
		return results;
	}
}
