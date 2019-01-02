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
    /// A robot interface for creating an interactive type of robot like
    /// <see cref="Robocode.Robot"/> and <see cref="Robocode.AdvancedRobot"/> that is able to
    /// receive interactive events from the keyboard or mouse.
    /// If a robot is directly inherited from this class it will behave as similar to
    /// a <see cref="IBasicRobot"/>. If you need it to behave similar to an
    /// <see cref="IAdvancedRobot"/> or <see cref="ITeamRobot"/>, you should inherit from these
    /// interfaces instead, as these are inherited from this interface.
    /// <seealso cref="Robocode.Robot"/>
    /// <seealso cref="Robocode.AdvancedRobot"/>
    /// <seealso cref="IBasicRobot"/>
    /// <seealso cref="IJuniorRobot"/>
    /// <seealso cref="IAdvancedRobot"/>
    /// <seealso cref="ITeamRobot"/>
    /// </summary>
    public interface IInteractiveRobot : IBasicRobot
    {
        /// <summary>
        /// This method is called by the game to notify this robot about interactive
        /// events, i.e. keyboard and mouse events. Hence, this method must be
        /// implemented so it returns your <see cref="IInteractiveEvents"/> listener.
        /// </summary>
        IInteractiveEvents GetInteractiveEventListener();
    }
}

//doc