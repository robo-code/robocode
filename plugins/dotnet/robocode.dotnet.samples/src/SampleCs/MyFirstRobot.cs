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
    ///   MyFirstRobot - a sample robot by Mathew Nelson
    ///   <p />
    ///   Moves in a seesaw motion, and spins the gun around at each end
    /// </summary>
    public class MyFirstRobot : Robot
    {
        /// <summary>
        ///   MyFirstRobot's run method - Seesaw
        /// </summary>
        public override void Run()
        {
            while (true)
            {
                Ahead(100); // Move ahead 100
                TurnGunRight(360); // Spin gun around
                Back(100); // Move back 100
                TurnGunRight(360); // Spin gun around
            }
        }

        /// <summary>
        ///   Fire when we see a robot
        /// </summary>
        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            Fire(1);
        }

        /// <summary>
        ///   We were hit!  Turn perpendicular to the bullet,
        ///   so our seesaw might avoid a future shot.
        /// </summary>
        public override void OnHitByBullet(HitByBulletEvent e)
        {
            TurnLeft(90 - e.Bearing);
        }
    }
}