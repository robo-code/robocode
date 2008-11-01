/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Javadocs
 *******************************************************************************/
package robocode;


import robocode.robotinterfaces.*;
import robocode.peer.RobotStatics;

import java.awt.*;


/**
 * A SkippedTurnEvent is sent to {@link AdvancedRobot#onSkippedTurn(SkippedTurnEvent)
 * onSkippedTurn()} when your robot is forced to skipping a turn.
 * You must take an action every turn in order to participate in the game.
 * For example,
 * <pre>
 *    try {
 *        Thread.sleep(1000);
 *    } catch (InterruptedException e) {
 *        // Immediately reasserts the exception by interrupting the caller thread
 *        // itself.
 *        Thread.currentThread().interrupt();
 *    }
 * </pre>
 * will cause many SkippedTurnEvents, because you are not responding to the game.
 * If you receive 30 SkippedTurnEvents, you will be removed from the round.
 * <p/>
 * Instead, you should do something such as:
 * <pre>
 *     for (int i = 0; i < 30; i++) {
 *         doNothing(); // or perhaps scan();
 *     }
 * </pre>
 * <p/>
 * This event may also be generated if you are simply doing too much processing
 * between actions, that is using too much processing power for the calculations
 * etc. in your robot.
 *
 * @author Mathew A. Nelson (original)
 * @see AdvancedRobot#onSkippedTurn(SkippedTurnEvent)
 * @see SkippedTurnEvent
 */
public final class SkippedTurnEvent extends Event {

	/**
	 * Called by the game to create a new SkippedTurnEvent.
	 */
	public SkippedTurnEvent() {
		super();
	}

	private static int classPriority = 100; // System event -> cannot be changed!;

	@Override
	protected final int getClassPriorityImpl() {
		return classPriority;
	}

	@Override
	protected void setClassPriorityImpl(int priority) {
		// System event -> cannot be changed!;
		System.out.println("SYSTEM: You may not change the priority of SkippedTurnEvent.  setPriority ignored.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispatch(IBasicRobot robot, RobotStatics statics, Graphics2D graphics) {
		if (statics.isAdvancedRobot()) {
			IAdvancedEvents listener = ((IAdvancedRobot) robot).getAdvancedEventListener();

			if (listener != null) {
				listener.onSkippedTurn(this);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isCriricalEvent() {
		return true;
	}
}
