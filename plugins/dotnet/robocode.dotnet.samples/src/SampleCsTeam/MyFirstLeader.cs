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
using SampleCs;

namespace SampleCsTeam
{
    /// <summary>
    ///   MyFirstLeader - a sample team robot by Mathew Nelson, and maintained by Flemming N. Larsen
    ///   <p />
    ///   Looks around for enemies, and orders teammates to Fire
    /// </summary>
    public class MyFirstLeader : TeamRobot
    {
        /// <summary>
        ///   Run:  Leader's default behavior
        /// </summary>
        public override void Run()
        {
            // Prepare RobotColors object
            var c = new RobotColors();

            c.bodyColor = Color.Red;
            c.gunColor = Color.Red;
            c.radarColor = Color.Red;
            c.scanColor = Color.Yellow;
            c.bulletColor = Color.Yellow;

            // Set the color of this robot containing the RobotColors
            BodyColor = c.bodyColor;
            GunColor = c.gunColor;
            RadarColor = c.radarColor;
            ScanColor = c.scanColor;
            BulletColor = c.bulletColor;
            try
            {
                // Send RobotColors object to our entire team
                BroadcastMessage(c);
            }
            catch (Exception)
            {
            }

            // Normal behavior
            while (true)
            {
                SetTurnRadarRight(10000);
                Ahead(100);
                Back(100);
            }
        }


        /// <summary>
        ///   OnScannedRobot:  What to do when you see another robot
        /// </summary>
        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            // Don't Fire on teammates
            if (IsTeammate(e.Name))
            {
                return;
            }
            // Calculate enemy bearing
            double enemyBearing = Heading + e.Bearing;
            // Calculate enemy's position
            double enemyX = X + e.Distance*Math.Sin(Utils.ToRadians(enemyBearing));
            double enemyY = Y + e.Distance*Math.Cos(Utils.ToRadians(enemyBearing));

            try
            {
                // Send enemy position to teammates
                BroadcastMessage(new PointD(enemyX, enemyY));
            }
            catch (Exception ex)
            {
                Out.WriteLine("Unable to send order: ");
                Out.WriteLine(ex);
            }
        }

        /// <summary>
        ///   OnHitByBullet:  Turn perpendicular to bullet path
        /// </summary>
        public override void OnHitByBullet(HitByBulletEvent e)
        {
            TurnLeft(90 - e.Bearing);
        }
    }
}