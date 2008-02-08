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
 *******************************************************************************/
package robocode.robotinterfaces;


import robocode.SkippedTurnEvent;
import robocode.CustomEvent;


/**
 * @author Pavel Savara (original)
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
	 * No correctly working, reasonable robot should ever receive this event.
	 *
	 * @param event the skipped turn event set by the game
	 *
	 * @see robocode.SkippedTurnEvent
	 * @see robocode.Event
	 */
	void onSkippedTurn(SkippedTurnEvent event);
}
