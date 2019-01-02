/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.events;


import robocode.control.snapshot.ITurnSnapshot;


/**
 * A TurnEndedEvent is sent to {@link IBattleListener#onTurnEnded(TurnEndedEvent)
 * onTurnEnded()} when the current turn in a battle round is ended. 
 *
 * @see IBattleListener
 * @see TurnStartedEvent
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public class TurnEndedEvent extends BattleEvent {
	private final ITurnSnapshot turnSnapshot;

	/**
	 * Called by the game to create a new TurnEndedEvent.
	 * Please don't use this constructor as it might change.
	 *
	 * @param turnSnapshot a snapshot of the turn that has ended.
	 */
	public TurnEndedEvent(ITurnSnapshot turnSnapshot) {
		super();
		this.turnSnapshot = turnSnapshot;
	}

	/**
	 * Returns a snapshot of the turn that has ended.
	 *
	 * @return a snapshot of the turn that has ended.
	 */
	public ITurnSnapshot getTurnSnapshot() {
		return turnSnapshot;
	}
}
