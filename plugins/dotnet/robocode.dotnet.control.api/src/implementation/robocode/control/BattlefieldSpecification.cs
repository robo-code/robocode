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

namespace Robocode.Control
{
    /// <summary>
    /// Defines the size of a battlefield, which is a part of the <see cref="BattleSpecification" />.
    /// </summary>
    /// <seealso cref="BattleSpecification(int, BattlefieldSpecification, RobotSpecification[])" />
    /// <seealso cref="BattleSpecification(int, long, double, BattlefieldSpecification, RobotSpecification[])" />
    /// <seealso cref="BattleSpecification.Battlefield" />
    [Serializable]
    public class BattlefieldSpecification
    {
        private readonly int width;
        private readonly int height;

        /// <summary>
        /// Creates a standard 800 x 600 battlefield.
        /// </summary>
        public BattlefieldSpecification()
            : this(800, 600)
        {
        }

        /// <summary>
        /// Creates a battlefield of the specified width and height.
        /// </summary>
        /// <param name="width">The width of the battlefield, where 400 &lt;= width &lt;= 5000.</param>
        /// <param name="height">The height of the battlefield, where 400 &lt;= height &lt;= 5000.</param>
        /// <exception cref="ArgumentException">Thrown when the width or height is &lt; 400 or &gt; 5000.
        /// </exception>
        public BattlefieldSpecification(int width, int height)
        {
            if (width < 400 || width > 5000)
                throw new ArgumentException("width must be: 400 <= width <= 5000");

            if (height < 400 || height > 5000)
                throw new ArgumentException("height must be: 400 <= height <= 5000");

            this.width = width;
            this.height = height;
        }

        /// <summary>
        /// Contains the width of this battlefield.
        /// </summary>
        /// <value>
        /// The width of this battlefield.
        /// </value>
        public int Width
        {
            get { return width; }
        }

        /// <summary>
        /// Contains the height of this battlefield.
        /// </summary>
        /// <value>
        /// The height of this battlefield.
        /// </value>
        public int Height
        {
            get { return height; }
        }
    }
}