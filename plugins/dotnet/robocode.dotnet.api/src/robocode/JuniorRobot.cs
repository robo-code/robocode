/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Nutch Poovarawan from Cubic Creative
 *     - The design and ideas for the JuniorRobot class
 *     Flemming N. Larsen
 *     - Implementor of the JuniorRobot
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
using System;
using System.Drawing;
using System.IO;
using robocode.exception;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using robocode.util;

namespace robocode
{
    /// <summary>
    /// This is the simplest robot type, which is simpler than the {@link Robot} and
    /// {@link AdvancedRobot} classes. The JuniorRobot has a simplified model, in
    /// purpose of teaching programming skills to inexperienced in programming
    /// students. The simplified robot model will keep player from overwhelming of
    /// Robocode's rules, programming syntax and programming concept.
    /// <p/>
    /// Instead of using getters and setters, public fields are provided for
    /// receiving information like the last scanned robot, the coordinate of the
    /// robot etc.
    /// <p/>
    /// All methods on this class are blocking calls, i.e. they do not return before
    /// their action has been completed and will at least take one turn to Execute.
    /// However, setting colors is executed immediately and does not cost a turn to
    /// perform.
    ///
    /// @author Nutch Poovarawan from Cubic Creative (designer)
    /// @author Flemming N. Larsen (implementor)
    /// @author Pavel Savara (contributor)
    /// @see Robot
    /// @see AdvancedRobot
    /// @see TeamRobot
    /// @see Droid
    /// @since 1.4
    /// </summary>
    public abstract class JuniorRobot : IJuniorRobot
    {
        internal IBasicRobotPeer peer;
        internal TextWriter _output;

        /// <summary>
        /// The color black (0x000000)
        /// </summary>
        public static readonly int black = 0x000000;

        /// <summary>
        /// The color white (0xFFFFFF)
        /// </summary>
        public static readonly int white = 0xFFFFFF;

        /// <summary>
        /// The color red  (0xFF0000)
        /// </summary>
        public static readonly int red = 0xFF0000;

        /// <summary>
        /// The color orange (0xFFA500)
        /// </summary>
        public static readonly int orange = 0xFFA500;

        /// <summary>
        /// The color yellow (0xFFFF00)
        /// </summary>
        public static readonly int yellow = 0xFFFF00;

        /// <summary>
        /// The color green (0x008000)
        /// </summary>
        public static readonly int green = 0x008000;

        /// <summary>
        /// The color blue (0x0000FF)
        /// </summary>
        public static readonly int blue = 0x0000FF;

        /// <summary>
        /// The color purple (0x800080)
        /// </summary>
        public static readonly int purple = 0x800080;

        /// <summary>
        /// The color brown (0x8B4513)
        /// </summary>
        public static readonly int brown = 0x8B4513;

        /// <summary>
        /// The color gray (0x808080)
        /// </summary>
        public static readonly int gray = 0x808080;

        /// <summary>
        /// Contains the width of the battlefield.
        ///
        /// @see #fieldWidth
        /// </summary>
        private int fieldWidth;

        /// <summary>
        /// Contains the height of the battlefield.
        ///
        /// @see #fieldWidth
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
        /// @see #robotY
        /// </summary>
        private int robotX;

        /// <summary>
        /// Current vertical location of this robot (in pixels).
        ///
        /// @see #robotX
        /// </summary>
        private int robotY;

        /// <summary>
        /// Current heading angle of this robot (in degrees).
        ///
        /// @see #TurnLeft(int)
        /// @see #TurnRight(int)
        /// @see #TurnTo(int)
        /// @see #TurnAheadLeft(int, int)
        /// @see #TurnAheadRight(int, int)
        /// @see #TurnBackLeft(int, int)
        /// @see #TurnBackRight(int, int)
        /// </summary>
        private int heading;

        /// <summary>
        /// Current gun heading angle of this robot (in degrees).
        ///
        /// @see #gunBearing
        /// @see #TurnGunLeft(int)
        /// @see #TurnGunRight(int)
        /// @see #TurnGunTo(int)
        /// @see #BearGunTo(int)
        /// </summary>
        private int gunHeading;

        /// <summary>
        /// Current gun heading angle of this robot compared to its body (in degrees).
        ///
        /// @see #gunHeading
        /// @see #TurnGunLeft(int)
        /// @see #TurnGunRight(int)
        /// @see #TurnGunTo(int)
        /// @see #BearGunTo(int)
        /// </summary>
        private int gunBearing;

