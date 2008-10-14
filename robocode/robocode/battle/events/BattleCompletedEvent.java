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


/**
 * @author Pavel Savara (original)
 * @since 1.6.1
 */
public class BattleCompletedEvent extends BattleEvent {
	private final BattleRules battleRules;
	private final BattleResults[] results;

	public BattleCompletedEvent(BattleRules battleRules, BattleResults[] results) {
		this.battleRules = battleRules;
		this.results = results;
	}

	public BattleRules getBattleRules() {
		return battleRules;
	}

	public BattleResults[] getResults() {
		return results;
	}
}
