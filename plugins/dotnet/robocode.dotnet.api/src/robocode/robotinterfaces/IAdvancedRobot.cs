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
    /// A robot interface for creating a more advanced type of robot like
    /// <see cref="Robocode.AdvancedRobot"/> that is able to handle advanced robot events.
    /// An advanced robot allows non-blocking calls, custom events, get notifications
    /// about skipped turns, and also allow writes to the file system.
    /// <seealso cref="Robocode.AdvancedRobot"/>
    /// <seealso cref="IBasicRobot"/>
    /// <seealso cref="IJuniorRobot"/>
    /// <seealso cref="IInteractiveRobot"/>
    /// <seealso cref="ITeamRobot"/>
    /// </summary>
    public interface IAdvancedRobot : IBasicRobot
    {
        /// <summary>
        /// This method is called by the game to notify this robot about advanced
        /// robot event. Hence, this method must be implemented so it returns your
        /// <see cref="IAdvancedEvents"/> listener.
        /// </summary>
        IAdvancedEvents GetAdvancedEventListener();
    }
}

//doc