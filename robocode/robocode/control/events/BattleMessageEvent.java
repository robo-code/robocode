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
 * A BattleMessageEvent is sent to {@link IBattleListener#onBattleMessage(BattleMessageEvent)
 * onBattleMessage()} when an information message is sent from the game in the during the battle. 
 *
 * @see IBattleListener
 * @see BattleErrorEvent
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public class BattleMessageEvent extends BattleEvent {
	private final String message;

	/**
	 * Creates a new BattleMessageEvent.
	 *
	 * @param message the information message.
	 */
	public BattleMessageEvent(String message) {
		super();
		this.message = message;
	}

	/**
	 * Returns the information message.
	 *
	 * @return the information message.
	 */
	public String getMessage() {
		return message;
	}
}
