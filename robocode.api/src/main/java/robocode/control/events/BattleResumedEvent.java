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
 * A BattleResumedEvent is sent to {@link IBattleListener#onBattleResumed(BattleResumedEvent)
 * onBattleResumed()} when the battle has been resumed (after having been paused). 
 *
 * @see IBattleListener
 * @see BattlePausedEvent
 * 
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public class BattleResumedEvent extends BattleEvent {

	/**
	 * Creates a new BattlePausedEvent.
	 */
	public BattleResumedEvent() {
		super();		
	}
}
