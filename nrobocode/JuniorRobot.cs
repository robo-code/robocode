// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
// - Initial implementation
// *****************************************************************************

using System.IO;
using nrobocode.utils;

namespace nrobocode
{
    /// <summary>
    /// .NET Friendly wrapper for junior robot
    /// </summary>
    public abstract class JuniorRobot // : IJuniorRobot
    {
        #region Java JuniorRobot

        private class JR : robocode.JuniorRobot
        {
            private JuniorRobot owner;
            public JR(JuniorRobot owner)
            {
                this.owner = owner;
                myOut = new JavaConsole(this.@out);
            }

            public override void onHitByBullet()
            {
                owner.OnHitByBullet(owner.robot.hitByBulletBearing);
            }

            public override void onHitRobot()
            {
                owner.OnHitRobot();
            }

            public override void onHitWall()
            {
                owner.OnHitWall();
            }

            public override void onScannedRobot()
            {
                owner.OnScannedRobot();
            }

            private JavaConsole myOut;

            public TextWriter Out
            {
                get
                {
                    return myOut;
                }
            }

        }

        private JR robot;

        #endregion

        #region Constructor

        public JuniorRobot()
        {
            robot=new JR(this);
        }

        #endregion

        #region Constants

        public enum Color
        {
            /// <summary>
            /// The color black (0x000000)
            /// </summary>
            Black = 0x000000,

            /// <summary>
            /// The color white (0xFFFFFF)
            /// </summary>
            White = 0xFFFFFF,

            /// <summary>
            /// The color red  (0xFF0000)
            /// </summary>
            Red = 0xFF0000,

            /// <summary>
            /// The color orange (0xFFA500)
            /// </summary>
            Orange = 0xFFA500, 

            /// <summary>
            /// The color yellow (0xFFFF00)
            /// </summary>
            Yellow = 0xFFFF00, 

            /// <summary>
            /// The color green (0x008000)
            /// </summary>
            Green = 0x008000, 
            
            /// <summary>
            /// The color blue (0x0000FF)
            /// </summary>
            Blue = 0x0000FF, 
            
            /// <summary>
            /// The color purple (0x800080)
            /// </summary>
            Purple = 0x800080, 

            /// <summary>
            /// The color brown (0x8B4513)
            /// </summary>
            Brown = 0x8B4513, 

            /// <summary>
            /// The color gray (0x808080)
            /// </summary>
            Gray = 0x808080,
        }
        #endregion

        #region Fields

        /// <summary>
        /// Current energy of this robot, where 100 means full energy and 0 means no energy (dead).
        /// </summary>
        public int Energy
        {
            get { return robot.energy; }
        }

        /// <summary>
        /// Contains the width of the battlefield
        /// </summary>
        public int FieldWidth
        {
            get { return robot.fieldWidth; }
        }

        public int FieldHeight
        {
            get { return robot.fieldHeight; }
        }

        public TextWriter Out
        {
            get { return robot.Out; }
        }

        #endregion

        #region Fast Actions

        public void setColors(Color bodyColor, Color gunColor, Color radarColor)
        {
            robot.setColors((int)bodyColor, (int)gunColor, (int)radarColor);
        }

        #endregion

        #region Slow Actions

        public void Ahead(int distance)
        {
            robot.ahead(distance);
        }

        public void Back(int distance)
        {
            robot.back(distance);
        }

        public void BearGunTo(int angle)
        {
            robot.bearGunTo(angle);
        }

        public void DoNothing()
        {
            robot.doNothing();
        }

        public void DoNothing(int turns)
        {
            robot.doNothing(turns);
        }

        public void Fire()
        {
            robot.fire();
        }

        public void Fire(double power)
        {
            robot.fire(power);
        }

        public void TurnAheadLeft(int distance, int degrees)
        {
            robot.turnAheadLeft(distance, degrees);
        }

        public void TurnAheadRight(int distance, int degrees)
        {
            robot.turnAheadRight(distance, degrees);
        }

        public void TurnBackLeft(int distance, int degrees)
        {
            robot.turnBackLeft(distance, degrees);
        }

        public void TurnBackRight(int distance, int degrees)
        {
            robot.turnBackRight(distance, degrees);
        }

        public void TurnGunLeft(int degrees)
        {
            robot.turnGunLeft(degrees);
        }

        public void TurnGunRight(int degrees)
        {
            robot.turnGunRight(degrees);
        }

        public void TurnGunTo(int angle)
        {
            robot.turnGunTo(angle);
        }

        public void TurnLeft(int degrees)
        {
            robot.turnLeft(degrees);
        }

        public void TurnRight(int degrees)
        {
            robot.turnRight(degrees);
        }

        public void TurnTo(int angle)
        {
            robot.turnTo(angle);
        }

        #endregion

        #region Events

        public abstract void Run();
        public virtual void OnScannedRobot() { }
        public virtual void OnHitByBullet(int hitByBulletBearing) { }
        public virtual void OnHitRobot() { }
        public virtual void OnHitWall() { }

        #endregion
    }
}
