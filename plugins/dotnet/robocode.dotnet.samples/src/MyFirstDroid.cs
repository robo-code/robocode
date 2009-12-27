/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Maintainance
 *     Pavel Savara
 *     - .NET conversion
 *******************************************************************************/
using System;
using System.Drawing;
using robocode;
using robocode.util;

namespace samplecs
{
    /// <summary>
    /// SimpleDroid - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
    /// <p/>
    /// Follows orders of team leader
    /// </summary>
    public class MyFirstDroid : TeamRobot, Droid
    {

        /// <summary>
        /// Droid's default behavior
        /// </summary>
        public override void run()
        {
            output.WriteLine("MyFirstDroid ready.");
        }

        /// <summary>
        /// onMessageReceived:  What to do when our leader sends a message
        /// </summary>
        public override void onMessageReceived(MessageEvent e)
        {
            // Fire at a point
            if (e.getMessage() is PointF)
            {
                var p = (PointF)e.getMessage();
                // Calculate x and y to target
                double dx = p.X - getX();
                double dy = p.Y - getY();
                // Calculate angle to target
                double theta = Utils.toDegrees(Math.Atan2(dx, dy));

                // Turn gun to target
                turnGunRight(Utils.normalRelativeAngleDegrees(theta - getGunHeading()));
                // Fire hard!
                fire(3);
            }
            else if (e.getMessage() is RobotColors)
            {
                // Set our colors
                var c = (RobotColors)e.getMessage();

                setBodyColor(c.bodyColor);
                setGunColor(c.gunColor);
                setRadarColor(c.radarColor);
                setScanColor(c.scanColor);
                setBulletColor(c.bulletColor);
            }
        }
    }
}
