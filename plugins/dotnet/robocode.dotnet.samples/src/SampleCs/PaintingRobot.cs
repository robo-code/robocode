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
    ///   PaintingRobot - a sample robot that demonstrates the OnPaint() and
    ///   GetGraphics() methods.
    ///   Also demonstrate feature of debugging properties on RobotDialog
    ///   <p />
    ///   Moves in a seesaw motion, and spins the gun around at each end.
    ///   When painting is enabled for this robot, a red circle will be painted
    ///   around this robot.
    /// </summary>
    public class PaintingRobot : Robot
    {
        /// <summary>
        ///   PaintingRobot's Run method - Seesaw
        /// </summary>
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

        /// <summary>
        ///   Fire when we see a robot
        /// </summary>
        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            // demonstrate feature of debugging properties on RobotDialog
            DebugProperty["lastScannedRobot"] = e.Name + " at " + e.Bearing + " degrees at time " + Time;

            Fire(1);
        }

        /// <summary>
        ///   We were hit!  Turn perpendicular to the bullet, 
        ///   so our seesaw might avoid a future shot. 
        ///   In addition, draw orange circles where we were hit.
        /// </summary>
        public override void OnHitByBullet(HitByBulletEvent e)
        {
            // demonstrate feature of debugging properties on RobotDialog
            DebugProperty["lastHitBy"] = e.Name + " with power of bullet " + e.Power + " at time " + Time;

            // show how to remove debugging property
            DebugProperty["lastScannedRobot"] = null;

            // gebugging by painting to battle view
            IGraphics g = Graphics;

            g.DrawEllipse(Pens.Orange, (int) (X - 55), (int) (Y - 55), 110, 110);
            g.DrawEllipse(Pens.Orange, (int) (X - 56), (int) (Y - 56), 112, 112);
            g.DrawEllipse(Pens.Orange, (int) (X - 59), (int) (Y - 59), 118, 118);
            g.DrawEllipse(Pens.Orange, (int) (X - 60), (int) (Y - 60), 120, 120);

            TurnLeft(90 - e.Bearing);
        }

        /// <summary>
        ///   Paint a red circle around our PaintingRobot
        /// </summary>
        public override void OnPaint(IGraphics graphics)
        {
            var transparentGreen = new SolidBrush(Color.FromArgb(30, 0, 0xFF, 0));
            graphics.FillEllipse(transparentGreen, (int) (X - 60), (int) (Y - 60), 120, 120);
            graphics.DrawEllipse(Pens.Red, (int) (X - 50), (int) (Y - 50), 100, 100);
        }
    }
}