/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Globalization;
using Robocode;
using Robocode.Util;

namespace tested.robotscs
{
    /*
     * @author Pavel Savara (original)
     */

    public class WatchBullets : AdvancedRobot
    {
        public override void Run()
        {
            while (true)
            {
                Ahead(100);
                TurnGunRight(360);
                Back(100);
                TurnGunRight(360);
            }
        }

        private Bullet bullet;

        public override void OnStatus(StatusEvent evnt)
        {
            dump();
        }


        public override void OnBulletHit(BulletHitEvent evnt)
        {
            if (bullet != evnt.Bullet)
            {
                Out.WriteLine("Failed bullet identity");
            }
        }


        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            // Calculate exact location of the robot
            double absoluteBearing = Heading + e.Bearing;
            double bearingFromGun = Utils.NormalRelativeAngle(absoluteBearing - GunHeading);

            // If it's close enough, fire!
            if (Math.Abs(bearingFromGun) <= 3)
            {
                TurnGunRight(bearingFromGun);
                // We check gun heat here, because calling Fire()
                // uses a turn, which could cause us to lose track
                // of the other robot.
                if (GunHeat == 0 && bullet == null)
                {
                    Bullet lbullet = FireBullet(Math.Min(3 - Math.Abs(bearingFromGun), Energy - .1));

                    bullet = lbullet;
                }
            } // otherwise just set the gun to turn.
                // Note:  This will have no effect until we call Scan()
            else
            {
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

        private void dump()
        {
            if (bullet != null)
            {
                Out.WriteLine(
                    Time + " " + bullet.X.ToString("F5", CultureInfo.InvariantCulture) + " " + bullet.Y.ToString("F5", CultureInfo.InvariantCulture) + " " + bullet.Heading.ToString("F5", CultureInfo.InvariantCulture) + " "
                    + bullet.IsActive);
            }
        }
    }
}