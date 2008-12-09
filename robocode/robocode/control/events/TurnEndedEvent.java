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
package robocode.control.events;


import robocode.control.snapshot.ITurnSnapshot;


/**
 * @author Pavel Savara (original)
 * @since 1.6.1
 */
public class TurnEndedEvent extends BattleEvent {
	private final ITurnSnapshot turnSnapshot;

	public TurnEndedEvent(ITurnSnapshot turnSnapshot) {
		this.turnSnapshot = turnSnapshot;
	}

	public ITurnSnapshot getTurnSnapshot() {
		return turnSnapshot;
	}
}
