/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using Robocode;

namespace Robocode.RobotInterfaces
{
    /// <summary>
    /// An event interface for receiving advanced robot events with an
    /// <see cref="IAdvancedRobot"/>
    /// <seealso cref="IAdvancedRobot"/>
    /// </summary>
    public interface IAdvancedEvents
    {
        /// <summary>
        /// This method is called if the robot is using too much time between
        /// actions. When this event occur, the robot's turn is skipped, meaning that
        /// it cannot take action anymore in this turn.
        /// <p/>
        /// If you receive 30 skipped turn event, your robot will be removed from the
        /// round and loose the round.
        /// <p/>
        /// You will only receive this event after taking an action. So a robot in an
        /// infinite loop will not receive any events, and will simply be stopped.
        /// <p/>
        /// No correctly working, reasonable robot should ever receive this event
        /// unless it is using too many CPU cycles.
        /// <seealso cref="Robocode.SkippedTurnEvent"/>
        /// <seealso cref="Robocode.Event"/>
        /// </summary>
        /// <param name="evnt">The skipped turn event set by the game</param>
        void OnSkippedTurn(SkippedTurnEvent evnt);

        /// <summary>
        /// This method is called when a custom condition is met.
        /// <p/>
        /// See the sample robots for examples of use, e.g. the Sample.Target
        /// robot.
        /// <seealso cref="Robocode.AdvancedRobot.AddCustomEvent(Condition)"/>
        /// <seealso cref="Robocode.CustomEvent"/>
        /// <seealso cref="Robocode.Event"/>
        /// </summary>
        /// <param name="evnt">The custom event that occurred</param>
        void OnCustomEvent(CustomEvent evnt);
    }
}

//doc