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


/**
 * @author Pavel Savara (original)
 * @since 1.6.1
 */
public class RoundEndedEvent extends BattleEvent {
	private final int round;
	private final int turns;

	public RoundEndedEvent(int round, int turns) {
		this.round = round;
		this.turns = turns;
	}

	public int getRound() {
		return round;
	}

	public int getTurns() {
		return turns;
	}
}
