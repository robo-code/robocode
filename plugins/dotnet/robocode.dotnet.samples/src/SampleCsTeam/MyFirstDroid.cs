/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using Robocode;
using Robocode.Util;
using SampleCs;

namespace SampleCsTeam
{
    /// <summary>
    ///   SimpleDroid - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
    ///   <p />
    ///   Follows orders of team leader
    /// </summary>
    public class MyFirstDroid : TeamRobot, IDroid
    {
        /// <summary>
        ///   Droid's default behavior
        /// </summary>
        public override void Run()
        {
            Out.WriteLine("MyFirstDroid ready.");
        }

        /// <summary>
        ///   OnMessageReceived:  What to do when our leader sends a message
        /// </summary>
        public override void OnMessageReceived(MessageEvent e)
        {
            // Fire at a point
            if (e.Message is PointD)
            {
                var p = (PointD)e.Message;
                // Calculate x and y to target
                double dx = p.X - X;
                double dy = p.Y - Y;
                // Calculate angle to target
                double theta = Utils.ToDegrees(Math.Atan2(dx, dy));

                // Turn gun to target
                TurnGunRight(Utils.NormalRelativeAngleDegrees(theta - GunHeading));
                // Fire hard!
                Fire(3);
            }
            else if (e.Message is RobotColors)
            {
                // Set our colors
                var c = (RobotColors) e.Message;

                BodyColor = c.bodyColor;
                GunColor = c.gunColor;
                RadarColor = c.radarColor;
                ScanColor = c.scanColor;
                BulletColor = c.bulletColor;
            }
        }
    }
}