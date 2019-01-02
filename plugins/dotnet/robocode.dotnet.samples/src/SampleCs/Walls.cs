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

namespace SampleCs
{
    /// <summary>
    ///   Walls - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
    ///   <p />
    ///   Moves around the outer edge with the gun facing in.
    /// </summary>
    public class Walls : Robot
    {
        private bool peek; // Don't turn if there's a robot there
        private double moveAmount; // How much to move

        /// <summary>
        ///   run: Move around the walls
        /// </summary>
        public override void Run()
        {
            // Set colors
            BodyColor = (Color.Black);
            GunColor = (Color.Black);
            RadarColor = (Color.Orange);
            BulletColor = (Color.Cyan);
            ScanColor = (Color.Cyan);

            // Initialize moveAmount to the maximum possible for this battlefield.
            moveAmount = Math.Max(BattleFieldWidth, BattleFieldHeight);
            // Initialize peek to false
            peek = false;

            // turnLeft to face a wall.
            // getHeading() % 90 means the remainder of
            // getHeading() divided by 90.
            TurnLeft(Heading%90);
            Ahead(moveAmount);
            // Turn the gun to turn right 90 degrees.
            peek = true;
            TurnGunRight(90);
            TurnRight(90);

            while (true)
            {
                // Look before we turn when ahead() completes.
                peek = true;
                // Move up the wall
                Ahead(moveAmount);
                // Don't look now
                peek = false;
                // Turn to the next wall
                TurnRight(90);
            }
        }

        /// <summary>
        ///   onHitRobot:  Move away a bit.
        /// </summary>
        public override void OnHitRobot(HitRobotEvent e)
        {
            // If he's in front of us, set back up a bit.
            if (e.Bearing > -90 && e.Bearing < 90)
            {
                Back(100);
            } // else he's in back of us, so set ahead a bit.
            else
            {
                Ahead(100);
            }
        }

        /// <summary>
        ///   onScannedRobot:  Fire!
        /// </summary>
        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            Fire(2);
            // Note that scan is called automatically when the robot is moving.
            // By calling it manually here, we make sure we generate another scan event if there's a robot on the next
            // wall, so that we do not start moving up it until it's gone.
            if (peek)
            {
                Scan();
            }
        }
    }
}