        /// <summary>
        /// Flag specifying if the gun is ready to Fire, i.e. gun heat &lt;= 0.
        /// {@code true} means that the gun is able to Fire; {@code false}
        /// means that the gun cannot Fire yet as it still needs to cool down.
        ///
        /// @see #Fire()
        /// @see #Fire(double)
        /// </summary>
        private bool gunReady;

        /// <summary>
        /// Current distance to the scanned nearest other robot (in pixels).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #OnScannedRobot()} evnt is active.
        ///
        /// @see #OnScannedRobot()
        /// @see #scannedAngle
        /// @see #scannedBearing
        /// @see #scannedEnergy
        /// @see #scannedVelocity
        /// @see #scannedHeading
        /// </summary>
        private int scannedDistance = -1;

        /// <summary>
        /// Current angle to the scanned nearest other robot (in degrees).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #OnScannedRobot()} evnt is active.
        ///
        /// @see #OnScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedBearing
        /// @see #scannedEnergy
        /// @see #scannedVelocity
        /// @see #scannedHeading
        /// </summary>
        private int scannedAngle = -1;

        /// <summary>
        /// Current angle to the scanned nearest other robot (in degrees) compared to
        /// the body of this robot.
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #OnScannedRobot()} evnt is active.
        ///
        /// @see #OnScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedAngle
        /// @see #scannedEnergy
        /// @see #scannedVelocity
        /// @see #scannedHeading
        /// </summary>
        private int scannedBearing = -1;

        /// <summary>
        /// Current velocity of the scanned nearest other robot.
        /// If there is no robot in the radar's sight, this field will be -99.
        /// Note that a positive value means that the robot moves forward, a negative
        /// value means that the robot moved backward, and 0 means that the robot is
        /// not moving at all.
        /// This field will not be updated while {@link #OnScannedRobot()} evnt is active.
        ///
        /// @see #OnScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedAngle
        /// @see #scannedBearing
        /// @see #scannedEnergy
        /// @see #scannedHeading
        /// </summary>
        private int scannedVelocity = -99;

        /// <summary>
        /// Current heading of the scanned nearest other robot (in degrees).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #OnScannedRobot()} evnt is active.
        ///
        /// @see #OnScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedAngle
        /// @see #scannedBearing
        /// @see #scannedEnergy
        /// @see #scannedVelocity
        /// </summary>
        private int scannedHeading = -1;

        /// <summary>
        /// Current energy of scanned nearest other robot.
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #OnScannedRobot()} evnt is active.
        ///
        /// @see #OnScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedAngle
        /// @see #scannedBearing
        /// @see #scannedVelocity
        /// </summary>
        private int scannedEnergy = -1;

        /// <summary>
        /// Latest angle from where this robot was hit by a bullet (in degrees).
        /// If the robot has never been hit, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #OnHitByBullet()} evnt is active.
        ///
        /// @see #OnHitByBullet()
        /// @see #hitByBulletBearing
        /// </summary>
        private int hitByBulletAngle = -1;

        /// <summary>
        /// Latest angle from where this robot was hit by a bullet (in degrees)
        /// compared to the body of this robot.
        /// If the robot has never been hit, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #OnHitByBullet()} evnt is active.
        ///
        /// @see #OnHitByBullet()
        /// @see #hitByBulletAngle
        /// </summary>
        private int hitByBulletBearing = -1;

        /// <summary>
        /// Latest angle where this robot has hit another robot (in degrees).
        /// If this robot has never hit another robot, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #OnHitRobot()} evnt is active.
        ///
        /// @see #OnHitRobot()
        /// @see #hitRobotBearing
        /// </summary>
        private int hitRobotAngle = -1;

        /// <summary>
        /// Latest angle where this robot has hit another robot (in degrees)
        /// compared to the body of this robot.
        /// If this robot has never hit another robot, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #OnHitRobot()} evnt is active.
        ///
        /// @see #OnHitRobot()
        /// @see #hitRobotAngle
        /// </summary>
        private int hitRobotBearing = -1;

        /// <summary>
        /// Latest angle where this robot has hit a wall (in degrees).
        /// If this robot has never hit a wall, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #OnHitWall()} evnt is active.
        ///
        /// @see #OnHitWall()
        /// @see #hitWallBearing
        /// </summary>
        private int hitWallAngle = -1;

