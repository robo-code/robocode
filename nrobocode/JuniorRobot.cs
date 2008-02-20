// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
//  - Initial implementation
// *****************************************************************************
using System.IO;
using java.io;
using java.lang;
using nrobocode.utils;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;

namespace nrobocode
{
    /// <summary>
    /// .NET Friendly base for junior robot
    /// </summary>
    public abstract class JuniorRobot : IJuniorRobot
    {
        #region Java JuniorRobot

        private class JR : robocode.JuniorRobot
        {
            private JuniorRobot owner;

            public JR(JuniorRobot owner)
            {
                this.owner = owner;
            }

            public override void onHitByBullet()
            {
                owner.OnHitByBullet();
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

            public override void run()
            {
                myOut = new JavaConsole(@out);
                owner.Run();
            }

            private JavaConsole myOut;

            public TextWriter Out
            {
                get { return myOut; }
            }
        }

        private JR slave;

        #endregion

        #region IJuniorRobot Members

        Runnable IBasicRobot.getRobotRunnable()
        {
            return slave.getRobotRunnable();
        }

        #region IBasicRobot Members

        #endregion

        IBasicEvents IBasicRobot.getBasicEventListener()
        {
            return slave.getBasicEventListener();
        }

        void IBasicRobot.setOut(PrintStream ps)
        {
            slave.setOut(ps);
        }

        void IBasicRobot.setPeer(IBasicRobotPeer rp)
        {
            slave.setPeer(rp);
        }

        #endregion

        #region Constructor

        protected JuniorRobot()
        {
            slave = new JR(this);
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
            get { return slave.energy; }
        }

        /// <summary>
        /// Contains the width of the battlefield
        /// </summary>
        public int FieldWidth
        {
            get { return slave.fieldWidth; }
        }

        /// <summary>
        /// Contains the height of the battlefield.
        /// </summary>
        public int FieldHeight
        {
            get { return slave.fieldHeight; }
        }

        /// <summary>
        /// Current number of other robots on the battle field.
        /// </summary>
        public int Others
        {
            get { return slave.others; }
        }


        /// Current horizontal location of this robot (in pixels).
        /// <see cref="RobotY"/>
        public int RobotX
        {
            get { return slave.robotX; }
        }

        /// Current vertical location of this robot (in pixels).
        /// <see cref="RobotX"/>
        public int RobotY
        {
            get { return slave.robotY; }
        }


        /// Current heading angle of this robot (in degrees).
        /// <see cref="TurnLeft(int)"/>
        /// <see cref="TurnRight(int)"/>
        /// <see cref="TurnTo(int)"/>
        /// <see cref="TurnAheadLeft(int, int)"/>
        /// <see cref="TurnAheadRight(int, int)"/>
        /// <see cref="TurnBackLeft(int, int)"/>
        /// <see cref="TurnBackRight(int, int)"/>
        public int Heading
        {
            get { return slave.heading; }
        }


        /// Current gun heading angle of this robot (in degrees).
        /// <see cref="GunBearing"/>
        /// <see cref="TurnGunLeft(int)"/>
        /// <see cref="TurnGunRight(int)"/>
        /// <see cref="TurnGunTo(int)"/>
        /// <see cref="BearGunTo(int)"/>
        public int GunHeading
        {
            get { return slave.gunHeading; }
        }


        /// Current gun heading angle of this robot compared to its body (in degrees).
        /// <see cref="GunHeading"/>
        /// <see cref="TurnGunLeft(int)"/>
        /// <see cref="TurnGunRight(int)"/>
        /// <see cref="TurnGunTo(int)"/>
        /// <see cref="BearGunTo(int)"/>
        public int GunBearing
        {
            get { return slave.gunBearing; }
        }


        /// Flag specifying if the gun is ready to fire, i.e. gun heat <= 0.
        /// <code>true</code> means that the gun is able to fire; <code>false</code>
        /// means that the gun cannot fire yet as it still needs to cool down.
        /// <see cref="Fire()"/>
        /// <see cref="Fire(double)"/>
        public bool GunReady
        {
            get { return slave.gunReady; }
        }


