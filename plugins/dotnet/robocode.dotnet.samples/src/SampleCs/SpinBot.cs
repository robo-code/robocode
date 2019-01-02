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
    ///   SpinBot - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
    ///   <p />
    ///   Moves in a circle, firing hard when an enemy is detected
    /// </summary>
    public class SpinBot : AdvancedRobot
    {
        /// <summary>
        ///   SpinBot's run method - Circle
        /// </summary>
        public override void Run()
        {
            // Set colors
            BodyColor = (Color.Blue);
            GunColor = (Color.Blue);
            RadarColor = (Color.Black);
            ScanColor = (Color.Yellow);

            // Loop forever
            while (true)
            {
                // Tell the game that when we take move,
                // we'll also want to turn right... a lot.
                SetTurnRight(10000);
                // Limit our speed to 5
                MaxVelocity = 5;
                // Start moving (and turning)
                Ahead(10000);
                // Repeat.
            }
        }

        /// <summary>
        ///   onScannedRobot: Fire hard!
        /// </summary>
        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            Fire(3);
        }

        /// 
        ///<summary>
        ///  onHitRobot:  If it's our fault, we'll stop turning and moving,
        ///  so we need to turn again to keep spinning.
        ///</summary>
        public override void OnHitRobot(HitRobotEvent e)
        {
            if (e.Bearing > -10 && e.Bearing < 10)
            {
                Fire(3);
            }
            if (e.IsMyFault)
            {
                TurnRight(10);
            }
        }
    }
}