        /// <summary>
        /// Latest angle where this robot has hit a wall (in degrees)
        /// compared to the body of this robot.
        /// If this robot has never hit a wall, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #OnHitWall()} evnt is active.
        ///
        /// @see #OnHitWall()
        /// @see #hitWallAngle
        /// </summary>
        private int hitWallBearing = -1;

        /// <summary>
        /// The robot evnt handler for this robot.
        /// </summary>
        private InnerEventHandler innerEventHandler;


        /// <summary>
        /// The Out stream your robot should use to print.
        /// <p/>
        /// You can view the print-outs by clicking the button for your robot in the
        /// right side of the battle window.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Print Out a line each time my robot hits another robot
        ///   public void OnHitRobot(HitRobotEvent e) {
        ///       Out.println("I hit a robot!  My energy: " + getEnergy() + " his energy: " + e.getEnergy());
        ///   }
        /// </pre>
        /// </summary>
        public TextWriter Out
        {
            get { return _output; }
        }

        /// <summary>
        /// Contains the width of the battlefield.
        ///
        /// @see #fieldWidth
        /// </summary>
        public int FieldWidth
        {
            get { return fieldWidth; }
        }

        /// <summary>
        /// Contains the height of the battlefield.
        ///
        /// @see #fieldWidth
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
        /// @see #robotY
        /// </summary>
        public int RobotX
        {
            get { return robotX; }
        }

        /// <summary>
        /// Current vertical location of this robot (in pixels).
        ///
        /// @see #robotX
        /// </summary>
        public int RobotY
        {
            get { return robotY; }
        }

        /// <summary>
        /// Current heading angle of this robot (in degrees).
        ///
        /// @see #TurnLeft(int)
        /// @see #TurnRight(int)
        /// @see #TurnTo(int)
        /// @see #TurnAheadLeft(int, int)
        /// @see #TurnAheadRight(int, int)
        /// @see #TurnBackLeft(int, int)
        /// @see #TurnBackRight(int, int)
        /// </summary>
        public int Heading
        {
            get { return heading; }
        }

        /// <summary>
        /// Current gun heading angle of this robot (in degrees).
        ///
        /// @see #gunBearing
        /// @see #TurnGunLeft(int)
        /// @see #TurnGunRight(int)
        /// @see #TurnGunTo(int)
        /// @see #BearGunTo(int)
        /// </summary>
        public int GunHeading
        {
            get { return gunHeading; }
        }

        /// <summary>
        /// Current gun heading angle of this robot compared to its body (in degrees).
        ///
        /// @see #gunHeading
        /// @see #TurnGunLeft(int)
        /// @see #TurnGunRight(int)
        /// @see #TurnGunTo(int)
        /// @see #BearGunTo(int)
        /// </summary>
        public int GunBearing
        {
            get { return gunBearing; }
        }

        /// <summary>
        /// Flag specifying if the gun is ready to Fire, i.e. gun heat &lt;= 0.
        /// {@code true} means that the gun is able to Fire; {@code false}
        /// means that the gun cannot Fire yet as it still needs to cool down.
        ///
        /// @see #Fire()
        /// @see #Fire(double)
        /// </summary>
        public bool IsGunReady
        {
            get { return gunReady; }
        }

        /// <summary>
        /// Current distance to the scanned nearest other robot (in pixels).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #OnScannedRobot()} evnt is active.
        ///
        /// @see #OnScannedRobot()
        /// @see #scannedAngle
        /// @see #scannedBearing
        /// @see #scannedEnergy
        /// @see #scannedVelocity
        /// @see #scannedHeading
        /// </summary>
        public int ScannedDistance
        {
            get { return scannedDistance; }
        }

        /// <summary>
        /// Current angle to the scanned nearest other robot (in degrees).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #OnScannedRobot()} evnt is active.
        ///
        /// @see #OnScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedBearing
        /// @see #scannedEnergy
        /// @see #scannedVelocity
        /// @see #scannedHeading
        /// </summary>
        public int ScannedAngle
        {
            get { return scannedAngle; }
        }

        /// <summary>
        /// Current angle to the scanned nearest other robot (in degrees) compared to
        /// the body of this robot.
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #OnScannedRobot()} evnt is active.
        ///
        /// @see #OnScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedAngle
        /// @see #scannedEnergy
        /// @see #scannedVelocity
        /// @see #scannedHeading
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
        /// This field will not be updated while {@link #OnScannedRobot()} evnt is active.
        ///
        /// @see #OnScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedAngle
        /// @see #scannedBearing
        /// @see #scannedEnergy
        /// @see #scannedHeading
        /// </summary>
        public int ScannedVelocity
        {
            get { return scannedVelocity; }
        }

