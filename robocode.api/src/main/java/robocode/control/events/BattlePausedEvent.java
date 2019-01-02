/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.events;


/**
 * A BattlePausedEvent is sent to {@link IBattleListener#onBattlePaused(BattlePausedEvent)
 * onBattlePaused()} when a battle has been paused. 
 *
 * @see IBattleListener
 * @see BattleResumedEvent
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public class BattlePausedEvent extends BattleEvent {

	/**
	 * Called by the game to create a new BattlePausedEvent.
	 * Please don't use this constructor as it might change.
	 */
	public BattlePausedEvent() {
		super();		
	}
}
