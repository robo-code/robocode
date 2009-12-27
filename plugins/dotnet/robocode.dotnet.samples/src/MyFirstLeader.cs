/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Maintainance
 *     Pavel Savara
 *     - .NET conversion
 *******************************************************************************/
using System;
using System.Drawing;
using robocode;
using robocode.util;

namespace samplecs
{
    /// <summary>
    /// MyFirstLeader - a sample team robot by Mathew Nelson, and maintained by Flemming N. Larsen
    /// <p/>
    /// Looks around for enemies, and orders teammates to fire
    /// </summary>
    public class MyFirstLeader : TeamRobot
    {
        /// <summary>
        /// run:  Leader's default behavior
        /// </summary>
        public override void run()
        {
            // Prepare RobotColors object
            var c = new RobotColors();

            c.bodyColor = Color.Red;
            c.gunColor = Color.Red;
            c.radarColor = Color.Red;
            c.scanColor = Color.Yellow;
            c.bulletColor = Color.Yellow;

            // Set the color of this robot containing the RobotColors
            setBodyColor(c.bodyColor);
            setGunColor(c.gunColor);
            setRadarColor(c.radarColor);
            setScanColor(c.scanColor);
            setBulletColor(c.bulletColor);
            try
            {
                // Send RobotColors object to our entire team
                broadcastMessage(c);
            }
            catch (Exception)
            {
            }

            // Normal behavior
            while (true)
            {
                setTurnRadarRight(10000);
                ahead(100);
                back(100);
            }
        }


        /// <summary>
        /// onScannedRobot:  What to do when you see another robot
        /// </summary>
        public override void onScannedRobot(ScannedRobotEvent e)
        {
            // Don't fire on teammates
            if (isTeammate(e.getName()))
            {
                return;
            }
            // Calculate enemy bearing
            double enemyBearing = getHeading() + e.getBearing();
            // Calculate enemy's position
            double enemyX = getX() + e.getDistance()*Math.Sin(Utils.toRadians(enemyBearing));
            double enemyY = getY() + e.getDistance()*Math.Cos(Utils.toRadians(enemyBearing));

            try
            {
                // Send enemy position to teammates
                broadcastMessage(new PointF((float) enemyX, (float) enemyY));
            }
            catch (Exception ex)
            {
                output.WriteLine("Unable to send order: ");
                output.WriteLine(ex);
            }
        }

        /// <summary>
        /// onHitByBullet:  Turn perpendicular to bullet path
        /// </summary>
        public override void onHitByBullet(HitByBulletEvent e)
        {
            turnLeft(90 - e.getBearing());
        }
    }
}
