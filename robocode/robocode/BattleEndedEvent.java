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
package robocode;


/**
 * A BattleEndedEvent is sent to {@link Robot#onBattleEnded(BattleEndedEvent)
 * onBattleEnded()} when the battle is ended.
 * You can use the information contained in this event to determine if the
 * battle was aborted and also get the results of the battle.
 *
 * @author Pavel Savara (original)
 * @see BattleResults
 * @see Robot#onBattleEnded(BattleEndedEvent)
 * @since 1.6.1
 */
public class BattleEndedEvent extends Event {

	private final boolean aborted;
	private final BattleResults results;

	/**
	 * Called by the game to create a new BattleEndedEvent.
	 *
	 * @param aborted {@code true} if the battle was aborted; {@code false} otherwise.
	 * @param results the battle results
	 */
	public BattleEndedEvent(boolean aborted, BattleResults results) {
		this.aborted = aborted;
		this.results = results;
	}

	/**
	 * Checks if this battle was aborted.
	 *
	 * @return {@code true} if the battle was aborted; {@code false} otherwise.
	 */
	public boolean isAborted() {
		return aborted;
	}

	/**
	 * Returns the battle results.
	 *
	 * @return the battle results.
	 */
	public BattleResults getResults() {
		return results;
	}
}
