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


import robocode.battle.snapshot.TurnSnapshot;


/**
 * @author Pavel Savara (original)
 * @since 1.6.1
 */
public class RoundStartedEvent extends BattleEvent {
	private final TurnSnapshot start;
	private final int round;

	public RoundStartedEvent(TurnSnapshot start, int round) {
		this.start = start;
		this.round = round;
	}

	public TurnSnapshot getTurnSnapshot() {
		return start;
	}

	public int getRound() {
		return round;
	}
}
