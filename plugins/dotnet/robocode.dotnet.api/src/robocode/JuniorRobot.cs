/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Drawing;
using System.IO;
using Robocode.Exception;
using Robocode.RobotInterfaces;
using Robocode.RobotInterfaces.Peer;
using Robocode.Util;

namespace Robocode
{
    /// <summary>
    /// This is the simplest robot type, which is simpler than the <see cref="Robot"/> and
    /// <see cref="AdvancedRobot"/> classes. The JuniorRobot has a simplified model, in
    /// purpose of teaching programming skills to inexperienced in programming
    /// students. The simplified robot model will keep player from overwhelming of
    /// Robocode's rules, programming syntax and programming concept.
    /// <p/>
    /// Instead of using getters and setters, public fields are provided for
    /// receiving information like the last scanned robot, the coordinate of the
    /// robot etc.
    /// <p/>
    /// All methods on this class are blocking calls, i.e. they do not return before
    /// their action has been completed and will at least take one turn to execute.
    /// However, setting colors is executed immediately and does not cost a turn to
    /// perform.
    /// <seealso cref="Robot"/>
    /// <seealso cref="AdvancedRobot"/>
    /// <seealso cref="TeamRobot"/>
    /// <seealso cref="IDroid"/>
    /// </summary>
    public abstract class JuniorRobot : IJuniorRobot
    {
        internal IBasicRobotPeer peer;
        internal TextWriter _output;

        /// <summary>
        /// The color black (0x000000)
        /// </summary>
        public const int BLACK=0;

        /// <summary>
        /// The color white (0xFFFFFF)
        /// </summary>
        public const int WHITE = 0xFFFFFF;

        /// <summary>
        /// The color red (0xFF0000)
        /// </summary>
        public const int RED = 0xFF0000;

        /// <summary>
        /// The color orange (0xFFA500)
        /// </summary>
        public const int ORANGE = 0xFFA500;

        /// <summary>
        /// The color yellow (0xFFFF00)
        /// </summary>
        public const int YELLOW = 0xFFFF00;

        /// <summary>
        /// The color green (0x008000)
        /// </summary>
        public const int GREEN = 0x008000;

        /// <summary>
        /// The color blue (0x0000FF)
        /// </summary>
        public const int BLUE = 0x0000FF;

        /// <summary>
        /// The color purple (0x800080)
        /// </summary>
        public const int PURPLE = 0x800080;

        /// <summary>
        /// The color brown (0x8B4513)
        /// </summary>
        public const int BROWN = 0x8B4513;

        /// <summary>
        /// The color gray (0x808080)
        /// </summary>
        public const int GRAY = 0x808080;

        /// <summary>
        /// Contains the width of the battlefield.
        ///
        /// <seealso cref="fieldWidth"/>
        /// </summary>
        private int fieldWidth;

        /// <summary>
        /// Contains the height of the battlefield.
        ///
        /// <seealso cref="fieldWidth"/>
        /// </summary>
        private int fieldHeight;

        /// <summary>
        /// Current number of other robots on the battle field.
        /// </summary>
        private int others;

        /// <summary>
        /// Current energy of this robot, where 100 means full energy and 0 means no energy (dead).
        /// </summary>
        private int energy;

        /// <summary>
        /// Current horizontal location of this robot (in pixels).
        ///
        /// <seealso cref="robotY"/>
        /// </summary>
        private int robotX;

        /// <summary>
        /// Current vertical location of this robot (in pixels).
        ///
        /// <seealso cref="robotX"/>
        /// </summary>
        private int robotY;

        /// <summary>
        /// Current heading angle of this robot (in degrees).
        ///
        /// <seealso cref="TurnLeft(int)"/>
        /// <seealso cref="TurnRight(int)"/>
        /// <seealso cref="TurnTo(int)"/>
        /// <seealso cref="TurnAheadLeft(int, int)"/>
        /// <seealso cref="TurnAheadRight(int, int)"/>
        /// <seealso cref="TurnBackLeft(int, int)"/>
        /// <seealso cref="TurnBackRight(int, int)"/>
        /// </summary>
        private int heading;

        /// <summary>
        /// Current gun heading angle of this robot (in degrees).
        ///
        /// <seealso cref="gunBearing"/>
        /// <seealso cref="TurnGunLeft(int)"/>
        /// <seealso cref="TurnGunRight(int)"/>
        /// <seealso cref="TurnGunTo(int)"/>
        /// <seealso cref="BearGunTo(int)"/>
        /// </summary>
        private int gunHeading;

        /// <summary>
        /// Current gun heading angle of this robot compared to its body (in degrees).
        ///
        /// <seealso cref="gunHeading"/>
        /// <seealso cref="TurnGunLeft(int)"/>
        /// <seealso cref="TurnGunRight(int)"/>
        /// <seealso cref="TurnGunTo(int)"/>
        /// <seealso cref="BearGunTo(int)"/>
        /// </summary>
        private int gunBearing;

        /// <summary>
        /// Flag specifying if the gun is ready to Fire, i.e. gun heat &lt;= 0.
        /// true means that the gun is able to Fire; false
        /// means that the gun cannot Fire yet as it still needs to cool down.
        ///
        /// <seealso cref="Fire()"/>
        /// <seealso cref="Fire(double)"/>
        /// </summary>
        private bool gunReady;

