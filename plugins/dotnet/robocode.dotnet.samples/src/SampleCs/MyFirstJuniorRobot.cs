/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using Robocode;

namespace SampleCs
{
    /// <summary>
    ///   MyFirstJuniorRobot - a sample robot by Flemming N. Larsen
    ///   <p />
    ///   Moves in a seesaw motion, and spins the gun around at each end
    ///   when it cannot see any enemy robot. When the robot sees and enemy
    ///   robot, it will immediately turn the gun and Fire at it.
    /// </summary>
    public class MyFirstJuniorRobot : JuniorRobot
    {
        /// <summary>
        ///   MyFirstJuniorRobot's run method - Seesaw as default
        /// </summary>
        public override void Run()
        {
            // Set robot colors
            SetColors(GREEN, BLACK, BLUE);

            // Seesaw forever
            while (true)
            {
                Ahead(100); // Move ahead 100
                TurnGunRight(360); // Spin gun around
                Back(100); // Move back 100
                TurnGunRight(360); // Spin gun around
            }
        }

        /// <summary>
        ///   When we see a robot, turn the gun towards it and Fire
        /// </summary>
        public override void OnScannedRobot()
        {
            // Turn gun to point at the scanned robot
            TurnGunTo(ScannedAngle);

            // Fire!
            Fire(1);
        }

        /// <summary>
        ///   We were hit!  Turn and move perpendicular to the bullet,
        ///   so our seesaw might avoid a future shot.
        /// </summary>
        public override void OnHitByBullet()
        {
            // Move ahead 100 and in the same time turn left papendicular to the bullet
            TurnAheadLeft(100, 90 - HitByBulletBearing);
        }
    }
}