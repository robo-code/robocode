/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.events;


/**
 * A BattleResumedEvent is sent to {@link IBattleListener#onBattleResumed(BattleResumedEvent)
 * onBattleResumed()} when a battle has been resumed (after having been paused). 
 *
 * @see IBattleListener
 * @see BattlePausedEvent
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public class BattleResumedEvent extends BattleEvent {

	/**
	 * Called by the game to create a new BattleResumedEvent.
	 * Please don't use this constructor as it might change.
	 */
	public BattleResumedEvent() {
		super();		
	}
}