        /// <summary>
        /// Current distance to the scanned nearest other robot (in pixels).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while <see cref="OnScannedRobot()"/> event is active.
        ///
        /// <seealso cref="OnScannedRobot()"/>
        /// <seealso cref="scannedAngle"/>
        /// <seealso cref="scannedBearing"/>
        /// <seealso cref="scannedEnergy"/>
        /// <seealso cref="scannedVelocity"/>
        /// <seealso cref="scannedHeading"/>
        /// </summary>
        private int scannedDistance = -1;

        /// <summary>
        /// Current angle to the scanned nearest other robot (in degrees).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while <see cref="OnScannedRobot()"/> event is active.
        ///
        /// <seealso cref="OnScannedRobot()"/>
        /// <seealso cref="scannedDistance"/>
        /// <seealso cref="scannedBearing"/>
        /// <seealso cref="scannedEnergy"/>
        /// <seealso cref="scannedVelocity"/>
        /// <seealso cref="scannedHeading"/>
        /// </summary>
        private int scannedAngle = -1;

        /// <summary>
        /// Current angle to the scanned nearest other robot (in degrees) compared to
        /// the body of this robot.
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while <see cref="OnScannedRobot()"/> event is active.
        ///
        /// <seealso cref="OnScannedRobot()"/>
        /// <seealso cref="scannedDistance"/>
        /// <seealso cref="scannedAngle"/>
        /// <seealso cref="scannedEnergy"/>
        /// <seealso cref="scannedVelocity"/>
        /// <seealso cref="scannedHeading"/>
        /// </summary>
        private int scannedBearing = -1;

        /// <summary>
        /// Current velocity of the scanned nearest other robot.
        /// If there is no robot in the radar's sight, this field will be -99.
        /// Note that a positive value means that the robot moves forward, a negative
        /// value means that the robot moved backward, and 0 means that the robot is
        /// not moving at all.
        /// This field will not be updated while <see cref="OnScannedRobot()"/> event is active.
        ///
        /// <seealso cref="OnScannedRobot()"/>
        /// <seealso cref="scannedDistance"/>
        /// <seealso cref="scannedAngle"/>
        /// <seealso cref="scannedBearing"/>
        /// <seealso cref="scannedEnergy"/>
        /// <seealso cref="scannedHeading"/>
        /// </summary>
        private int scannedVelocity = -99;

        /// <summary>
        /// Current heading of the scanned nearest other robot (in degrees).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while <see cref="OnScannedRobot()"/> event is active.
        ///
        /// <seealso cref="OnScannedRobot()"/>
        /// <seealso cref="scannedDistance"/>
        /// <seealso cref="scannedAngle"/>
        /// <seealso cref="scannedBearing"/>
        /// <seealso cref="scannedEnergy"/>
        /// <seealso cref="scannedVelocity"/>
        /// </summary>
        private int scannedHeading = -1;

        /// <summary>
        /// Current energy of scanned nearest other robot.
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while <see cref="OnScannedRobot()"/> event is active.
        ///
        /// <seealso cref="OnScannedRobot()"/>
        /// <seealso cref="scannedDistance"/>
        /// <seealso cref="scannedAngle"/>
        /// <seealso cref="scannedBearing"/>
        /// <seealso cref="scannedVelocity"/>
        /// </summary>
        private int scannedEnergy = -1;

        /// <summary>
        /// Latest angle from where this robot was hit by a bullet (in degrees).
        /// If the robot has never been hit, this field will be less than 0, i.e. -1.
        /// This field will not be updated while <see cref="OnHitByBullet()"/> event is active.
        ///
        /// <seealso cref="OnHitByBullet()"/>
        /// <seealso cref="hitByBulletBearing"/>
        /// </summary>
        private int hitByBulletAngle = -1;

        /// <summary>
        /// Latest angle from where this robot was hit by a bullet (in degrees)
        /// compared to the body of this robot.
        /// If the robot has never been hit, this field will be less than 0, i.e. -1.
        /// This field will not be updated while <see cref="OnHitByBullet()"/> event is active.
        ///
        /// <seealso cref="OnHitByBullet()"/>
        /// <seealso cref="hitByBulletAngle"/>
        /// </summary>
        private int hitByBulletBearing = -1;

        /// <summary>
        /// Latest angle where this robot has hit another robot (in degrees).
        /// If this robot has never hit another robot, this field will be less than 0, i.e. -1.
        /// This field will not be updated while <see cref="OnHitRobot()"/> event is active.
        ///
        /// <seealso cref="OnHitRobot()"/>
        /// <seealso cref="hitRobotBearing"/>
        /// </summary>
        private int hitRobotAngle = -1;

        /// <summary>
        /// Latest angle where this robot has hit another robot (in degrees)
        /// compared to the body of this robot.
        /// If this robot has never hit another robot, this field will be less than 0, i.e. -1.
        /// This field will not be updated while <see cref="OnHitRobot()"/> event is active.
        ///
        /// <seealso cref="OnHitRobot()"/>
        /// <seealso cref="hitRobotAngle"/>
        /// </summary>
        private int hitRobotBearing = -1;

        /// <summary>
        /// Latest angle where this robot has hit a wall (in degrees).
        /// If this robot has never hit a wall, this field will be less than 0, i.e. -1.
        /// This field will not be updated while <see cref="OnHitWall()"/> event is active.
        ///
        /// <seealso cref="OnHitWall()"/>
        /// <seealso cref="hitWallBearing"/>
        /// </summary>
        private int hitWallAngle = -1;

