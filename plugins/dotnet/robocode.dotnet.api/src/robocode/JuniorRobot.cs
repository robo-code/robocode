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
using robocode.robocode.robotinterfaces;
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
    /// their action has been completed and will at least take one turn to execute.
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
    public class JuniorRobot : IJuniorRobot
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
        public int fieldWidth;

        /// <summary>
        /// Contains the height of the battlefield.
        ///
        /// @see #fieldWidth
        /// </summary>
        public int fieldHeight;

        /// <summary>
        /// Current number of other robots on the battle field.
        /// </summary>
        public int others;

        /// <summary>
        /// Current energy of this robot, where 100 means full energy and 0 means no energy (dead).
        /// </summary>
        public int energy;

        /// <summary>
        /// Current horizontal location of this robot (in pixels).
        ///
        /// @see #robotY
        /// </summary>
        public int robotX;

        /// <summary>
        /// Current vertical location of this robot (in pixels).
        ///
        /// @see #robotX
        /// </summary>
        public int robotY;

        /// <summary>
        /// Current heading angle of this robot (in degrees).
        ///
        /// @see #turnLeft(int)
        /// @see #turnRight(int)
        /// @see #turnTo(int)
        /// @see #turnAheadLeft(int, int)
        /// @see #turnAheadRight(int, int)
        /// @see #turnBackLeft(int, int)
        /// @see #turnBackRight(int, int)
        /// </summary>
        public int heading;

        /// <summary>
        /// Current gun heading angle of this robot (in degrees).
        ///
        /// @see #gunBearing
        /// @see #turnGunLeft(int)
        /// @see #turnGunRight(int)
        /// @see #turnGunTo(int)
        /// @see #bearGunTo(int)
        /// </summary>
        public int gunHeading;

        /// <summary>
        /// Current gun heading angle of this robot compared to its body (in degrees).
        ///
        /// @see #gunHeading
        /// @see #turnGunLeft(int)
        /// @see #turnGunRight(int)
        /// @see #turnGunTo(int)
        /// @see #bearGunTo(int)
        /// </summary>
        public int gunBearing;

        /// <summary>
        /// Flag specifying if the gun is ready to fire, i.e. gun heat <= 0.
        /// {@code true} means that the gun is able to fire; {@code false}
        /// means that the gun cannot fire yet as it still needs to cool down.
        ///
        /// @see #fire()
        /// @see #fire(double)
        /// </summary>
        public bool gunReady;

        /// <summary>
        /// Current distance to the scanned nearest other robot (in pixels).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #onScannedRobot()} evnt is active.
        ///
        /// @see #onScannedRobot()
        /// @see #scannedAngle
        /// @see #scannedBearing
        /// @see #scannedEnergy
        /// @see #scannedVelocity
        /// @see #scannedHeading
        /// </summary>
        public int scannedDistance = -1;

        /// <summary>
        /// Current angle to the scanned nearest other robot (in degrees).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #onScannedRobot()} evnt is active.
        ///
        /// @see #onScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedBearing
        /// @see #scannedEnergy
        /// @see #scannedVelocity
        /// @see #scannedHeading
        /// </summary>
        public int scannedAngle = -1;

        /// <summary>
        /// Current angle to the scanned nearest other robot (in degrees) compared to
        /// the body of this robot.
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #onScannedRobot()} evnt is active.
        ///
        /// @see #onScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedAngle
        /// @see #scannedEnergy
        /// @see #scannedVelocity
        /// @see #scannedHeading
        /// </summary>
        public int scannedBearing = -1;

        /// <summary>
        /// Current velocity of the scanned nearest other robot.
        /// If there is no robot in the radar's sight, this field will be -99.
        /// Note that a positive value means that the robot moves forward, a negative
        /// value means that the robot moved backward, and 0 means that the robot is
        /// not moving at all.
        /// This field will not be updated while {@link #onScannedRobot()} evnt is active.
        ///
        /// @see #onScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedAngle
        /// @see #scannedBearing
        /// @see #scannedEnergy
        /// @see #scannedHeading
        /// </summary>
        public int scannedVelocity = -99;

        /// <summary>
        /// Current heading of the scanned nearest other robot (in degrees).
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #onScannedRobot()} evnt is active.
        ///
        /// @see #onScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedAngle
        /// @see #scannedBearing
        /// @see #scannedEnergy
        /// @see #scannedVelocity
        /// </summary>
        public int scannedHeading = -1;

        /// <summary>
        /// Current energy of scanned nearest other robot.
        /// If there is no robot in the radar's sight, this field will be less than 0, i.e -1.
        /// This field will not be updated while {@link #onScannedRobot()} evnt is active.
        ///
        /// @see #onScannedRobot()
        /// @see #scannedDistance
        /// @see #scannedAngle
        /// @see #scannedBearing
        /// @see #scannedVelocity
        /// </summary>
        public int scannedEnergy = -1;

        /// <summary>
        /// Latest angle from where this robot was hit by a bullet (in degrees).
        /// If the robot has never been hit, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #onHitByBullet()} evnt is active.
        ///
        /// @see #onHitByBullet()
        /// @see #hitByBulletBearing
        /// </summary>
        public int hitByBulletAngle = -1;

        /// <summary>
        /// Latest angle from where this robot was hit by a bullet (in degrees)
        /// compared to the body of this robot.
        /// If the robot has never been hit, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #onHitByBullet()} evnt is active.
        ///
        /// @see #onHitByBullet()
        /// @see #hitByBulletAngle
        /// </summary>
        public int hitByBulletBearing = -1;

        /// <summary>
        /// Latest angle where this robot has hit another robot (in degrees).
        /// If this robot has never hit another robot, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #onHitRobot()} evnt is active.
        ///
        /// @see #onHitRobot()
        /// @see #hitRobotBearing
        /// </summary>
        public int hitRobotAngle = -1;

        /// <summary>
        /// Latest angle where this robot has hit another robot (in degrees)
        /// compared to the body of this robot.
        /// If this robot has never hit another robot, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #onHitRobot()} evnt is active.
        ///
        /// @see #onHitRobot()
        /// @see #hitRobotAngle
        /// </summary>
        public int hitRobotBearing = -1;

        /// <summary>
        /// Latest angle where this robot has hit a wall (in degrees).
        /// If this robot has never hit a wall, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #onHitWall()} evnt is active.
        ///
        /// @see #onHitWall()
        /// @see #hitWallBearing
        /// </summary>
        public int hitWallAngle = -1;

        /// <summary>
        /// Latest angle where this robot has hit a wall (in degrees)
        /// compared to the body of this robot.
        /// If this robot has never hit a wall, this field will be less than 0, i.e. -1.
        /// This field will not be updated while {@link #onHitWall()} evnt is active.
        ///
        /// @see #onHitWall()
        /// @see #hitWallAngle
        /// </summary>
        public int hitWallBearing = -1;

        /// <summary>
        /// The robot evnt handler for this robot.
        /// </summary>
        private InnerEventHandler innerEventHandler;


        /// <summary>
        /// The output stream your robot should use to print.
        /// <p/>
        /// You can view the print-outs by clicking the button for your robot in the
        /// right side of the battle window.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Print output a line each time my robot hits another robot
        ///   public void onHitRobot(HitRobotEvent e) {
        ///       output.println("I hit a robot!  My energy: " + getEnergy() + " his energy: " + e.getEnergy());
        ///   }
        /// </pre>
        /// </summary>
        public TextWriter output
        {
            get { return _output; }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        void IBasicRobot.setOut(TextWriter outpt)
        {
            _output = outpt;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        void IBasicRobot.setPeer(IBasicRobotPeer per)
        {
            peer = per;
        }

        /// <summary>
        /// Throws a RobotException. This method should be called when the robot's peer
        /// is uninitialized.
        /// </summary>
        internal static void uninitializedException()
        {
            throw new RobotException(
                "You cannot call the methods before your run() method is called, or you are using a Robot object that the game doesn't know about.");
        }

        /// <summary>
        /// {@inheritDoc}}
        /// </summary>
        Runnable IBasicRobot.getRobotRunnable()
        {
            return getEventHandler();
        }

        /// <summary>
        /// {@inheritDoc}}
        /// </summary>
        IBasicEvents IBasicRobot.getBasicEventListener()
        {
            return getEventHandler();
        }


        /// <summary>
        /// Moves this robot forward by pixels.
        ///
        /// @param distance the amount of pixels to move forward
        /// @see #back(int)
        /// @see #robotX
        /// @see #robotY
        /// </summary>
        public void ahead(int distance)
        {
            if (peer != null)
            {
                peer.move(distance);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Moves this robot backward by pixels.
        ///
        /// @param distance the amount of pixels to move backward
        /// @see #ahead(int)
        /// @see #robotX
        /// @see #robotY
        /// </summary>
        public void back(int distance)
        {
            ahead(-distance);
        }

        /// <summary>
        /// Turns the gun to the specified angle (in degrees) relative to body of this robot.
        /// The gun will turn to the side with the shortest delta angle to the specified angle.
        ///
        /// @param angle the angle to turn the gun to relative to the body of this robot
        /// @see #gunHeading
        /// @see #gunBearing
        /// @see #turnGunLeft(int)
        /// @see #turnGunRight(int)
        /// @see #turnGunTo(int)
        /// </summary>
        public void bearGunTo(int angle)
        {
            if (peer != null)
            {
                peer.turnGun(
                    Utils.normalRelativeAngle(peer.getBodyHeading() + Utils.toRadians(angle) - peer.getGunHeading()));
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Skips a turn.
        ///
        /// @see #doNothing(int)
        /// </summary>
        public void doNothing()
        {
            if (peer != null)
            {
                peer.execute();
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Skips the specified number of turns.
        ///
        /// @param turns the number of turns to skip
        /// @see #doNothing()
        /// </summary>
        public void doNothing(int turns)
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
                uninitializedException();
            }
        }

        /// <summary>
        /// Fires a bullet with the default power of 1.
        /// If the gun heat is more than 0 and hence cannot fire, this method will
        /// suspend until the gun is ready to fire, and then fire a bullet.
        ///
        /// @see #gunReady
        /// </summary>
        public void fire()
        {
            fire(1);
        }

        /// <summary>
        /// Fires a bullet with the specified bullet power, which is between 0.1 and 3
        /// where 3 is the maximum bullet power.
        /// If the gun heat is more than 0 and hence cannot fire, this method will
        /// suspend until the gun is ready to fire, and then fire a bullet.
        ///
        /// @param power between 0.1 and 3
        /// @see #gunReady
        /// </summary>
        public void fire(double power)
        {
            if (peer != null)
            {
                getEventHandler().juniorFirePower = power;
                peer.execute();
            }
            else
            {
                uninitializedException();
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
        public virtual void onHitByBullet()
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
        public virtual void onHitRobot()
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
        public virtual void onHitWall()
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
        public virtual void onScannedRobot()
        {
        }

        /// <summary>
        /// The main method in every robot. You must override this to set up your
        /// robot's basic behavior.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // A basic robot that moves around in a square
        ///   public void run() {
        ///       ahead(100);
        ///       turnRight(90);
        ///   }
        /// </pre>
        /// This method is automatically re-called when it has returned.
        /// </summary>
        public virtual void run()
        {
        }

        /// <summary>
        /// Sets the colors of the robot. The color values are RGB values.
        /// You can use the colors that are already defined for this class.
        ///
        /// @param bodyColor  the RGB color value for the body
        /// @param gunColor   the RGB color value for the gun
        /// @param radarColor the RGB color value for the radar
        /// @see #setColors(int, int, int, int, int)
        /// </summary>
        public void setColors(int bodyColor, int gunColor, int radarColor)
        {
            if (peer != null)
            {
                peer.setBodyColor(Color.FromArgb(bodyColor));
                peer.setGunColor(Color.FromArgb(gunColor));
                peer.setRadarColor(Color.FromArgb(radarColor));
            }
            else
            {
                uninitializedException();
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
        /// @param scanArcColor the RGB color value for the scan arc
        /// @see #setColors(int, int, int)
        /// </summary>
        public void setColors(int bodyColor, int gunColor, int radarColor, int bulletColor, int scanArcColor)
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
                uninitializedException();
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
        /// @see #turnLeft(int)
        /// @see #turnRight(int)
        /// @see #turnTo(int)
        /// @see #turnAheadRight(int, int)
        /// @see #turnBackLeft(int, int)
        /// @see #turnBackRight(int, int)
        /// </summary>
        public void turnAheadLeft(int distance, int degrees)
        {
            turnAheadRight(distance, -degrees);
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
        /// @see #turnLeft(int)
        /// @see #turnRight(int)
        /// @see #turnTo(int)
        /// @see #turnAheadLeft(int, int)
        /// @see #turnBackLeft(int, int)
        /// @see #turnBackRight(int, int)
        /// </summary>
        public void turnAheadRight(int distance, int degrees)
        {
            if (peer != null)
            {
                ((IJuniorRobotPeer) peer).turnAndMove(distance, Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
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
        /// @see #turnLeft(int)
        /// @see #turnRight(int)
        /// @see #turnTo(int)
        /// @see #turnAheadLeft(int, int)
        /// @see #turnAheadRight(int, int)
        /// @see #turnBackRight(int, int)
        /// </summary>
        public void turnBackLeft(int distance, int degrees)
        {
            turnAheadRight(-distance, degrees);
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
        /// @see #turnLeft(int)
        /// @see #turnRight(int)
        /// @see #turnTo(int)
        /// @see #turnAheadLeft(int, int)
        /// @see #turnAheadRight(int, int)
        /// @see #turnBackLeft(int, int)
        /// </summary>
        public void turnBackRight(int distance, int degrees)
        {
            turnAheadRight(-distance, -degrees);
        }

        /// <summary>
        /// Turns the gun left by degrees.
        ///
        /// @param degrees the amount of degrees to turn the gun to the left
        /// @see #gunHeading
        /// @see #gunBearing
        /// @see #turnGunRight(int)
        /// @see #turnGunTo(int)
        /// @see #bearGunTo(int)
        /// </summary>
        public void turnGunLeft(int degrees)
        {
            turnGunRight(-degrees);
        }

        /// <summary>
        /// Turns the gun right by degrees.
        ///
        /// @param degrees the amount of degrees to turn the gun to the right
        /// @see #gunHeading
        /// @see #gunBearing
        /// @see #turnGunLeft(int)
        /// @see #turnGunTo(int)
        /// @see #bearGunTo(int)
        /// </summary>
        public void turnGunRight(int degrees)
        {
            if (peer != null)
            {
                peer.turnGun(Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
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
        /// @see #turnGunLeft(int)
        /// @see #turnGunRight(int)
        /// @see #bearGunTo(int)
        /// </summary>
        public void turnGunTo(int angle)
        {
            if (peer != null)
            {
                peer.turnGun(Utils.normalRelativeAngle(Utils.toRadians(angle) - peer.getGunHeading()));
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Turns this robot left by degrees.
        ///
        /// @param degrees the amount of degrees to turn to the left
        /// @see #heading
        /// @see #turnRight(int)
        /// @see #turnTo(int)
        /// @see #turnAheadLeft(int, int)
        /// @see #turnAheadRight(int, int)
        /// @see #turnBackLeft(int, int)
        /// @see #turnBackRight(int, int)
        /// </summary>
        public void turnLeft(int degrees)
        {
            turnRight(-degrees);
        }

        /// <summary>
        /// Turns this robot right by degrees.
        ///
        /// @param degrees the amount of degrees to turn to the right
        /// @see #heading
        /// @see #turnLeft(int)
        /// @see #turnTo(int)
        /// @see #turnAheadLeft(int, int)
        /// @see #turnAheadRight(int, int)
        /// @see #turnBackLeft(int, int)
        /// @see #turnBackRight(int, int)
        /// </summary>
        public void turnRight(int degrees)
        {
            if (peer != null)
            {
                peer.turnBody(Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Turns this robot to the specified angle (in degrees).
        /// The robot will turn to the side with the shortest delta angle to the
        /// specified angle.
        ///
        /// @param angle the angle to turn this robot to
        /// @see #heading
        /// @see #turnLeft(int)
        /// @see #turnRight(int)
        /// @see #turnAheadLeft(int, int)
        /// @see #turnAheadRight(int, int)
        /// @see #turnBackLeft(int, int)
        /// @see #turnBackRight(int, int)
        /// </summary>
        public void turnTo(int angle)
        {
            if (peer != null)
            {
                peer.turnBody(Utils.normalRelativeAngle(Utils.toRadians(angle) - peer.getBodyHeading()));
            }
            else
            {
                uninitializedException();
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
        /// JuniorRobot evnt, and Runnable.
        /// </summary>
        private sealed class InnerEventHandler : IBasicEvents, Runnable
        {
            public InnerEventHandler(JuniorRobot robot)
            {
                this.robot = robot;
            }

            private JuniorRobot robot;
            internal double juniorFirePower;
            private long currentTurn;

            public void onBulletHit(BulletHitEvent evnt)
            {
            }

            public void onBulletHitBullet(BulletHitBulletEvent evnt)
            {
            }

            public void onBulletMissed(BulletMissedEvent evnt)
            {
            }

            public void onDeath(DeathEvent evnt)
            {
            }

            public void onHitByBullet(HitByBulletEvent evnt)
            {
                double angle = robot.peer.getBodyHeading() + evnt.getBearingRadians();

                robot.hitByBulletAngle = (int) (Utils.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
                robot.hitByBulletBearing = (int) (evnt.getBearing() + 0.5);
                robot.onHitByBullet();
            }

            public void onHitRobot(HitRobotEvent evnt)
            {
                double angle = robot.peer.getBodyHeading() + evnt.getBearingRadians();

                robot.hitRobotAngle = (int) (Utils.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
                robot.hitRobotBearing = (int) (evnt.getBearing() + 0.5);
                robot.onHitRobot();
            }

            public void onHitWall(HitWallEvent evnt)
            {
                double angle = robot.peer.getBodyHeading() + evnt.getBearingRadians();

                robot.hitWallAngle = (int) (Utils.toDegrees(Utils.normalAbsoluteAngle(angle)) + 0.5);
                robot.hitWallBearing = (int) (evnt.getBearing() + 0.5);
                robot.onHitWall();
            }

            public void onRobotDeath(RobotDeathEvent evnt)
            {
                robot.others = robot.peer.getOthers();
            }

            public void onScannedRobot(ScannedRobotEvent evnt)
            {
                robot.scannedDistance = (int) (evnt.getDistance() + 0.5);
                robot.scannedEnergy = Math.Max(1, (int) (evnt.getEnergy() + 0.5));
                robot.scannedAngle = (int) (Utils.toDegrees(
                                                Utils.normalAbsoluteAngle(robot.peer.getBodyHeading() +
                                                                          evnt.getBearingRadians()))
                                            + 0.5);
                robot.scannedBearing = (int) (evnt.getBearing() + 0.5);
                robot.scannedHeading = (int) (evnt.getHeading() + 0.5);
                robot.scannedVelocity = (int) (evnt.getVelocity() + 0.5);

                robot.onScannedRobot();
            }

            public void onStatus(StatusEvent e)
            {
                RobotStatus s = e.getStatus();

                robot.others = robot.peer.getOthers();
                robot.energy = Math.Max(1, (int) (s.getEnergy() + 0.5));
                robot.robotX = (int) (s.getX() + 0.5);
                robot.robotY = (int) (s.getY() + 0.5);
                robot.heading = (int) (Utils.toDegrees(s.getHeading()) + 0.5);
                robot.gunHeading = (int) (Utils.toDegrees(s.getGunHeading()) + 0.5);
                robot.gunBearing =
                    (int) (Utils.toDegrees(Utils.normalRelativeAngle(s.getGunHeading() - s.getHeading())) + 0.5);
                robot.gunReady = (s.getGunHeat() <= 0);

                currentTurn = e.getTime();

                // Auto fire
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

            public void onWin(WinEvent evnt)
            {
            }

            public void run()
            {
                robot.fieldWidth = (int) (robot.peer.getBattleFieldWidth() + 0.5);
                robot.fieldHeight = (int) (robot.peer.getBattleFieldHeight() + 0.5);

                // noinspection InfiniteLoopStatement
                while (true)
                {
                    long lastTurn = currentTurn; // Used for the rescan check

                    robot.run(); // Run the code in the JuniorRobot

                    // Make sure that we rescan if the robot did not execute anything this turn.
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