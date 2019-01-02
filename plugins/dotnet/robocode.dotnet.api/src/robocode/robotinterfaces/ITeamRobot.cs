/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
namespace Robocode.RobotInterfaces
{
    /// <summary>
    /// A robot interface for creating a team robot like <see cref="Robocode.TeamRobot"/>
    /// that is able to receive team events.
    /// A team robot is an advanced type of robot that supports sending messages
    /// between teammates that participates in a team.
    /// <seealso cref="Robocode.TeamRobot"/>
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
        /// </summary>
        ITeamEvents GetTeamEventListener();
    }
}

//doc