        /// <summary>
        /// Latest angle where this robot has hit a wall (in degrees)
        /// compared to the body of this robot.
        /// If this robot has never hit a wall, this field will be less than 0, i.e. -1.
        /// This field will not be updated while <see cref="OnHitWall()"/> event is active.
        ///
        /// <seealso cref="OnHitWall()"/>
        /// <seealso cref="hitWallAngle"/>
        /// </summary>
        private int hitWallBearing = -1;

        /// <summary>
        /// The robot event handler for this robot.
        /// </summary>
        private InnerEventHandler innerEventHandler;


        /// <summary>
        /// The Out stream your robot should use to print.
        /// <p/>
        /// You can view the print-outs by clicking the button for your robot in the
        /// right side of the battle window.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Print Out a line each time my robot hits another robot
        ///   public void OnHitRobot(HitRobotEvent e)
        ///   {
        ///       Out.WriteLine("I hit a robot!  My energy: " + Energy + " his energy: " + e.Energy);
        ///   }
        ///   </code>
        /// </example>
        /// </summary>
        public TextWriter Out
        {
            get { return _output; }
        }

        /// <summary>
        /// Contains the width of the battlefield.
        ///
        /// <seealso cref="FieldHeight"/>
        /// </summary>
        public int FieldWidth
        {
            get { return fieldWidth; }
        }

        /// <summary>
        /// Contains the height of the battlefield.
        ///
        /// <seealso cref="FieldWidth"/>
        /// </summary>
        public int FieldHeight
        {
            get { return fieldHeight; }
        }

        /// <summary>
        /// Current number of other robots on the battle field.
        /// </summary>
        public int Others
        {
            get { return others; }
        }

        /// <summary>
        /// Current energy of this robot, where 100 means full energy and 0 means no energy (dead).
        /// </summary>
        public int Energy
        {
            get { return energy; }
        }

        /// <summary>
        /// Current horizontal location of this robot (in pixels).
        ///
        /// <seealso cref="RobotY"/>
        /// </summary>
        public int RobotX
        {
            get { return robotX; }
        }

        /// <summary>
        /// Current vertical location of this robot (in pixels).
        ///
        /// <seealso cref="RobotX"/>
        /// </summary>
        public int RobotY
        {
            get { return robotY; }
        }

        /// <summary>
        /// Current heading angle of this robot (in degrees).
        ///
        /// <seealso cref="TurnLeft(int)"/>
        /// <seealso cref="TurnRight(int)"/>
        /// <seealso cref="TurnTo(int)"/>
        /// <seealso cref="TurnAheadLeft(int, int)"/>
        /// <seealso cref="TurnAheadRight(int, int)"/>
        /// <seealso cref="TurnBackLeft(int, int)"/>
        /// <seealso cref="TurnBackRight(int, int)"/>
        /// </summary>
        public int Heading
        {
            get { return heading; }
        }

        /// <summary>
        /// Current gun heading angle of this robot (in degrees).
        ///
        /// <seealso cref="GunBearing"/>
        /// <seealso cref="TurnGunLeft(int)"/>
        /// <seealso cref="TurnGunRight(int)"/>
        /// <seealso cref="TurnGunTo(int)"/>
        /// <seealso cref="BearGunTo(int)"/>
        /// </summary>
        public int GunHeading
        {
            get { return gunHeading; }
        }

        /// <summary>
        /// Current gun heading angle of this robot compared to its body (in degrees).
        ///
        /// <seealso cref="GunHeading"/>
        /// <seealso cref="TurnGunLeft(int)"/>
        /// <seealso cref="TurnGunRight(int)"/>
        /// <seealso cref="TurnGunTo(int)"/>
        /// <seealso cref="BearGunTo(int)"/>
        /// </summary>
        public int GunBearing
        {
            get { return gunBearing; }
        }

        /// <summary>
        /// Flag specifying if the gun is ready to Fire, i.e. gun heat &lt;= 0.
        /// true means that the gun is able to Fire; false
        /// means that the gun cannot Fire yet as it still needs to cool down.
        ///
        /// <seealso cref="Fire()"/>
        /// <seealso cref="Fire(double)"/>
        /// </summary>
        public bool IsGunReady
        {
            get { return gunReady; }
        }

        /// <summary>
        /// Current distance to the scanned nearest other robot (in pixels).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while <see cref="OnScannedRobot()"/> event is active.
        ///
        /// <seealso cref="OnScannedRobot()"/>
        /// <seealso cref="ScannedAngle"/>
        /// <seealso cref="ScannedBearing"/>
        /// <seealso cref="ScannedEnergy"/>
        /// <seealso cref="ScannedVelocity"/>
        /// <seealso cref="ScannedHeading"/>
        /// </summary>
        public int ScannedDistance
        {
            get { return scannedDistance; }
        }

        /// <summary>
        /// Current angle to the scanned nearest other robot (in degrees).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while <see cref="OnScannedRobot()"/> event is active.
        ///
        /// <seealso cref="OnScannedRobot()"/>
        /// <seealso cref="ScannedDistance"/>
        /// <seealso cref="ScannedBearing"/>
        /// <seealso cref="ScannedEnergy"/>
        /// <seealso cref="ScannedVelocity"/>
        /// <seealso cref="ScannedHeading"/>
        /// </summary>
        public int ScannedAngle
        {
            get { return scannedAngle; }
        }

