/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System.Drawing;
using Robocode;

namespace SampleCs
{
    /// <summary>
    ///   RamFire - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
    ///   <p />
    ///   Drives at robots trying to ram them.
    ///   Fires when it hits them.
    /// </summary>
    public class RamFire : Robot
    {
        private int turnDirection = 1; // Clockwise or counterclockwise

        /// <summary>
        ///   run: Spin around looking for a target
        /// </summary>
        public override void Run()
        {
            // Set colors
            BodyColor = (Color.LightGray);
            GunColor = (Color.Gray);
            RadarColor = (Color.DarkGray);

            while (true)
            {
                TurnRight(5*turnDirection);
            }
        }

        /// <summary>
        ///   onScannedRobot:  We have a target.  Go get it.
        /// </summary>
        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            if (e.Bearing >= 0)
            {
                turnDirection = 1;
            }
            else
            {
                turnDirection = -1;
            }

            TurnRight(e.Bearing);
            Ahead(e.Distance + 5);
            Scan(); // Might want to move ahead again!
        }

        /// <summary>
        ///   onHitRobot:  Turn to face robot, fire hard, and ram him again!
        /// </summary>
        public override void OnHitRobot(HitRobotEvent e)
        {
            if (e.Bearing >= 0)
            {
                turnDirection = 1;
            }
            else
            {
                turnDirection = -1;
            }
            TurnRight(e.Bearing);

            // Determine a shot that won't kill the robot...
            // We want to ram him instead for bonus points
            if (e.Energy > 16)
            {
                Fire(3);
            }
            else if (e.Energy > 10)
            {
                Fire(2);
            }
            else if (e.Energy > 4)
            {
                Fire(1);
            }
            else if (e.Energy > 2)
            {
                Fire(.5);
            }
            else if (e.Energy > .4)
            {
                Fire(.1);
            }
            Ahead(40); // Ram him again!
        }
    }
}