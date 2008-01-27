// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
// - Port from Java version
// *****************************************************************************

using System;
using nrobocode.robot;
using nrobocode;

[assembly: Description("A sample robot\r\n     Moves in a seesaw motion, and spins the gun around at each end\r\n     Moves perpendicular to the direction of a bullet that hits it")]
[assembly: WebPage("http://robocode.sourceforge.net/")]
[assembly: Version("1.4")]
[assembly: SourceIncluded(true)]
[assembly: AuthorName("Flemming N. Larsen, Pavel Savara")]
[assembly: Name(typeof(nsample.MyFirstJuniorRobot))]

namespace nsample
{
    public class MyFirstJuniorRobot : JuniorRobot
    {
        public override void Run()
        {
            SetColors(Color.Green, Color.Black, Color.Blue);

            Out.WriteLine("Cool, we are running !");
            Console.WriteLine("Cool, we are running !");

            // Seesaw forever
            while (true)
            {
                Ahead(100); // Move ahead 100
                TurnGunRight(360); // Spin gun around
                Back(100); // Move back 100
                TurnGunRight(360); // Spin gun around
            }
        }

        public override void OnScannedRobot()
        {
            // Turn gun to point at the scanned robot
            TurnGunTo(ScannedAngle);

            // Fire!
            Fire(1);
        }

        public override void OnHitByBullet()
        {
            Out.WriteLine("Ouch !");

            // Move ahead 100 and in the same time turn left papendicular to the bullet
            TurnAheadLeft(100, 90 - HitByBulletBearing);
        }
    }
}