        /// <summary>
        /// Current heading of the scanned nearest other robot (in degrees).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #OnScannedRobot()} evnt is active.
        ///
        /// @see #OnScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedAngle
        /// @see #scannedBearing
        /// @see #scannedEnergy
        /// @see #scannedVelocity
        /// </summary>
        public int ScannedHeading
        {
            get { return scannedHeading; }
        }

        /// <summary>
        /// Current energy of scanned nearest other robot.
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #OnScannedRobot()} evnt is active.
        ///
        /// @see #OnScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedAngle
        /// @see #scannedBearing
        /// @see #scannedVelocity
        /// </summary>
        public int ScannedEnergy
        {
            get { return scannedEnergy; }
        }

        /// <summary>
        /// Latest angle from where this robot was hit by a bullet (in degrees).
        /// If the robot has never been hit, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #OnHitByBullet()} evnt is active.
        ///
        /// @see #OnHitByBullet()
        /// @see #hitByBulletBearing
        /// </summary>
        public int HitByBulletAngle
        {
            get { return hitByBulletAngle; }
        }

        /// <summary>
        /// Latest angle from where this robot was hit by a bullet (in degrees)
        /// compared to the body of this robot.
        /// If the robot has never been hit, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #OnHitByBullet()} evnt is active.
        ///
        /// @see #OnHitByBullet()
        /// @see #hitByBulletAngle
        /// </summary>
        public int HitByBulletBearing
        {
            get { return hitByBulletBearing; }
        }

        /// <summary>
        /// Latest angle where this robot has hit another robot (in degrees).
        /// If this robot has never hit another robot, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #OnHitRobot()} evnt is active.
        ///
        /// @see #OnHitRobot()
        /// @see #hitRobotBearing
        /// </summary>
        public int HitRobotAngle
        {
            get { return hitRobotAngle; }
        }

        /// <summary>
        /// Latest angle where this robot has hit another robot (in degrees)
        /// compared to the body of this robot.
        /// If this robot has never hit another robot, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #OnHitRobot()} evnt is active.
        ///
        /// @see #OnHitRobot()
        /// @see #hitRobotAngle
        /// </summary>
        public int HitRobotBearing
        {
            get { return hitRobotBearing; }
        }

        /// <summary>
        /// Latest angle where this robot has hit a wall (in degrees).
        /// If this robot has never hit a wall, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #OnHitWall()} evnt is active.
        ///
        /// @see #OnHitWall()
        /// @see #hitWallBearing
        /// </summary>
        public int HitWallAngle
        {
            get { return hitWallAngle; }
        }

        /// <summary>
        /// Latest angle where this robot has hit a wall (in degrees)
        /// compared to the body of this robot.
        /// If this robot has never hit a wall, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #OnHitWall()} evnt is active.
        ///
        /// @see #OnHitWall()
        /// @see #hitWallAngle
        /// </summary>
        public int HitWallBearing
        {
            get { return hitWallBearing; }
        }

        /// <inheritdoc />
        void IBasicRobot.SetOut(TextWriter outpt)
        {
            _output = outpt;
        }

        /// <inheritdoc />
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

        /// <inheritdoc />
        IRunnable IBasicRobot.GetRobotRunnable()
        {
            return getEventHandler();
        }

        /// <inheritdoc />
        IBasicEvents IBasicRobot.GetBasicEventListener()
        {
            return getEventHandler();
        }


