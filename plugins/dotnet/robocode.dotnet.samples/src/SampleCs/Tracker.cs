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
    ///   Tracker - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
    ///   <p />
    ///   Locks onto a robot, moves close, fires when close.
    /// </summary>
    public class Tracker : Robot
    {
        private int count; // Keeps track of how long we've
        // been searching for our target
        private double gunTurnAmt; // How much to turn our gun when searching
        private String trackName; // Name of the robot we're currently tracking

        /// <summary>
        ///   run:  Tracker's main run function
        /// </summary>
        public override void Run()
        {
            // Set colors
            BodyColor = (Color.FromArgb(128, 128, 50));
            GunColor = (Color.FromArgb(50, 50, 20));
            RadarColor = (Color.FromArgb(200, 200, 70));
            ScanColor = (Color.White);
            BulletColor = (Color.Blue);

            // Prepare gun
            trackName = null; // Initialize to not tracking anyone
            IsAdjustGunForRobotTurn = (true); // Keep the gun still when we turn
            gunTurnAmt = 10; // Initialize gunTurn to 10

            // Loop forever
            while (true)
            {
                // turn the Gun (looks for enemy)
                TurnGunRight(gunTurnAmt);
                // Keep track of how long we've been looking
                count++;
                // If we've haven't seen our target for 2 turns, look left
                if (count > 2)
                {
                    gunTurnAmt = -10;
                }
                // If we still haven't seen our target for 5 turns, look right
                if (count > 5)
                {
                    gunTurnAmt = 10;
                }
                // If we *still* haven't seen our target after 10 turns, find another target
                if (count > 11)
                {
                    trackName = null;
                }
            }
        }

        /// <summary>
        ///   onScannedRobot:  Here's the good stuff
        /// </summary>
        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            // If we have a target, and this isn't it, return immediately
            // so we can get more ScannedRobotEvents.
            if (trackName != null && e.Name != trackName)
            {
                return;
            }

            // If we don't have a target, well, now we do!
            if (trackName == null)
            {
                trackName = e.Name;
                Out.WriteLine("Tracking " + trackName);
            }
            // This is our target.  Reset count (see the run method)
            count = 0;
            // If our target is too far away, turn and move toward it.
            if (e.Distance > 150)
            {
                gunTurnAmt = Utils.NormalRelativeAngleDegrees(e.Bearing + (Heading - RadarHeading));

                TurnGunRight(gunTurnAmt); // Try changing these to setTurnGunRight,
                TurnRight(e.Bearing); // and see how much Tracker improves...
                // (you'll have to make Tracker an AdvancedRobot)
                Ahead(e.Distance - 140);
                return;
            }

            // Our target is close.
            gunTurnAmt = Utils.NormalRelativeAngleDegrees(e.Bearing + (Heading - RadarHeading));
            TurnGunRight(gunTurnAmt);
            Fire(3);

            // Our target is too close!  Back up.
            if (e.Distance < 100)
            {
                if (e.Bearing > -90 && e.Bearing <= 90)
                {
                    Back(40);
                }
                else
                {
                    Ahead(40);
                }
            }
            Scan();
        }

        /// <summary>
        ///   onHitRobot:  Set him as our new target
        /// </summary>
        public override void OnHitRobot(HitRobotEvent e)
        {
            // Only print if he's not already our target.
            if (trackName != null && trackName != e.Name)
            {
                Out.WriteLine("Tracking " + e.Name + " due to collision");
            }
            // Set the target
            trackName = e.Name;
            // Back up a bit.
            // Note:  We won't get scan events while we're doing this!
            // An AdvancedRobot might use setBack(); Execute();
            gunTurnAmt = Utils.NormalRelativeAngleDegrees(e.Bearing + (Heading - RadarHeading));
            TurnGunRight(gunTurnAmt);
            Fire(3);
            Back(50);
        }

        /// <summary>
        ///   onWin:  Do a victory dance
        /// </summary>
        public override void OnWin(WinEvent e)
        {
            for (int i = 0; i < 50; i++)
            {
                TurnRight(30);
                TurnLeft(30);
            }
        }
    }
}