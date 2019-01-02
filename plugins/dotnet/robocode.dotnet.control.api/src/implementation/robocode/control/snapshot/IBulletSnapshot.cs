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
    /// Interface of a bullet snapshot at a specific time in a battle.
    /// </summary>
    public interface IBulletSnapshot
    {
        /// <summary>
        /// Contains the bullet state.
        /// </summary>
        /// <value>
        /// The bullet state.
        /// </value>
        BulletState State { get; }

        /// <summary>
        /// Contains the bullet power.
        /// </summary>
        /// <value>
        /// The bullet power.
        /// </value>
        double Power { get; }

        /// <summary>
        /// Contains the X position of the bullet.
        /// </summary>
        /// <value>
        /// The X position of the bullet.
        /// </value>
        double X { get; }

        /// <summary>
        /// Contains the Y position of the bullet.
        /// </summary>
        /// <value>
        /// The Y position of the bullet.
        /// </value>
        double Y { get; }

        /// <summary>
        /// Contains the X painting position of the bullet.
        /// Note that this is not necessarily equal to the X position of the bullet, even though
        /// it will be in most cases. The painting position of the bullet is needed as the bullet
        /// will "stick" to its victim when it has been hit, but only visually. 
        /// </summary>
        /// <value>
        /// The X painting position of the bullet.
        /// </value>
        double PaintX { get; }

        /// <summary>
        /// Contains the Y painting position of the bullet.
        /// Note that this is not necessarily equal to the Y position of the bullet, even though
        /// it will be in most cases. The painting position of the bullet is needed as the bullet
        /// will "stick" to its victim when it has been hit, but only visually. 
        /// </summary>
        /// <value>
        /// The Y painting position of the bullet.
        /// </value>
        double PaintY { get; }

        /// <summary>
        /// Contains the color of the bullet.
        /// </summary>
        /// <value>
        /// An ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
        /// </value>
        int Color { get; }

        /// <summary>
        /// Contains the current frame number to display, i.e. when the bullet explodes.
        /// </summary>
        /// <value>
        /// The current frame number.
        /// </value>
        /// <seealso cref="IsExplosion"/>
        /// <seealso cref="ExplosionImageIndex"/>
        int Frame { get; }

        /// <summary>
        /// Flag specifying if the bullet has become an explosion.
        /// </summary>
        /// <value>
        /// <em>true</em> if the bullet is an explosion; <em>false</em> otherwise.
        /// </value>
        /// <seealso cref="Frame"/>
        /// <seealso cref="ExplosionImageIndex"/>
        bool IsExplosion { get; }

        /// <summary>
        /// Contains the explosion image index, which is different depending on the type of explosion.
        /// E.g. if it is a small explosion on a robot that has been hit by this bullet,
        /// or a big explosion when a robot dies.
        /// </summary>
        /// <value>
        /// The explosion image index.
        /// </value>
        /// <seealso cref="Frame"/>
        /// <seealso cref="IsExplosion"/>
        int ExplosionImageIndex { get; }

        /// <summary>
        /// Contains the ID of the bullet used for identifying the bullet in a collection of bullets.
        /// </summary>
        /// <value>
        /// The ID of the bullet.
        /// </value>
        int BulletId { get; }
    }
}
