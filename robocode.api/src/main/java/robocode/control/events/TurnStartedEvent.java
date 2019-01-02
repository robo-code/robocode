/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.events;


/**
 * A TurnStartedEvent is sent to {@link IBattleListener#onTurnStarted(TurnStartedEvent)
 * onTurnStarted()} when a new turn in a battle round is started. 
 *
 * @see IBattleListener
 * @see TurnEndedEvent
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public class TurnStartedEvent extends BattleEvent {

	/**
	 * Called by the game to create a new TurnStartedEvent.
	 * Please don't use this constructor as it might change.
	 */
	public TurnStartedEvent() {
		super();		
	}	
}
