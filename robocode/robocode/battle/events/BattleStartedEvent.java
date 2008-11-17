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


import robocode.BattleRules;


/**
 * @author Pavel Savara (original)
 * @since 1.6.1
 */
public class BattleStartedEvent extends BattleEvent {
	private final BattleRules battleRules;
	private final boolean isReplay;
	private final int robotsCount;

	public BattleStartedEvent(BattleRules battleRules, int robotsCount, boolean isReplay) {
		this.battleRules = battleRules;
		this.isReplay = isReplay;
		this.robotsCount = robotsCount;
	}

	public BattleRules getBattleRules() {
		return battleRules;
	}

	public int getRobotsCount() {
		return robotsCount;
	}

	public boolean isReplay() {
		return isReplay;
	}
}