        /// <summary>
        /// Current angle to the scanned nearest other robot (in degrees) compared to
        /// the body of this robot.
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while <see cref="OnScannedRobot()"/> event is active.
        ///
        /// <seealso cref="OnScannedRobot()"/>
        /// <seealso cref="ScannedDistance"/>
        /// <seealso cref="ScannedAngle"/>
        /// <seealso cref="ScannedEnergy"/>
        /// <seealso cref="ScannedVelocity"/>
        /// <seealso cref="ScannedHeading"/>
        /// </summary>
        public int ScannedBearing
        {
            get { return scannedBearing; }
        }

        /// <summary>
        /// Current velocity of the scanned nearest other robot.
        /// If there is no robot in the radar's sight, this field will be -99.
        /// Note that a positive value means that the robot moves forward, a negative
        /// value means that the robot moved backward, and 0 means that the robot is
        /// not moving at all.
        /// This field will not be updated while <see cref="OnScannedRobot()"/> event is active.
        ///
        /// <seealso cref="OnScannedRobot()"/>
        /// <seealso cref="ScannedDistance"/>
        /// <seealso cref="ScannedAngle"/>
        /// <seealso cref="ScannedBearing"/>
        /// <seealso cref="ScannedEnergy"/>
        /// <seealso cref="ScannedHeading"/>
        /// </summary>
        public int ScannedVelocity
        {
            get { return scannedVelocity; }
        }

        /// <summary>
        /// Current heading of the scanned nearest other robot (in degrees).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while <see cref="OnScannedRobot()"/> event is active.
        ///
        /// <seealso cref="OnScannedRobot()"/>
        /// <seealso cref="ScannedDistance"/>
        /// <seealso cref="ScannedAngle"/>
        /// <seealso cref="ScannedBearing"/>
        /// <seealso cref="ScannedEnergy"/>
        /// <seealso cref="ScannedVelocity"/>
        /// </summary>
        public int ScannedHeading
        {
            get { return scannedHeading; }
        }

        /// <summary>
        /// Current energy of scanned nearest other robot.
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while <see cref="OnScannedRobot()"/> event is active.
        ///
        /// <seealso cref="OnScannedRobot()"/>
        /// <seealso cref="ScannedDistance"/>
        /// <seealso cref="ScannedAngle"/>
        /// <seealso cref="ScannedBearing"/>
        /// <seealso cref="ScannedVelocity"/>
        /// </summary>
        public int ScannedEnergy
        {
            get { return scannedEnergy; }
        }

        /// <summary>
        /// Latest angle from where this robot was hit by a bullet (in degrees).
        /// If the robot has never been hit, this field will be less than 0, i.e. -1.
        /// This field will not be updated while <see cref="OnHitByBullet()"/> event is active.
        ///
        /// <seealso cref="OnHitByBullet()"/>
        /// <seealso cref="HitByBulletBearing"/>
        /// </summary>
        public int HitByBulletAngle
        {
            get { return hitByBulletAngle; }
        }

        /// <summary>
        /// Latest angle from where this robot was hit by a bullet (in degrees)
        /// compared to the body of this robot.
        /// If the robot has never been hit, this field will be less than 0, i.e. -1.
        /// This field will not be updated while <see cref="OnHitByBullet()"/> event is active.
        ///
        /// <seealso cref="OnHitByBullet()"/>
        /// <seealso cref="HitByBulletAngle"/>
        /// </summary>
        public int HitByBulletBearing
        {
            get { return hitByBulletBearing; }
        }

        /// <summary>
        /// Latest angle where this robot has hit another robot (in degrees).
        /// If this robot has never hit another robot, this field will be less than 0, i.e. -1.
        /// This field will not be updated while <see cref="OnHitRobot()"/> event is active.
        ///
        /// <seealso cref="OnHitRobot()"/>
        /// <seealso cref="HitRobotBearing"/>
        /// </summary>
        public int HitRobotAngle
        {
            get { return hitRobotAngle; }
        }

        /// <summary>
        /// Latest angle where this robot has hit another robot (in degrees)
        /// compared to the body of this robot.
        /// If this robot has never hit another robot, this field will be less than 0, i.e. -1.
        /// This field will not be updated while <see cref="OnHitRobot()"/> event is active.
        ///
        /// <seealso cref="OnHitRobot()"/>
        /// <seealso cref="HitRobotAngle"/>
        /// </summary>
        public int HitRobotBearing
        {
            get { return hitRobotBearing; }
        }

        /// <summary>
        /// Latest angle where this robot has hit a wall (in degrees).
        /// If this robot has never hit a wall, this field will be less than 0, i.e. -1.
        /// This field will not be updated while <see cref="OnHitWall()"/> event is active.
        ///
        /// <seealso cref="OnHitWall()"/>
        /// <seealso cref="HitWallBearing"/>
        /// </summary>
        public int HitWallAngle
        {
            get { return hitWallAngle; }
        }

        /// <summary>
        /// Latest angle where this robot has hit a wall (in degrees)
        /// compared to the body of this robot.
        /// If this robot has never hit a wall, this field will be less than 0, i.e. -1.
        /// This field will not be updated while <see cref="OnHitWall()"/> event is active.
        ///
        /// <seealso cref="OnHitWall()"/>
        /// <seealso cref="HitWallAngle"/>
        /// </summary>
        public int HitWallBearing
        {
            get { return hitWallBearing; }
        }

        /// <inheritdoc cref="IBasicRobot.SetOut(TextWriter)"/>
        void IBasicRobot.SetOut(TextWriter outpt)
        {
            _output = outpt;
        }

        /// <inheritdoc cref="IBasicRobot.SetPeer(IBasicRobotPeer)" />
        void IBasicRobot.SetPeer(IBasicRobotPeer per)
        {
            peer = per;
        }

