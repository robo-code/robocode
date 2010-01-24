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
namespace robocode.robotinterfaces
{
    /// <summary>
    /// An evnt interface for receiving advanced robot events with an
    /// {@link IAdvancedRobot}.
    ///
    /// @author Pavel Savara (original)
    /// @author Flemming N. Larsen (javadoc)
    /// @see IAdvancedRobot
    /// @since 1.6
    /// </summary>
    public interface IAdvancedEvents
    {
        /// <summary>
        /// This method is called if the robot is using too much time between
        /// actions. When this evnt occur, the robot's turn is skipped, meaning that
        /// it cannot take action anymore in this turn.
        /// <p/>
        /// If you receive 30 skipped turn evnt, your robot will be removed from the
        /// round and loose the round.
        /// <p/>
        /// You will only receive this evnt after taking an action. So a robot in an
        /// infinite loop will not receive any events, and will simply be stopped.
        /// <p/>
        /// No correctly working, reasonable robot should ever receive this event
        /// unless it is using too many CPU cycles.
        ///
        /// @param evnt the skipped turn evnt set by the game
        /// @see robocode.SkippedTurnEvent
        /// @see robocode.Event
        /// </summary>
        void OnSkippedTurn(SkippedTurnEvent evnt);

        /// <summary>
        /// This method is called when a custom condition is met.
        /// <p/>
        /// See the sample robots for examples of use, e.g. the {@code sample.Target}
        /// robot.
        ///
        /// @param evnt the custom evnt that occurred
        /// @see robocode.AdvancedRobot#AddCustomEvent
        /// @see robocode.CustomEvent
        /// @see robocode.Event
        /// </summary>
        void OnCustomEvent(CustomEvent evnt);
    }
}

//happy