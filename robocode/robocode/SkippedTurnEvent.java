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
 *     Flemming N. Larsen
 *     - Updated Javadoc
 *******************************************************************************/
package robocode;


/**
 * A SkippedTurnEvent is sent to {@link AdvancedRobot#onSkippedTurn
 * onSkippedTurn} when your robot is forced to skipping a turn.
 * You must take an action every turn in order to participate in the game.
 * For example,
 * <pre>
 *    try {
 *        Thread.sleep(1000);
 *    } catch (InterruptedException e) {}
 * </pre>
 * will cause many SkippedTurnEvents, because you are not responding to the game.
 * If you receive 30 SkippedTurnEvents, you will be removed from the round.
 *
 * Instead, you should do something such as:
 * <pre>
 *     for (int i = 0; i < 30; i++) {
 *         doNothing(); // or perhaps scan();
 *     }
 * </pre>
 *
 * This event may also be generated if you are simply doing too much processing
 * between actions, that is using too much processing power for the calculations
 * etc. in your robot.
 *
 * @see AdvancedRobot#onSkippedTurn
 *
 * @author Mathew A. Nelson (original)
 */
public class SkippedTurnEvent extends Event {

	/**
	 * Called by the game to create a new SkippedTurnEvent.
	 */
	public SkippedTurnEvent() {
		super();
	}
}