        /// <summary>
        /// Throws a RobotException. This method should be called when the robot's peer
        /// is uninitialized.
        /// </summary>
        internal static void UninitializedException()
        {
            throw new RobotException(
                "You cannot call the methods before your Run() method is called, or you are using a Robot object that the game doesn't know about.");
        }

        /// <inheritdoc cref="IBasicRobot.GetRobotRunnable()"/>
        IRunnable IBasicRobot.GetRobotRunnable()
        {
            return getEventHandler();
        }

        /// <inheritdoc cref="IBasicRobot.GetBasicEventListener()"/>
        IBasicEvents IBasicRobot.GetBasicEventListener()
        {
            return getEventHandler();
        }


        /// <summary>
        /// Moves this robot forward by pixels.
        /// <seealso cref="Back(int)"/>
        /// <seealso cref="RobotX"/>
        /// <seealso cref="RobotY"/>
        /// </summary>
        /// <param name="distance">The amount of pixels to move forward</param> 
        public void Ahead(int distance)
        {
            if (peer != null)
            {
                peer.Move(distance);
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Moves this robot backward by pixels.
        ///
        /// <seealso cref="Ahead(int)"/>
        /// <seealso cref="RobotX"/>
        /// <seealso cref="RobotY"/>
        /// </summary>
        /// <param name="distance"> the amount of pixels to move backward</param> 
        public void Back(int distance)
        {
            Ahead(-distance);
        }

        /// <summary>
        /// Turns the gun to the specified angle (in degrees) relative to body of this robot.
        /// The gun will turn to the side with the shortest delta angle to the specified angle.
        /// <seealso cref="GunHeading"/>
        /// <seealso cref="GunBearing"/>
        /// <seealso cref="TurnGunLeft(int)"/>
        /// <seealso cref="TurnGunRight(int)"/>
        /// <seealso cref="TurnGunTo(int)"/>
        /// </summary>
        /// <param name="angle">the angle to turn the gun to relative to the body of this robot</param> 
        public void BearGunTo(int angle)
        {
            if (peer != null)
            {
                peer.TurnGun(
                    Utils.NormalRelativeAngle(peer.GetBodyHeading() + Utils.ToRadians(angle) - peer.GetGunHeading()));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Skips a turn.
        /// <seealso cref="DoNothing(int)"/>
        /// </summary>
        public void DoNothing()
        {
            if (peer != null)
            {
                peer.Execute();
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Skips the specified number of turns.
        /// <seealso cref="DoNothing()"/>
        /// </summary>
        /// <param name="turns">The number of turns to skip</param>
        public void DoNothing(int turns)
        {
            if (turns <= 0)
            {
                return;
            }
            if (peer != null)
            {
                for (int i = 0; i < turns; i++)
                {
                    peer.Execute();
                }
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Fires a bullet with the default power of 1.
        /// If the gun heat is more than 0 and hence cannot Fire, this method will
        /// suspend until the gun is ready to Fire, and then Fire a bullet.
        ///
        /// <seealso cref="GunReady"/>
        /// </summary>
        public void Fire()
        {
            Fire(1);
        }

        /// <summary>
        /// Fires a bullet with the specified bullet power, which is between 0.1 and 3
        /// where 3 is the maximum bullet power.
        /// If the gun heat is more than 0 and hence cannot Fire, this method will
        /// suspend until the gun is ready to Fire, and then Fire a bullet.
        /// <seealso cref="GunReady"/>
        /// </summary>
        /// <param name="power">Between 0.1 and 3</param>
        public void Fire(double power)
        {
            if (peer != null)
            {
                getEventHandler().juniorFirePower = power;
                peer.Execute();
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// This event method is called from the game when this robot has been hit
        /// by another robot's bullet. When this event occurs the
        /// <see cref="HitByBulletAngle"/> and <see cref="HitByBulletBearing"/> fields values
        /// are automatically updated.
        ///
        /// <seealso cref="HitByBulletAngle"/>
        /// <seealso cref="HitByBulletBearing"/>
        /// </summary>
        public virtual void OnHitByBullet()
        {
        }

        /// <summary>
        /// This event method is called from the game when a bullet from this robot
        /// has hit another robot. When this event occurs the <see cref="HitRobotAngle"/>
        /// and <see cref="HitRobotBearing"/> fields values are automatically updated.
        ///
        /// <seealso cref="HitRobotAngle"/>
        /// <seealso cref="HitRobotBearing"/>
        /// </summary>
        public virtual void OnHitRobot()
        {
        }

        /// <summary>
        /// This event method is called from the game when this robot has hit a wall.
        /// When this event occurs the <see cref="HitWallAngle"/> and
        /// <see cref="HitWallBearing"/> fields values are automatically updated.
        ///
        /// <seealso cref="HitWallAngle"/>
        /// <seealso cref="HitWallBearing"/>
        /// </summary>
        public virtual void OnHitWall()
        {
        }

        /// <summary>
        /// This event method is called from the game when the radar detects another
        /// robot. When this event occurs the <see cref="ScannedDistance"/>,
        /// <see cref="ScannedAngle"/>, <see cref="ScannedBearing"/>, and <see cref="ScannedEnergy"/>
        /// field values are automatically updated.
        /// <seealso cref="ScannedDistance"/>
        /// <seealso cref="ScannedAngle"/>
        /// <seealso cref="ScannedBearing"/>
        /// <seealso cref="ScannedEnergy"/>
        /// </summary>
        public virtual void OnScannedRobot()
        {
        }

        /// <summary>
        /// The main method in every robot. You must override this to set up your
        /// robot's basic behavior.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // A basic robot that moves around in a square
        ///   public void Run()
        ///   {
        ///       Ahead(100);
        ///       TurnRight(90);
        ///   }
        ///   </code>
        /// </example>
        /// This method is automatically re-called when it has returned.
        /// </summary>
        public virtual void Run()
        {
        }

        /// <summary>
        /// Sets the colors of the robot. The color values are RGB values.
        /// You can use the colors that are already defined for this class.
        /// <seealso cref="SetColors(int, int, int, int, int)"/>
        /// </summary>
        /// <param name="bodyColor">The RGB color value for the body</param>
        /// <param name="gunColor">The RGB color value for the gun</param>
        /// <param name="radarColor">The RGB color value for the radar</param>
        public void SetColors(int bodyColor, int gunColor, int radarColor)
        {
            if (peer != null)
            {
                peer.SetBodyColor(Color.FromArgb(bodyColor));
                peer.SetGunColor(Color.FromArgb(gunColor));
                peer.SetRadarColor(Color.FromArgb(radarColor));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Sets the colors of the robot. The color values are RGB values.
        /// You can use the colors that are already defined for this class.
        /// <seealso cref="SetColors(int, int, int)"/>
        /// </summary>
        /// <param name="bodyColor">The RGB color value for the body</param>
        /// <param name="gunColor">The RGB color value for the gun</param>
        /// <param name="radarColor">The RGB color value for the radar</param>
        /// <param name="bulletColor">The RGB color value for the bullets</param>
        /// <param name="scanArcColor">The RGB color value for the scan arc</param>
        public void SetColors(int bodyColor, int gunColor, int radarColor, int bulletColor, int scanArcColor)
        {
            if (peer != null)
            {
                peer.SetBodyColor(Color.FromArgb(bodyColor));
                peer.SetGunColor(Color.FromArgb(gunColor));
                peer.SetRadarColor(Color.FromArgb(radarColor));
                peer.SetBulletColor(Color.FromArgb(bulletColor));
                peer.SetScanColor(Color.FromArgb(scanArcColor));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Moves this robot forward by pixels and turns this robot left by degrees
        /// at the same time. The robot will move in a curve that follows a perfect
        /// circle, and the moving and turning will end at the same time.
        /// <p/>
        /// Note that the max. velocity and max. turn rate is automatically adjusted,
        /// which means that the robot will move slower the sharper the turn is
        /// compared to the distance.
        /// <seealso cref="Heading"/>
        /// <seealso cref="RobotX"/>
        /// <seealso cref="RobotY"/>
        /// <seealso cref="TurnLeft(int)"/>
        /// <seealso cref="TurnRight(int)"/>
        /// <seealso cref="TurnTo(int)"/>
        /// <seealso cref="TurnAheadRight(int, int)"/>
        /// <seealso cref="TurnBackLeft(int, int)"/>
        /// <seealso cref="TurnBackRight(int, int)"/>
        /// </summary>
        /// <param name="distance">The amount of pixels to move forward</param>
        /// <param name="degrees">The amount of degrees to turn to the left</param>
        public void TurnAheadLeft(int distance, int degrees)
        {
            TurnAheadRight(distance, -degrees);
        }

        /// <summary>
        /// Moves this robot forward by pixels and turns this robot right by degrees
        /// at the same time. The robot will move in a curve that follows a perfect
        /// circle, and the moving and turning will end at the same time.
        /// <p/>
        /// Note that the max. velocity and max. turn rate is automatically adjusted,
        /// which means that the robot will move slower the sharper the turn is
        /// compared to the distance.
        /// <seealso cref="Heading"/>
        /// <seealso cref="RobotX"/>
        /// <seealso cref="RobotY"/>
        /// <seealso cref="TurnLeft(int)"/>
        /// <seealso cref="TurnRight(int)"/>
        /// <seealso cref="TurnTo(int)"/>
        /// <seealso cref="TurnAheadLeft(int, int)"/>
        /// <seealso cref="TurnBackLeft(int, int)"/>
        /// <seealso cref="TurnBackRight(int, int)"/>
        /// </summary>
        /// <param name="distance">The amount of pixels to move forward</param>
        /// <param name="degrees">The amount of degrees to turn to the right</param>
        public void TurnAheadRight(int distance, int degrees)
        {
            if (peer != null)
            {
                ((IJuniorRobotPeer) peer).TurnAndMove(distance, Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Moves this robot backward by pixels and turns this robot left by degrees
        /// at the same time. The robot will move in a curve that follows a perfect
        /// circle, and the moving and turning will end at the same time.
        /// <p/>
        /// Note that the max. velocity and max. turn rate is automatically adjusted,
        /// which means that the robot will move slower the sharper the turn is
        /// compared to the distance.
        /// <seealso cref="Heading"/>
        /// <seealso cref="RobotX"/>
        /// <seealso cref="RobotY"/>
        /// <seealso cref="TurnLeft(int)"/>
        /// <seealso cref="TurnRight(int)"/>
        /// <seealso cref="TurnTo(int)"/>
        /// <seealso cref="TurnAheadLeft(int, int)"/>
        /// <seealso cref="TurnAheadRight(int, int)"/>
        /// <seealso cref="TurnBackRight(int, int)"/>
        /// </summary>
        /// <param name="distance">The amount of pixels to move backward</param>
        /// <param name="degrees">The amount of degrees to turn to the left</param>
        public void TurnBackLeft(int distance, int degrees)
        {
            TurnAheadRight(-distance, degrees);
        }

        /// <summary>
        /// Moves this robot backward by pixels and turns this robot right by degrees
        /// at the same time. The robot will move in a curve that follows a perfect
        /// circle, and the moving and turning will end at the same time.
        /// <p/>
        /// Note that the max. velocity and max. turn rate is automatically adjusted,
        /// which means that the robot will move slower the sharper the turn is
        /// compared to the distance.
        /// <seealso cref="Heading"/>
        /// <seealso cref="RobotX"/>
        /// <seealso cref="RobotY"/>
        /// <seealso cref="TurnLeft(int)"/>
        /// <seealso cref="TurnRight(int)"/>
        /// <seealso cref="TurnTo(int)"/>
        /// <seealso cref="TurnAheadLeft(int, int)"/>
        /// <seealso cref="TurnAheadRight(int, int)"/>
        /// <seealso cref="TurnBackLeft(int, int)"/>
        /// </summary>
        /// <param name="distance">The amount of pixels to move backward</param>
        /// <param name="degrees">The amount of degrees to turn to the right</param>
        public void TurnBackRight(int distance, int degrees)
        {
            TurnAheadRight(-distance, -degrees);
        }

        /// <summary>
        /// Turns the gun left by degrees.
        /// <seealso cref="GunHeading"/>
        /// <seealso cref="GunBearing"/>
        /// <seealso cref="TurnGunRight(int)"/>
        /// <seealso cref="TurnGunTo(int)"/>
        /// <seealso cref="BearGunTo(int)"/>
        /// </summary>
        /// <param name="degrees">The amount of degrees to turn the gun to the left</param>
        public void TurnGunLeft(int degrees)
        {
            TurnGunRight(-degrees);
        }

        /// <summary>
        /// Turns the gun right by degrees.
        /// <seealso cref="GunHeading"/>
        /// <seealso cref="GunBearing"/>
        /// <seealso cref="TurnGunLeft(int)"/>
        /// <seealso cref="TurnGunTo(int)"/>
        /// <seealso cref="BearGunTo(int)"/>
        /// </summary>
        /// <param name="degrees">The amount of degrees to turn the gun to the right</param>
        public void TurnGunRight(int degrees)
        {
            if (peer != null)
            {
                peer.TurnGun(Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Turns the gun to the specified angle (in degrees).
        /// The gun will turn to the side with the shortest delta angle to the
        /// specified angle.
        /// <seealso cref="GunHeading"/>
        /// <seealso cref="GunBearing"/>
        /// <seealso cref="TurnGunLeft(int)"/>
        /// <seealso cref="TurnGunRight(int)"/>
        /// <seealso cref="BearGunTo(int)"/>
        /// </summary>
        /// <param name="angle">The angle to turn the gun to</param>
        public void TurnGunTo(int angle)
        {
            if (peer != null)
            {
                peer.TurnGun(Utils.NormalRelativeAngle(Utils.ToRadians(angle) - peer.GetGunHeading()));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Turns this robot left by degrees.
        /// <seealso cref="Heading"/>
        /// <seealso cref="TurnRight(int)"/>
        /// <seealso cref="TurnTo(int)"/>
        /// <seealso cref="TurnAheadLeft(int, int)"/>
        /// <seealso cref="TurnAheadRight(int, int)"/>
        /// <seealso cref="TurnBackLeft(int, int)"/>
        /// <seealso cref="TurnBackRight(int, int)"/>
        /// </summary>
        /// <param name="degrees">The amount of degrees to turn to the left</param>
        public void TurnLeft(int degrees)
        {
            TurnRight(-degrees);
        }

        /// <summary>
        /// Turns this robot right by degrees.
        /// <seealso cref="Heading"/>
        /// <seealso cref="TurnLeft(int)"/>
        /// <seealso cref="TurnTo(int)"/>
        /// <seealso cref="TurnAheadLeft(int, int)"/>
        /// <seealso cref="TurnAheadRight(int, int)"/>
        /// <seealso cref="TurnBackLeft(int, int)"/>
        /// <seealso cref="TurnBackRight(int, int)"/>
        /// </summary>
        /// <param name="degrees">The amount of degrees to turn to the right</param>
        public void TurnRight(int degrees)
        {
            if (peer != null)
            {
                peer.TurnBody(Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Turns this robot to the specified angle (in degrees).
        /// The robot will turn to the side with the shortest delta angle to the
        /// specified angle.
        /// <seealso cref="Heading"/>
        /// <seealso cref="TurnLeft(int)"/>
        /// <seealso cref="TurnRight(int)"/>
        /// <seealso cref="TurnAheadLeft(int, int)"/>
        /// <seealso cref="TurnAheadRight(int, int)"/>
        /// <seealso cref="TurnBackLeft(int, int)"/>
        /// <seealso cref="TurnBackRight(int, int)"/>
        /// </summary>
        /// <param name="angle">The angle to turn this robot to</param>
        public void TurnTo(int angle)
        {
            if (peer != null)
            {
                peer.TurnBody(Utils.NormalRelativeAngle(Utils.ToRadians(angle) - peer.GetBodyHeading()));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Returns the event handler of this robot.
        /// </summary>
        private InnerEventHandler getEventHandler()
        {
            if (innerEventHandler == null)
            {
                innerEventHandler = new InnerEventHandler(this);
            }
            return innerEventHandler;
        }

        /// <summary>
        /// The JuniorRobot event handler, which extends the basic robot events,
        /// IBasicEvents, and IRunnable.
        /// </summary>
        private sealed class InnerEventHandler : IBasicEvents, IRunnable
        {
            public InnerEventHandler(JuniorRobot robot)
            {
                this.robot = robot;
            }

            private readonly JuniorRobot robot;
            internal double juniorFirePower;
            private long currentTurn;

            public void OnBulletHit(BulletHitEvent evnt)
            {
            }

            public void OnBulletHitBullet(BulletHitBulletEvent evnt)
            {
            }

            public void OnBulletMissed(BulletMissedEvent evnt)
            {
            }

            public void OnDeath(DeathEvent evnt)
            {
            }

            public void OnHitByBullet(HitByBulletEvent evnt)
            {
                double angle = robot.peer.GetBodyHeading() + evnt.BearingRadians;

                robot.hitByBulletAngle = (int) (Utils.ToDegrees(Utils.NormalAbsoluteAngle(angle)) + 0.5);
                robot.hitByBulletBearing = (int) (evnt.Bearing + 0.5);
                robot.OnHitByBullet();
            }

            public void OnHitRobot(HitRobotEvent evnt)
            {
                double angle = robot.peer.GetBodyHeading() + evnt.BearingRadians;

                robot.hitRobotAngle = (int) (Utils.ToDegrees(Utils.NormalAbsoluteAngle(angle)) + 0.5);
                robot.hitRobotBearing = (int) (evnt.Bearing + 0.5);
                robot.OnHitRobot();
            }

            public void OnHitWall(HitWallEvent evnt)
            {
                double angle = robot.peer.GetBodyHeading() + evnt.BearingRadians;

                robot.hitWallAngle = (int) (Utils.ToDegrees(Utils.NormalAbsoluteAngle(angle)) + 0.5);
                robot.hitWallBearing = (int) (evnt.Bearing + 0.5);
                robot.OnHitWall();
            }

            public void OnRobotDeath(RobotDeathEvent evnt)
            {
                robot.others = robot.peer.GetOthers();
            }

            public void OnScannedRobot(ScannedRobotEvent evnt)
            {
                robot.scannedDistance = (int) (evnt.Distance + 0.5);
                robot.scannedEnergy = Math.Max(1, (int) (evnt.Energy + 0.5));
                robot.scannedAngle = (int) (Utils.ToDegrees(
                                                Utils.NormalAbsoluteAngle(robot.peer.GetBodyHeading() +
                                                                          evnt.BearingRadians))
                                            + 0.5);
                robot.scannedBearing = (int) (evnt.Bearing + 0.5);
                robot.scannedHeading = (int) (evnt.Heading + 0.5);
                robot.scannedVelocity = (int) (evnt.Velocity + 0.5);

                robot.OnScannedRobot();
            }

            public void OnStatus(StatusEvent e)
            {
                RobotStatus s = e.Status;

                robot.others = robot.peer.GetOthers();
                robot.energy = Math.Max(1, (int) (s.Energy + 0.5));
                robot.robotX = (int) (s.X + 0.5);
                robot.robotY = (int) (s.Y + 0.5);
                robot.heading = (int) (Utils.ToDegrees(s.Heading) + 0.5);
                robot.gunHeading = (int) (Utils.ToDegrees(s.GunHeading) + 0.5);
                robot.gunBearing =
                    (int) (Utils.ToDegrees(Utils.NormalRelativeAngle(s.GunHeading - s.Heading)) + 0.5);
                robot.gunReady = (s.GunHeat <= 0);

                currentTurn = e.Time;

                // Auto Fire
                if (juniorFirePower > 0 && robot.gunReady && (robot.peer.GetGunTurnRemaining() == 0))
                {
                    if (robot.peer.SetFire(juniorFirePower) != null)
                    {
                        robot.gunReady = false;
                        juniorFirePower = 0;
                    }
                }

                // Reset event data
                robot.scannedDistance = -1;
                robot.scannedAngle = -1;
                robot.scannedBearing = -1;
                robot.scannedVelocity = -99;
                robot.scannedHeading = -1;
                robot.scannedEnergy = -1;
                robot.hitByBulletAngle = -1;
                robot.hitByBulletBearing = -1;
                robot.hitRobotAngle = -1;
                robot.hitRobotBearing = -1;
                robot.hitWallAngle = -1;
                robot.hitWallBearing = -1;
            }

            public void OnWin(WinEvent evnt)
            {
            }

            // ReSharper disable FunctionNeverReturns
            public void Run()
            {
                robot.fieldWidth = (int) (robot.peer.GetBattleFieldWidth() + 0.5);
                robot.fieldHeight = (int) (robot.peer.GetBattleFieldHeight() + 0.5);

                // noinspection InfiniteLoopStatement
                while (true)
                {
                    long lastTurn = currentTurn; // Used for the Rescan check

                    robot.Run(); // Run the code in the JuniorRobot

                    // Make sure that we Rescan if the robot did not execute anything this turn.
                    // When the robot executes the currentTurn will automatically be increased by 1,
                    // So when the turn stays the same, the robot did not take any action this turn.
                    if (lastTurn == currentTurn)
                    {
                        robot.peer.Rescan(); // Spend a turn on rescanning
                    }
                }
            }
        }
        // ReSharper restore FunctionNeverReturns
    }
}
//doc