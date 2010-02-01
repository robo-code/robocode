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
    /// A robot interface for creating a team robot like <see cref="robocode.TeamRobot"/>
    /// that is able to receive team events.
    /// A team robot is an advanced type of robot that supports sending messages
    /// between teammates that participates in a team.
    /// <seealso cref="robocode.TeamRobot"/>
    /// <seealso cref="IBasicRobot"/>
    /// <seealso cref="IJuniorRobot"/>
    /// <seealso cref="IInteractiveRobot"/>
    /// <seealso cref="IAdvancedRobot"/>
    /// <seealso cref="ITeamRobot"/>
    /// </summary>
    public interface ITeamRobot : IAdvancedRobot
    {
        /// <summary>
        /// This method is called by the game to notify this robot about team events.
        /// Hence, this method must be implemented so it returns your
        /// <see cref="ITeamEvents"/> listener.
        ///
        /// @return listener to team events or null if this robot should
        ///         not receive the notifications.
        /// </summary>
        ITeamEvents GetTeamEventListener();
    }
}

//doc