        /// Current distance to the scanned nearest other robot (in pixels).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link onScannedRobot()} event is active.
        /// <see cref="OnScannedRobot"/>
        /// <see cref="ScannedAngle"/>
        /// <see cref="ScannedBearing"/>
        /// <see cref="ScannedEnergy"/>
        /// <see cref="ScannedVelocity"/>
        /// <see cref="ScannedHeading"/>
        public int ScannedDistance
        {
            get { return slave.scannedDistance; }
        }


        /// Current angle to the scanned nearest other robot (in degrees).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link onScannedRobot()} event is active.
        /// <see cref="OnScannedRobot"/>
        /// <see cref="ScannedDistance"/>
        /// <see cref="ScannedBearing"/>
        /// <see cref="ScannedEnergy"/>
        /// <see cref="ScannedVelocity"/>
        /// <see cref="ScannedHeading"/>
        public int ScannedAngle
        {
            get { return slave.scannedAngle; }
        }


        /// Current angle to the scanned nearest other robot (in degrees) compared to
        /// the body of this robot.
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link onScannedRobot()} event is active.
        /// <see cref="OnScannedRobot"/>
        /// <see cref="ScannedDistance"/>
        /// <see cref="ScannedAngle"/>
        /// <see cref="ScannedEnergy"/>
        /// <see cref="ScannedVelocity"/>
        /// <see cref="ScannedHeading"/>
        public int ScannedBearing
        {
            get { return slave.scannedBearing; }
        }


        /// Current velocity of the scanned nearest other robot.
        /// If there is no robot in the radar's sight, this field will be -99.
        /// Note that a positive value means that the robot moves forward, a negative
        /// value means that the robot moved backward, and 0 means that the robot is
        /// not moving at all.
        /// This field will not be updated while {@link onScannedRobot()} event is active.
        /// <see cref="OnScannedRobot"/>
        /// <see cref="ScannedDistance"/>
        /// <see cref="ScannedAngle"/>
        /// <see cref="ScannedBearing"/>
        /// <see cref="ScannedEnergy"/>
        /// <see cref="ScannedHeading"/>
        public int ScannedVelocity
        {
            get { return slave.scannedVelocity; }
        }


        /// Current heading of the scanned nearest other robot (in degrees).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link onScannedRobot()} event is active.
        /// <see cref="OnScannedRobot"/>
        /// <see cref="ScannedDistance"/>
        /// <see cref="ScannedAngle"/>
        /// <see cref="ScannedBearing"/>
        /// <see cref="ScannedEnergy"/>
        /// <see cref="ScannedVelocity"/>
        public int ScannedHeading
        {
            get { return slave.scannedHeading; }
        }


        /// Current energy of scanned nearest other robot.
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link onScannedRobot()} event is active.
        /// <see cref="OnScannedRobot"/>
        /// <see cref="ScannedDistance"/>
        /// <see cref="ScannedAngle"/>
        /// <see cref="ScannedBearing"/>
        /// <see cref="ScannedVelocity"/>
        public int ScannedEnergy
        {
            get { return slave.scannedEnergy; }
        }


        /// Latest angle from where this robot was hit by a bullet (in degrees).
        /// If the robot has never been hit, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link onHitByBullet()} event is active.
        /// <see cref="OnHitByBullet"/>
        /// <see cref="HitByBulletBearing"/>
        public int HitByBulletAngle
        {
            get { return slave.hitByBulletAngle; }
        }


        /// Latest angle from where this robot was hit by a bullet (in degrees)
        /// compared to the body of this robot.
        /// If the robot has never been hit, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link onHitByBullet()} event is active.
        /// <see cref="OnHitByBullet"/>
        /// <see cref="HitByBulletAngle"/>
        public int HitByBulletBearing
        {
            get { return slave.hitByBulletBearing; }
        }


