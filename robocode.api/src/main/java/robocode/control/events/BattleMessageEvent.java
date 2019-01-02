/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.control.events;


/**
 * A BattleMessageEvent is sent to {@link IBattleListener#onBattleMessage(BattleMessageEvent)
 * onBattleMessage()} when an informal message is sent from the game in the during the battle. 
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
	 * Called by the game to create a new BattleMessageEvent.
	 * Please don't use this constructor as it might change.
	 *
	 * @param message the informal message from the game.
	 */
	public BattleMessageEvent(String message) {
		super();
		this.message = message;
	}

	/**
	 * Returns the informal message.
	 *
	 * @return the informal message that was sent from the game during the battle.
	 */
	public String getMessage() {
		return message;
	}
}
