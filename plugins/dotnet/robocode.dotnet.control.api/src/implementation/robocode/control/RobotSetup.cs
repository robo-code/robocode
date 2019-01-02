/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;

namespace Robocode.Control
{
    /// <summary>
    /// Contains the initial position and heading for a robot.
    /// </summary>
    [Serializable]
    public class RobotSetup
    {
        private readonly Double x;
        private readonly Double y;
        private readonly Double heading;

        /// <summary>
        /// Constructs a new RobotSetup.
        /// </summary>
        /// <param name="x">The x coordinate, where <code>null</code> means random.
        /// <param name="y">The y coordinate, where <code>null</code> means random.
        /// <param name="heading">The heading in degrees of the body, gun, and radar, where <code>null</code> means random.
        public RobotSetup(Double x, Double y, Double heading)
        {
            this.x = x;
            this.y = y;
            this.heading = heading;
        }

        /// <summary>
        /// Contains the x coordinate.
        /// </summary>
        /// <value>
        /// The x coordinate, where <code>null</code> means unspecified (random).
        /// </value>
        public Double X
        {
            get { return x; }
        }

        /// <summary>
        /// Contains the y coordinate.
        /// </summary>
        /// <value>
        /// The y coordinate, where <code>null</code> means unspecified (random).
        /// </value>
        public Double Y
        {
            get { return y; }
        }

        /// <summary>
        /// Contains the body, gun, and radar heading (in degrees).
        /// </summary>
        /// <value>
        /// The heading (in degrees), where <code>null</code> means unspecified (random).
        /// </value>
        public Double Heading
        {
            get { return heading; }
        }
    }
}