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
    ///   Corners - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
    ///   <p />
    ///   This robot moves to a corner, then swings the gun back and forth.
    ///   If it dies, it tries a new corner in the next round.
    /// </summary>
    public class Corners : Robot
    {
        private int others; // Number of other robots in the game
        private static int corner; // Which corner we are currently using
        // static so that it keeps it between rounds.
        private bool stopWhenSeeRobot; // See goCorner()

        /// <summary>
        ///   run:  Corners' main run function.
        /// </summary>
        public override void Run()
        {
            // Set colors
            BodyColor = (Color.Red);
            GunColor = (Color.Black);
            RadarColor = (Color.Yellow);
            BulletColor = (Color.Green);
            ScanColor = (Color.Green);

            // Save # of other bots
            others = Others;

            // Move to a corner
            goCorner();

            // Initialize gun turn speed to 3
            int gunIncrement = 3;

            // Spin gun back and forth
            while (true)
            {
                for (int i = 0; i < 30; i++)
                {
                    TurnGunLeft(gunIncrement);
                }
                gunIncrement *= -1;
            }
        }

        /// <summary>
        ///   goCorner:  A very inefficient way to get to a corner.  Can you do better?
        /// </summary>
        public void goCorner()
        {
            // We don't want to stop when we're just turning...
            stopWhenSeeRobot = false;
            // turn to face the wall to the "right" of our desired corner.
            TurnRight(Utils.NormalRelativeAngleDegrees(corner - Heading));
            // Ok, now we don't want to crash into any robot in our way...
            stopWhenSeeRobot = true;
            // Move to that wall
            Ahead(5000);
            // Turn to face the corner
            TurnLeft(90);
            // Move to the corner
            Ahead(5000);
            // Turn gun to starting point
            TurnGunLeft(90);
        }

        /// <summary>
        ///   onScannedRobot:  Stop and Fire!
        /// </summary>
        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            // Should we stop, or just Fire?
            if (stopWhenSeeRobot)
            {
                // Stop everything!  You can safely call stop multiple times.
                Stop();
                // Call our custom firing method
                smartFire(e.Distance);
                // Look for another robot.
                // NOTE:  If you call scan() inside onScannedRobot, and it sees a robot,
                // the game will interrupt the event handler and start it over
                Scan();
                // We won't get here if we saw another robot.
                // Okay, we didn't see another robot... start moving or turning again.
                Resume();
            }
            else
            {
                smartFire(e.Distance);
            }
        }

        /// <summary>
        ///   smartFire:  Custom Fire method that determines firepower based on distance.
        /// </summary>
        /// <param name="robotDistance">
        ///   the distance to the robot to Fire at
        /// </param>
        public void smartFire(double robotDistance)
        {
            if (robotDistance > 200 || Energy < 15)
            {
                Fire(1);
            }
            else if (robotDistance > 50)
            {
                Fire(2);
            }
            else
            {
                Fire(3);
            }
        }

        /// <summary>
        ///   onDeath:  We died.  Decide whether to try a different corner next game.
        /// </summary>
        public override void OnDeath(DeathEvent e)
        {
            // Well, others should never be 0, but better safe than sorry.
            if (others == 0)
            {
                return;
            }

            // If 75% of the robots are still alive when we die, we'll switch corners.
            if ((others - Others)/(double) others < .75)
            {
                corner += 90;
                if (corner == 270)
                {
                    corner = -90;
                }
                Out.WriteLine("I died and did poorly... switching corner to " + corner);
            }
            else
            {
                Out.WriteLine("I died but did well.  I will still use corner " + corner);
            }
        }
    }
}