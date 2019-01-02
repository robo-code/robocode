/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Snapshot
{
    /// <summary>
    /// Defines a robot state, which can be: active on the battlefield, hitting a wall or robot this turn, or dead.
    /// </summary>
    public enum RobotState
    {
        /// <summary>
        /// The robot is active on the battlefield and has not hit the wall or a robot at this turn.
        /// </summary>
        Active = 0,

        /// <summary>
        /// The robot has hit a wall, i.e. one of the four borders, at this turn. This state only last one turn.
        /// </summary>
        HitWall = 1,

        /// <summary>
        /// The robot has hit another robot at this turn. This state only last one turn.
        /// </summary>
        HitRobot = 2,

        /// <summary>
        /// The robot is dead.
        /// </summary>
        Dead = 3
    }
}
