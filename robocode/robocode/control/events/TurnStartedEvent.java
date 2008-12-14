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
 * A TurnStartedEvent is sent to {@link IBattleListener#onTurnStarted(TurnStartedEvent)
 * onTurnStarted()} when a new turn in a battle round is started. 
 *
 * @see IBattleListener
 * @see TurnEndedEvent
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public class TurnStartedEvent extends BattleEvent {

	/**
	 * Creates a new TurnStartedEvent.
	 */
	public TurnStartedEvent() {
		super();		
	}	
}
