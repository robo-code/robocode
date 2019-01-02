/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System.Drawing;
using Robocode;
using Robocode.Util;

namespace SampleCs
{
    /// <summary>
    ///   Fire - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
    ///   Sits still.  Spins gun around.  Moves when hit.
    /// </summary>
    public class Fire : Robot
    {
        private int dist = 50; // distance to move when we're hit

        /// <summary>
        ///   run:  Fire's main run function
        /// </summary>
        public override void Run()
        {
            // Set colors
            BodyColor = (Color.Orange);
            GunColor = (Color.Orange);
            RadarColor = (Color.Red);
            ScanColor = (Color.Red);
            BulletColor = (Color.Red);

            // Spin the gun around slowly... forever
            while (true)
            {
                TurnGunRight(5);
            }
        }

        /// <summary>
        ///   onScannedRobot:  Fire!
        /// </summary>
        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            // If the other robot is close by, and we have plenty of life,
            // Fire hard!
            if (e.Distance < 50 && Energy > 50)
            {
                Fire(3);
            } // otherwise, Fire 1.
            else
            {
                Fire(1);
            }
            // Call scan again, before we turn the gun
            Scan();
        }

        /// <summary>
        ///   onHitByBullet:  Turn perpendicular to the bullet, and move a bit.
        /// </summary>
        public override void OnHitByBullet(HitByBulletEvent e)
        {
            TurnRight(Utils.NormalRelativeAngleDegrees(90 - (Heading - e.Heading)));

            Ahead(dist);
            dist *= -1;
            Scan();
        }

        /// <summary>
        ///   onHitRobot:  Aim at it.  Fire Hard!
        /// </summary>
        public override void OnHitRobot(HitRobotEvent e)
        {
            double turnGunAmt = Utils.NormalRelativeAngleDegrees(e.Bearing + Heading - GunHeading);

            TurnGunRight(turnGunAmt);
            Fire(3);
        }
    }
}