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


/**
 * A RoundEndedEvent is sent to {@link IBattleListener#onRoundEnded(RoundEndedEvent)
 * onRoundEnded()} when the current round of a battle has ended. 
 *
 * @see IBattleListener
 * @see RoundStartedEvent
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public class RoundEndedEvent extends BattleEvent {
	private final int round;
	private final int turns;

	/**
	 * Creates a new RoundEndedEvent.
	 *
	 * @param round the round number that was ended.
	 * @param turns the number of turns that this round reached.
	 */
	public RoundEndedEvent(int round, int turns) {
		super();
		this.round = round;
		this.turns = turns;
	}

	/**
	 * Returns the round number that was ended.
	 *
	 * @return the round number that was ended.
	 */
	public int getRound() {
		return round;
	}

	/**
	 * Returns the number of turns that this round reached.
	 *
	 * @return the number of turns that this round reached.
	 */
	public int getTurns() {
		return turns;
	}
}
