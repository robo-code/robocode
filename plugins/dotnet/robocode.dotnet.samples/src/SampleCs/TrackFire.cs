/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Drawing;
using Robocode;
using Robocode.Util;

namespace SampleCs
{
    /// <summary>
    ///   TrackFire - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
    ///   <p />
    ///   Sits still.  Tracks and fires at the nearest robot it sees
    /// </summary>
    public class TrackFire : Robot
    {
        /// <summary>
        ///   TrackFire's run method
        /// </summary>
        public override void Run()
        {
            // Set colors
            BodyColor = (Color.Pink);
            GunColor = (Color.Pink);
            RadarColor = (Color.Pink);
            ScanColor = (Color.Pink);
            BulletColor = (Color.Pink);

            // Loop forever
            while (true)
            {
                TurnGunRight(10); // Scans automatically
            }
        }

        /// <summary>
        ///   onScannedRobot:  We have a target.  Go get it.
        /// </summary>
        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            // Calculate exact location of the robot
            double absoluteBearing = Heading + e.Bearing;
            double bearingFromGun = Utils.NormalRelativeAngleDegrees(absoluteBearing - GunHeading);

            // If it's close enough, fire!
            if (Math.Abs(bearingFromGun) <= 3)
            {
                TurnGunRight(bearingFromGun);
                // We check gun heat here, because calling Fire()
                // uses a turn, which could cause us to lose track
                // of the other robot.
                if (GunHeat == 0)
                {
                    Fire(Math.Min(3 - Math.Abs(bearingFromGun), Energy - .1));
                }
            }
            else
            {
                // otherwise just set the gun to turn.
                // Note:  This will have no effect until we call scan()
                TurnGunRight(bearingFromGun);
            }
            // Generates another scan event if we see a robot.
            // We only need to call this if the gun (and therefore radar)
            // are not turning.  Otherwise, scan is called automatically.
            if (bearingFromGun == 0)
            {
                Scan();
            }
        }

        public override void OnWin(WinEvent e)
        {
            // Victory dance
            TurnRight(36000);
        }
    }
}