        /// Latest angle where this robot has hit another robot (in degrees).
        /// If this robot has never hit another robot, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link onHitRobot()} event is active.
        /// <see cref="OnHitRobot"/>
        /// <see cref="HitRobotBearing"/>
        public int HitRobotAngle
        {
            get { return slave.hitRobotAngle; }
        }


        /// Latest angle where this robot has hit another robot (in degrees)
        /// compared to the body of this robot.
        /// If this robot has never hit another robot, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link onHitRobot()} event is active.
        /// <see cref="OnHitRobot"/>
        /// <see cref="HitRobotAngle"/>
        public int HitRobotBearing
        {
            get { return slave.hitRobotBearing; }
        }


        /// Latest angle where this robot has hit a wall (in degrees).
        /// If this robot has never hit a wall, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link onHitWall()} event is active.
        /// <see cref="OnHitWall"/>
        /// <see cref="HitWallBearing"/>
        public int HitWallAngle
        {
            get { return slave.hitWallAngle; }
        }


        /// Latest angle where this robot has hit a wall (in degrees)
        /// compared to the body of this robot.
        /// If this robot has never hit a wall, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link onHitWall()} event is active.
        /// <see cref="OnHitWall"/>
        /// <see cref="HitWallAngle"/>
        public int HitWallBearing
        {
            get { return slave.hitWallBearing; }
        }

        /// <summary>
        /// The output stream your robot should use to print.
        /// <p>
        /// You can view the print-outs by clicking the button for your robot in the
        /// right side of the battle window.
        /// <p>
        /// Example:
        /// <pre>
        /// // Print out a line each time my robot hits another robot
        /// public void OnHitRobot() 
        /// {
        ///     Out.WriteLine("I hit a robot!  My energy: " + Energy);
        /// }
        /// </pre>
        /// </summary>
        public TextWriter Out
        {
            get { return slave.Out; }
        }

        #endregion

        #region Fast Actions

        public void SetColors(Color bodyColor, Color gunColor, Color radarColor)
        {
            slave.setColors((int)bodyColor, (int)gunColor, (int)radarColor);
        }

        #endregion

        #region Slow Actions

        public void Ahead(int distance)
        {
            slave.ahead(distance);
        }

        public void Back(int distance)
        {
            slave.back(distance);
        }

        public void BearGunTo(int angle)
        {
            slave.bearGunTo(angle);
        }

        public void DoNothing()
        {
            slave.doNothing();
        }

        public void DoNothing(int turns)
        {
            slave.doNothing(turns);
        }

        public void Fire()
        {
            slave.fire();
        }

        public void Fire(double power)
        {
            slave.fire(power);
        }

        public void TurnAheadLeft(int distance, int degrees)
        {
            slave.turnAheadLeft(distance, degrees);
        }

        public void TurnAheadRight(int distance, int degrees)
        {
            slave.turnAheadRight(distance, degrees);
        }

        public void TurnBackLeft(int distance, int degrees)
        {
            slave.turnBackLeft(distance, degrees);
        }

        public void TurnBackRight(int distance, int degrees)
        {
            slave.turnBackRight(distance, degrees);
        }

        public void TurnGunLeft(int degrees)
        {
            slave.turnGunLeft(degrees);
        }

        public void TurnGunRight(int degrees)
        {
            slave.turnGunRight(degrees);
        }

        public void TurnGunTo(int angle)
        {
            slave.turnGunTo(angle);
        }

        public void TurnLeft(int degrees)
        {
            slave.turnLeft(degrees);
        }

        public void TurnRight(int degrees)
        {
            slave.turnRight(degrees);
        }

        public void TurnTo(int angle)
        {
            slave.turnTo(angle);
        }

        #endregion

        #region Events

        public virtual void Run()
        {
        }

        public virtual void OnScannedRobot()
        {
        }

        public virtual void OnHitByBullet()
        {
        }

        public virtual void OnHitRobot()
        {
        }

        public virtual void OnHitWall()
        {
        }

        #endregion
    }
}