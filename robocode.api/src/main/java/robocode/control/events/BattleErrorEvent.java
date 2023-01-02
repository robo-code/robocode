/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
	private final Throwable throwable;

	/**
	 * Called by the game to create a new BattleErrorEvent.
	 * Please don't use this constructor as it might change.
	 *
	 * @param error the error message from the game.
	 * @param throwable instance of the error
	 */
	public BattleErrorEvent(String error,Throwable throwable) {
		super();
		this.error = error;
		this.throwable = throwable;
	}

	/**
	 * Returns the error message.
	 *
	 * @return the error message that was sent from the game during the battle.
	 */
	public String getError() {
		return error;
	}

	/**
	 * Returns the error instance when available.
	 *
	 * @return the error instance that was sent from the game during the battle. Could be null.
	 */
	public Throwable getErrorInstance() {
		return throwable;
	}
}
