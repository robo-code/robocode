/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.events;


import robocode.BattleRules;


/**
 * A BattleStartedEvent is sent to {@link IBattleListener#onBattleStarted(BattleStartedEvent)
 * onBattleStarted()} when a new battle is started. 
 *
 * @see IBattleListener
 * @see BattleCompletedEvent
 * @see BattleFinishedEvent
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public class BattleStartedEvent extends BattleEvent {
	private final BattleRules battleRules;
	private final boolean isReplay;
	private final int robotsCount;

	/**
	 * Called by the game to create a new BattleStartedEvent.
	 * Please don't use this constructor as it might change.
	 *
	 * @param battleRules the rules that will be used in the battle.
	 * @param robotsCount the number of robots participating in the battle.
	 * @param isReplay a flag specifying if this battle is a replay or real battle:
	 *                 {@code true} if the battle is a replay; {@code false} otherwise.
	 */
	public BattleStartedEvent(BattleRules battleRules, int robotsCount, boolean isReplay) {
		super();
		this.battleRules = battleRules;
		this.isReplay = isReplay;
		this.robotsCount = robotsCount;
	}

	/**
	 * Returns the rules that will be used in the battle.
	 *
	 * @return the rules that will be used in the battle.
	 */
	public BattleRules getBattleRules() {
		return battleRules;
	}

	/**
	 * Returns the number of robots participating in the battle.
	 * 
	 * @return the number of robots participating in the battle.
	 */
	public int getRobotsCount() {
		return robotsCount;
	}

	/**
	 * Checks if this battle is a replay or a new battle.
	 *
	 * @return {@code true} if the battle is a replay; {@code false} otherwise.
	 */
	public boolean isReplay() {
		return isReplay;
	}
}
