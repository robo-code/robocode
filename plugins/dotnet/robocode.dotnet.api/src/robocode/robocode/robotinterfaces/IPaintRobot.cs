#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

namespace Robocode.RobotInterfaces
{
    /// <summary>
    /// A robot interface that makes it possible for a robot to receive paint events.
    /// </summary>
    public interface IPaintRobot : IBasicRobot
    {
        /// <summary>
        /// This method is called by the game to notify this robot about painting
        /// events. Hence, this method must be implemented so it returns your
        /// <see cref="IPaintEvents"/> listener.
        /// </summary>
        IPaintEvents GetPaintEventListener();
    }
}

//doc