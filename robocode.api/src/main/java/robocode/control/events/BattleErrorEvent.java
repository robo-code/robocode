/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
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
 * A BattleErrorEvent is sent to {@link IBattleListener#onBattleError(BattleErrorEvent)
 * onBattleError()} when an error message is sent from the game in the during the battle. 
 * 
 * @see IBattleListener
 * @see BattleMessageEvent
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public class BattleErrorEvent extends BattleEvent {
	private final String error;

	/**
	 * Called by the game to create a new BattleErrorEvent.
	 * Please don't use this constructor as it might change.
	 *
	 * @param error the error message.
	 */
	public BattleErrorEvent(String error) {
		super();
		this.error = error;
	}

	/**
	 * Returns the error message.
	 *
	 * @return the error message.
	 */
	public String getError() {
		return error;
	}
}
