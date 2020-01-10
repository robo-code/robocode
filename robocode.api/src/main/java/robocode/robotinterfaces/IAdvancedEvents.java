/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.robotinterfaces;


import robocode.CustomEvent;
import robocode.SkippedTurnEvent;


/**
 * An event interface for receiving advanced robot events with an
 * {@link IAdvancedRobot}.
 *
 * @see IAdvancedRobot
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6
 */
public interface IAdvancedEvents {

	/**
	 * This method is called if the robot is using too much time between
	 * actions. When this event occur, the robot's turn is skipped, meaning that
	 * it cannot take action anymore in this turn.
	 * <p>
	 * If you receive 30 skipped turn event, your robot will be removed from the
	 * round and loose the round.
	 * <p>
	 * You will only receive this event after taking an action. So a robot in an
	 * infinite loop will not receive any events, and will simply be stopped.
	 * <p>
	 * No correctly working, reasonable robot should ever receive this event
	 * unless it is using too many CPU cycles.
	 *
	 * @param event the skipped turn event set by the game
	 * @see robocode.SkippedTurnEvent
	 * @see robocode.Event
	 */
	void onSkippedTurn(SkippedTurnEvent event);

	/**
	 * This method is called when a custom condition is met.
	 * <p>
	 * See the sample robots for examples of use, e.g. the {@code sample.Target}
	 * robot.
	 *
	 * @param event the custom event that occurred
	 * @see robocode.AdvancedRobot#addCustomEvent
	 * @see robocode.CustomEvent
	 * @see robocode.Event
	 */
	void onCustomEvent(CustomEvent event);
}
