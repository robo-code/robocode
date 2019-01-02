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
    /// Defines a bullet state, which can be: just fired, moving somewhere, hitting a victim,
    /// hitting another bullet, hitting the wall, exploded, or inactive.
    /// </summary>
    public enum BulletState
    {
        /// <summary>
        /// The bullet has just been fired this turn and hence just been created.
        /// This state only last one turn.
        /// </summary>
        Fired = 0,

        /// <summary>
        /// The bullet is now moving across the battlefield, but has not hit anything yet.
        /// </summary>
        Moving = 1,

        /// <summary>
        /// The bullet has hit a robot victim.
        /// </summary>
        HitVictim = 2,

        /// <summary>
        /// The bullet has hit another bullet.
        /// </summary>
        HitBullet = 3,

        /// <summary>
        /// The bullet has the wall, i.e. one of the four borders of the battlefield.
        /// </summary>
        HitWall = 4,

        /// <summary>
        /// The bullet currently represents a robot explosion, i.e. a robot death.
        /// </summary>
        Exploded = 5,

        /// <summary>
        /// The bullet is currently inactive. Hence, it is not active or visible on the battlefield.
        /// </summary>
        Inactive = 6
    }
}
