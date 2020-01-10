/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
	private final int totalTurns;

	/**
	 * Called by the game to create a new RoundEndedEvent.
	 * Please don't use this constructor as it might change.
	 *
	 * @param round the round number that was ended (zero indexed).
	 * @param turns the number of turns that this round reached.
	 * @param totalTurns the total number of turns reached in the battle when this round ended.
	 */
	public RoundEndedEvent(int round, int turns, int totalTurns) {
		super();
		this.round = round;
		this.turns = turns;
		this.totalTurns = totalTurns;
	}

	/**
	 * Returns the round number that has ended.
	 *
	 * @return the round number that has ended, which is zero indexed.
	 */
	public int getRound() {
		return round;
	}

	/**
	 * Returns the number of turns that this round reached.
	 *
	 * @return the number of turns that this round reached.
	 *
	 * @see #getTotalTurns()
	 */
	public int getTurns() {
		return turns;
	}

	/**
	 * Returns the total number of turns reached in the battle when this round ended. 
	 *
	 * @return the total number of turns reached in the battle when this round ended.
	 *
	 * @see #getTurns()
	 *
	 * @since 1.7.2
	 */
	public int getTotalTurns() {
		return totalTurns;
	}
}
