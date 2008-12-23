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


import robocode.control.snapshot.ITurnSnapshot;


/**
 * A RoundStartedEvent is sent to {@link IBattleListener#onRoundStarted(RoundStartedEvent)
 * onRoundStarted()} when a new round in a battle is started. 
 *
 * @see IBattleListener
 * @see RoundEndedEvent
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public class RoundStartedEvent extends BattleEvent {
	private final ITurnSnapshot startSnapshot;
	private final int round;

	/**
	 * Creates a new RoundStartedEvent.
	 *
	 * @param startSnapshot the start snapshot of the participating robots, initial starting positions etc.
	 * @param round the round number.
	 */
	public RoundStartedEvent(ITurnSnapshot startSnapshot, int round) {
		super();
		this.startSnapshot = startSnapshot;
		this.round = round;
	}

	/**
	 * Returns the start snapshot of the participating robots, initial starting positions etc.
	 *
	 * @return a ITurnSnapshot instance that serves as the start snapshot of the round.
	 */
	public ITurnSnapshot getStartSnapshot() {
		return startSnapshot;
	}

	/**
	 * Returns the round number.
	 *
	 * @return the round number.
	 */
	public int getRound() {
		return round;
	}
}
