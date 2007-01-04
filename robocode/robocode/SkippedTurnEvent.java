/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *******************************************************************************/
package robocode;


/**
 * A SkippedTurnEvent is sent to {@link robocode.AdvancedRobot#onSkippedTurn} when skipping a turn.
 * You must take an action every turn in order to participate in the game.
 * For example,
 * <PRE>
 *   try {Thread.sleep(1000);} catch (InterruptedException e) {}
 * </PRE>
 * will cause many SkippedTurnEvents, because you are not responding to the game.
 * If you receive 30 SkippedTurnEvents, you will be removed from the round.
 *
 * Instead, you should do something such as:
 * <PRE>
 * for (int i = 0; i < 30; i++)
 *   doNothing(); // or perhaps scan();
 * </PRE>
 *
 * This event may also be generated if your are simply doing too much processing between actions.
 *
 * @see robocode.AdvancedRobot#onSkippedTurn
 *
 * @author Mathew A. Nelson
 */
public class SkippedTurnEvent extends Event {

	/**
	 * Called by the game to create a new SkippedTurnEvent.
	 */
	public SkippedTurnEvent() {
		super();
	}
}