        /// <summary>
        /// Moves this robot forward by pixels.
        ///
        /// @param distance the amount of pixels to move forward
        /// @see #Back(int)
        /// @see #robotX
        /// @see #robotY
        /// </summary>
        public void Ahead(int distance)
        {
            if (peer != null)
            {
                peer.move(distance);
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Moves this robot backward by pixels.
        ///
        /// @param distance the amount of pixels to move backward
        /// @see #Ahead(int)
        /// @see #robotX
        /// @see #robotY
        /// </summary>
        public void Back(int distance)
        {
            Ahead(-distance);
        }

        /// <summary>
        /// Turns the gun to the specified angle (in degrees) relative to body of this robot.
        /// The gun will turn to the side with the shortest delta angle to the specified angle.
        ///
        /// @param angle the angle to turn the gun to relative to the body of this robot
        /// @see #gunHeading
        /// @see #gunBearing
        /// @see #TurnGunLeft(int)
        /// @see #TurnGunRight(int)
        /// @see #TurnGunTo(int)
        /// </summary>
        public void BearGunTo(int angle)
        {
            if (peer != null)
            {
                peer.turnGun(
                    Utils.NormalRelativeAngle(peer.getBodyHeading() + Utils.ToRadians(angle) - peer.getGunHeading()));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Skips a turn.
        ///
        /// @see #DoNothing(int)
        /// </summary>
        public void DoNothing()
        {
            if (peer != null)
            {
                peer.execute();
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Skips the specified number of turns.
        ///
        /// @param turns the number of turns to skip
        /// @see #DoNothing()
        /// </summary>
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
                    peer.execute();
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
        /// @see #gunReady
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
        ///
        /// @param power between 0.1 and 3
        /// @see #gunReady
        /// </summary>
        public void Fire(double power)
        {
            if (peer != null)
            {
                getEventHandler().juniorFirePower = power;
                peer.execute();
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// This evnt methods is called from the game when this robot has been hit
        /// by another robot's bullet. When this evnt occurs the
        /// {@link #hitByBulletAngle} and {@link #hitByBulletBearing} fields values
        /// are automatically updated.
        ///
        /// @see #hitByBulletAngle
        /// @see #hitByBulletBearing
        /// </summary>
        public virtual void OnHitByBullet()
        {
        }

        /// <summary>
        /// This evnt methods is called from the game when a bullet from this robot
        /// has hit another robot. When this evnt occurs the {@link #hitRobotAngle}
        /// and {@link #hitRobotBearing} fields values are automatically updated.
        ///
        /// @see #hitRobotAngle
        /// @see #hitRobotBearing
        /// </summary>
        public virtual void OnHitRobot()
        {
        }

        /// <summary>
        /// This evnt methods is called from the game when this robot has hit a wall.
        /// When this evnt occurs the {@link #hitWallAngle} and {@link #hitWallBearing}
        /// fields values are automatically updated.
        ///
        /// @see #hitWallAngle
        /// @see #hitWallBearing
        /// </summary>
        public virtual void OnHitWall()
        {
        }

        /// <summary>
        /// This evnt method is called from the game when the radar detects another
        /// robot. When this evnt occurs the {@link #scannedDistance},
        /// {@link #scannedAngle}, {@link #scannedBearing}, and {@link #scannedEnergy}
        /// field values are automatically updated.
        ///
        /// @see #scannedDistance
        /// @see #scannedAngle
        /// @see #scannedBearing
        /// @see #scannedEnergy
        /// </summary>
        public virtual void OnScannedRobot()
        {
        }

        /// <summary>
        /// The main method in every robot. You must override this to set up your
        /// robot's basic behavior.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // A basic robot that moves around in a square
        ///   public void Run() {
        ///       Ahead(100);
        ///       TurnRight(90);
        ///   }
        /// </pre>
        /// This method is automatically re-called when it has returned.
        /// </summary>
        public virtual void Run()
        {
        }

        /// <summary>
        /// Sets the colors of the robot. The color values are RGB values.
        /// You can use the colors that are already defined for this class.
        ///
        /// @param bodyColor  the RGB color value for the body
        /// @param gunColor   the RGB color value for the gun
        /// @param radarColor the RGB color value for the radar
        /// @see #SetColors(int, int, int, int, int)
        /// </summary>
        public void SetColors(int bodyColor, int gunColor, int radarColor)
        {
            if (peer != null)
            {
                peer.setBodyColor(Color.FromArgb(bodyColor));
                peer.setGunColor(Color.FromArgb(gunColor));
                peer.setRadarColor(Color.FromArgb(radarColor));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Sets the colors of the robot. The color values are RGB values.
        /// You can use the colors that are already defined for this class.
        ///
        /// @param bodyColor	the RGB color value for the body
        /// @param gunColor	 the RGB color value for the gun
        /// @param radarColor   the RGB color value for the radar
        /// @param bulletColor  the RGB color value for the bullets
        /// @param scanArcColor the RGB color value for the Scan arc
        /// @see #SetColors(int, int, int)
        /// </summary>
        public void SetColors(int bodyColor, int gunColor, int radarColor, int bulletColor, int scanArcColor)
        {
            if (peer != null)
            {
                peer.setBodyColor(Color.FromArgb(bodyColor));
                peer.setGunColor(Color.FromArgb(gunColor));
                peer.setRadarColor(Color.FromArgb(radarColor));
                peer.setBulletColor(Color.FromArgb(bulletColor));
                peer.setScanColor(Color.FromArgb(scanArcColor));
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
        ///
        /// @param distance the amount of pixels to move forward
        /// @param degrees  the amount of degrees to turn to the left
        /// @see #heading
        /// @see #robotX
        /// @see #robotY
        /// @see #TurnLeft(int)
        /// @see #TurnRight(int)
        /// @see #TurnTo(int)
        /// @see #TurnAheadRight(int, int)
        /// @see #TurnBackLeft(int, int)
        /// @see #TurnBackRight(int, int)
        /// </summary>
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
        ///
        /// @param distance the amount of pixels to move forward
        /// @param degrees  the amount of degrees to turn to the right
        /// @see #heading
        /// @see #robotX
        /// @see #robotY
        /// @see #TurnLeft(int)
        /// @see #TurnRight(int)
        /// @see #TurnTo(int)
        /// @see #TurnAheadLeft(int, int)
        /// @see #TurnBackLeft(int, int)
        /// @see #TurnBackRight(int, int)
        /// </summary>
        public void TurnAheadRight(int distance, int degrees)
        {
            if (peer != null)
            {
                ((IJuniorRobotPeer) peer).turnAndMove(distance, Utils.ToRadians(degrees));
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
        ///
        /// @param distance the amount of pixels to move backward
        /// @param degrees  the amount of degrees to turn to the left
        /// @see #heading
        /// @see #robotX
        /// @see #robotY
        /// @see #TurnLeft(int)
        /// @see #TurnRight(int)
        /// @see #TurnTo(int)
        /// @see #TurnAheadLeft(int, int)
        /// @see #TurnAheadRight(int, int)
        /// @see #TurnBackRight(int, int)
        /// </summary>
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
        ///
        /// @param distance the amount of pixels to move backward
        /// @param degrees  the amount of degrees to turn to the right
        /// @see #heading
        /// @see #robotX
        /// @see #robotY
        /// @see #TurnLeft(int)
        /// @see #TurnRight(int)
        /// @see #TurnTo(int)
        /// @see #TurnAheadLeft(int, int)
        /// @see #TurnAheadRight(int, int)
        /// @see #TurnBackLeft(int, int)
        /// </summary>
        public void TurnBackRight(int distance, int degrees)
        {
            TurnAheadRight(-distance, -degrees);
        }

        /// <summary>
        /// Turns the gun left by degrees.
        ///
        /// @param degrees the amount of degrees to turn the gun to the left
        /// @see #gunHeading
        /// @see #gunBearing
        /// @see #TurnGunRight(int)
        /// @see #TurnGunTo(int)
        /// @see #BearGunTo(int)
        /// </summary>
        public void TurnGunLeft(int degrees)
        {
            TurnGunRight(-degrees);
        }

        /// <summary>
        /// Turns the gun right by degrees.
        ///
        /// @param degrees the amount of degrees to turn the gun to the right
        /// @see #gunHeading
        /// @see #gunBearing
        /// @see #TurnGunLeft(int)
        /// @see #TurnGunTo(int)
        /// @see #BearGunTo(int)
        /// </summary>
        public void TurnGunRight(int degrees)
        {
            if (peer != null)
            {
                peer.turnGun(Utils.ToRadians(degrees));
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
        ///
        /// @param angle the angle to turn the gun to
        /// @see #gunHeading
        /// @see #gunBearing
        /// @see #TurnGunLeft(int)
        /// @see #TurnGunRight(int)
        /// @see #BearGunTo(int)
        /// </summary>
        public void TurnGunTo(int angle)
        {
            if (peer != null)
            {
                peer.turnGun(Utils.NormalRelativeAngle(Utils.ToRadians(angle) - peer.getGunHeading()));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Turns this robot left by degrees.
        ///
        /// @param degrees the amount of degrees to turn to the left
        /// @see #heading
        /// @see #TurnRight(int)
        /// @see #TurnTo(int)
        /// @see #TurnAheadLeft(int, int)
        /// @see #TurnAheadRight(int, int)
        /// @see #TurnBackLeft(int, int)
        /// @see #TurnBackRight(int, int)
        /// </summary>
        public void TurnLeft(int degrees)
        {
            TurnRight(-degrees);
        }

        /// <summary>
        /// Turns this robot right by degrees.
        ///
        /// @param degrees the amount of degrees to turn to the right
        /// @see #heading
        /// @see #TurnLeft(int)
        /// @see #TurnTo(int)
        /// @see #TurnAheadLeft(int, int)
        /// @see #TurnAheadRight(int, int)
        /// @see #TurnBackLeft(int, int)
        /// @see #TurnBackRight(int, int)
        /// </summary>
        public void TurnRight(int degrees)
        {
            if (peer != null)
            {
                peer.turnBody(Utils.ToRadians(degrees));
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
        ///
        /// @param angle the angle to turn this robot to
        /// @see #heading
        /// @see #TurnLeft(int)
        /// @see #TurnRight(int)
        /// @see #TurnAheadLeft(int, int)
        /// @see #TurnAheadRight(int, int)
        /// @see #TurnBackLeft(int, int)
        /// @see #TurnBackRight(int, int)
        /// </summary>
        public void TurnTo(int angle)
        {
            if (peer != null)
            {
                peer.turnBody(Utils.NormalRelativeAngle(Utils.ToRadians(angle) - peer.getBodyHeading()));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        /// Returns the evnt handler of this robot.
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
        /// The JuniorRobot evnt handler, which  :  the basic robot events,
        /// JuniorRobot evnt, and IRunnable.
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
                double angle = robot.peer.getBodyHeading() + evnt.BearingRadians;

                robot.hitByBulletAngle = (int) (Utils.ToDegrees(Utils.NormalAbsoluteAngle(angle)) + 0.5);
                robot.hitByBulletBearing = (int) (evnt.Bearing + 0.5);
                robot.OnHitByBullet();
            }

            public void OnHitRobot(HitRobotEvent evnt)
            {
                double angle = robot.peer.getBodyHeading() + evnt.BearingRadians;

                robot.hitRobotAngle = (int) (Utils.ToDegrees(Utils.NormalAbsoluteAngle(angle)) + 0.5);
                robot.hitRobotBearing = (int) (evnt.Bearing + 0.5);
                robot.OnHitRobot();
            }

            public void OnHitWall(HitWallEvent evnt)
            {
                double angle = robot.peer.getBodyHeading() + evnt.BearingRadians;

                robot.hitWallAngle = (int) (Utils.ToDegrees(Utils.NormalAbsoluteAngle(angle)) + 0.5);
                robot.hitWallBearing = (int) (evnt.Bearing + 0.5);
                robot.OnHitWall();
            }

            public void OnRobotDeath(RobotDeathEvent evnt)
            {
                robot.others = robot.peer.getOthers();
            }

            public void OnScannedRobot(ScannedRobotEvent evnt)
            {
                robot.scannedDistance = (int) (evnt.Distance + 0.5);
                robot.scannedEnergy = Math.Max(1, (int) (evnt.Energy + 0.5));
                robot.scannedAngle = (int) (Utils.ToDegrees(
                                                Utils.NormalAbsoluteAngle(robot.peer.getBodyHeading() +
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

                robot.others = robot.peer.getOthers();
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
                if (juniorFirePower > 0 && robot.gunReady && (robot.peer.getGunTurnRemaining() == 0))
                {
                    if (robot.peer.setFire(juniorFirePower) != null)
                    {
                        robot.gunReady = false;
                        juniorFirePower = 0;
                    }
                }

                // Reset evnt data
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

            public void Run()
            {
                robot.fieldWidth = (int) (robot.peer.getBattleFieldWidth() + 0.5);
                robot.fieldHeight = (int) (robot.peer.getBattleFieldHeight() + 0.5);

                // noinspection InfiniteLoopStatement
                while (true)
                {
                    long lastTurn = currentTurn; // Used for the rescan check

                    robot.Run(); // Run the code in the JuniorRobot

                    // Make sure that we rescan if the robot did not Execute anything this turn.
                    // When the robot executes the currentTurn will automatically be increased by 1,
                    // So when the turn stays the same, the robot did not take any action this turn.
                    if (lastTurn == currentTurn)
                    {
                        robot.peer.rescan(); // Spend a turn on rescanning
                    }
                }
            }
        }
    }
}

//happy