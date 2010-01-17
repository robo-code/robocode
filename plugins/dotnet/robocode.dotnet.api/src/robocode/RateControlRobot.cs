/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Joshua Galecki
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Small adjustments + extended javadocs
 *******************************************************************************/
using System;
using robocode.util;

namespace robocode
{
    /// <summary>
    /// This advanced robot type allows you to set a rate for each of the robot's movements.
    /// <p/>
    /// You can set the rate for:<ul>
    /// <li>velocity - pixels per turn</li>
    /// <li>robot turn - radians per turn</li>
    /// <li>gun rotation - radians per turn</li>
    /// <li>radar rotation - radians per turn</li>
    /// </ul>
    /// When you set a rate for one of the above movements, the movement will continue the move by
    /// specified rate for ever, until the rate is changed. In order to move Ahead or right, the
    /// rate must be set to a positive value. If a negative value is used instead, the movement
    /// will go Back or to the left. In order to Stop the movement, the rate must be
    /// set to 0.
    /// 
    /// Note: When calling {@code setVelocityRate()}, {@code setTurnRate()}, {@code setGunRotationRate()},
    /// {@code setRadarRotationRate()} and variants, Any previous calls to "movement" functions outside of
    /// {@code RateControlRobot}, such as {@code SetAhead()}, {@code SetTurnLeft()},
    /// {@code SetTurnRadarRightRadians()} and similar will be overridden when calling the
    /// {@link #Execute() Execute()} on this robot class.
    /// 
    /// Look into the source code for the {@code sample.VelociRobot} in order to see how to use this
    /// robot type. 
    ///
    /// @author Joshua Galecki
    /// @since 1.7.1.3
    /// </summary>
    public abstract class RateControlRobot : AdvancedRobot
    {
        private double velocityRate; // Pixels per turn
        private double turnRate; // Radians per turn
        private double gunRotationRate; // Radians per turn
        private double radarRotationRate; // Radians per turn

        /// <summary>
        /// Sets the speed the robot will move (forward), in pixels per turn.
        /// <p/>
        /// This call returns immediately, and will not Execute until you call
        /// Execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot will move backwards
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the robot to move forward 2 pixels per turn
        ///   setVelocityRate(2);
        ///
        ///   // Set the robot to move backwards 8 pixels per turn
        ///   // (overrides the previous order)
        ///   setVelocityRate(-8);
        ///
        ///   ...
        ///   // Executes the last setVelocityRate()
        ///   Execute();
        /// </pre>
        ///
        /// Note: This method overrules {@link robocode.AdvancedRobot#SetAhead(double)} and
        /// {@link robocode.AdvancedRobot#SetBack(double)}.
        ///
        /// @param velocityRate pixels per turn the robot will move.
        /// 
        /// @see #getVelocityRate()
        /// @see #setTurnRate(double)
        /// @see #setGunRotationRate(double)
        /// @see #setRadarRotationRate(double)
        /// @see AdvancedRobot#SetAhead(double)
        /// @see AdvancedRobot#SetBack(double)
        /// </summary>
        public double VelocityRate
        {
            get { return velocityRate; }
            set { velocityRate = value; }        }

        /// <summary>
        /// Sets the robot's clockwise (right) rotation per turn, in degrees.
        /// <p/>
        /// This call returns immediately, and will not Execute until you call
        /// Execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot turns counterclockwise (left)
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the robot to turn right 10 degrees per turn
        ///   setTurnRate(10);
        ///
        ///   // Set the robot to turn left 4 degrees per turn
        ///   // (overrides the previous order)
        ///   setTurnRate(-5);
        ///
        ///   ...
        ///   // Executes the last setTurnRate()
        ///   Execute();
        /// </pre>
        ///
        /// @param turnRate angle of the clockwise rotation, in degrees.
        ///
        /// @see #GetTurnRate()
        /// @see #setVelocityRate(double)
        /// @see #setGunRotationRate(double)
        /// @see #setRadarRotationRate(double)
        /// @see AdvancedRobot#SetTurnRight(double)
        /// @see AdvancedRobot#SetTurnLeft(double)
        /// </summary>
        public double TurnRate
        {
            get { return Utils.ToRadians(turnRate); }            set { turnRate = Utils.ToRadians(value); }        }

        /// <summary>
        /// Sets the robot's clockwise (right) rotation per turn, in radians.
        /// <p/>
        /// This call returns immediately, and will not Execute until you call
        /// Execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot turns counterclockwise (left)
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the robot to turn right pi / 32 radians per turn
        ///   setTurnRateRadians(Math.PI / 32);
        /// <p/>
        ///   // Set the robot to turn left pi / 20 radians per turn
        ///   // (overrides the previous order)
        ///   setTurnRateRadians(-Math.PI / 20);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnRateRadians()
        ///   Execute();
        /// </pre>
        /// 
        /// @param turnRate angle of the clockwise rotation, in radians.
        ///
        /// @see #GetTurnRateRadians()()
        /// @see #setVelocityRate(double)
        /// @see #setGunRotationRateRadians(double)
        /// @see #setRadarRotationRateRadians(double)
        /// @see AdvancedRobot#SetTurnRightRadians(double)
        /// @see AdvancedRobot#SetTurnLeftRadians(double)
        /// </summary>
        public double TurnRateRadians
        {
            get { return turnRate; }
            set{turnRate = value;}
        }

        /// <summary>
        /// Sets the gun's clockwise (right) rotation per turn, in degrees.
        /// <p/>
        /// This call returns immediately, and will not Execute until you call
        /// Execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the gun turns counterclockwise (left)
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the gun to turn right 15 degrees per turn
        ///   setGunRotationRate(15);
        /// <p/>
        ///   // Set the gun to turn left 9 degrees per turn
        ///   // (overrides the previous order)
        ///   setGunRotationRate(-9);
        /// <p/>
        ///   ...
        ///   // Executes the last setGunRotationRate()
        ///   Execute();
        /// </pre>
        /// 
        /// @param gunRotationRate angle of the clockwise rotation, in degrees.
        ///
        /// @see #getGunRotationRate()
        /// @see #setVelocityRate(double)
        /// @see #setTurnRate(double)
        /// @see #setRadarRotationRate(double)
        /// @see AdvancedRobot#SetTurnGunRight(double)
        /// @see AdvancedRobot#SetTurnGunLeft(double)
        /// </summary>
        public double GunRotationRate
        {
            get { return Utils.ToDegrees(gunRotationRate); }
            set { gunRotationRate = Utils.ToRadians(value); }        }

        /// <summary>
        /// Sets the gun's clockwise (right) rotation per turn, in radians.
        /// <p/>
        /// This call returns immediately, and will not Execute until you call
        /// Execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the gun turns counterclockwise (left)
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the gun to turn right pi / 16 radians per turn
        ///   setGunRotationRateRadians(Math.PI / 16);
        /// <p/>
        ///   // Set the gun to turn left pi / 12 radians per turn
        ///   // (overrides the previous order)
        ///   setGunRotationRateRadians(-Math.PI / 12);
        /// <p/>
        ///   ...
        ///   // Executes the last setGunRotationRateRadians()
        ///   Execute();
        /// </pre>
        /// 
        /// @param gunRotationRate angle of the clockwise rotation, in radians.
        ///
        /// @see #getGunRotationRateRadians()
        /// @see #setVelocityRate(double)
        /// @see #setTurnRateRadians(double)
        /// @see #setRadarRotationRateRadians(double)
        /// @see AdvancedRobot#SetTurnGunRightRadians(double)
        /// @see AdvancedRobot#SetTurnGunLeftRadians(double)
        /// </summary>
        public double GunRotationRateRadians
        {
            get { return gunRotationRate; }
            set { gunRotationRate = value; }        }

        /// <summary>
        /// Sets the radar's clockwise (right) rotation per turn, in degrees.
        /// <p/>
        /// This call returns immediately, and will not Execute until you call
        /// Execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the radar turns counterclockwise (left)
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the radar to turn right 45 degrees per turn
        ///   setRadarRotationRate(45);
        /// <p/>
        ///   // Set the radar to turn left 15 degrees per turn
        ///   // (overrides the previous order)
        ///   setRadarRotationRate(-15);
        /// <p/>
        ///   ...
        ///   // Executes the last setRadarRotationRate()
        ///   Execute();
        /// </pre>
        /// 
        /// @param radarRotationRate angle of the clockwise rotation, in degrees.
        ///
        /// @see #getRadarRotationRate()
        /// @see #setVelocityRate(double)
        /// @see #setTurnRate(double)
        /// @see #setGunRotationRate(double)
        /// @see AdvancedRobot#SetTurnRadarRight(double)
        /// @see AdvancedRobot#SetTurnRadarLeft(double)
        /// </summary>        public double RadarRotationRate
        {
            get { return Utils.ToDegrees(radarRotationRate); }
            set { radarRotationRate = Utils.ToRadians(value); }        }

        /// <summary>
        /// Sets the radar's clockwise (right) rotation per turn, in radians.
        /// <p/>
        /// This call returns immediately, and will not Execute until you call
        /// Execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the radar turns counterclockwise (left)
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the radar to turn right pi / 4 radians per turn
        ///   setRadarRotationRateRadians(Math.PI / 4);
        /// <p/>
        ///   // Set the radar to turn left pi / 8 radians per turn
        ///   // (overrides the previous order)
        ///   setRadarRotationRateRadians(-Math.PI / 8);
        /// <p/>
        ///   ...
        ///   // Executes the last setRadarRotationRateRadians()
        ///   Execute();
        /// </pre>
        /// 
        /// @param gunRotationRate angle of the clockwise rotation, in radians.
        ///
        /// @see #getRadarRotationRateRadians()
        /// @see #setVelocityRate(double)
        /// @see #setTurnRateRadians(double)
        /// @see #setGunRotationRateRadians(double)
        /// @see AdvancedRobot#SetTurnRadarRightRadians(double)
        /// @see AdvancedRobot#SetTurnRadarLeftRadians(double)
        /// </summary>
        public double RadarRotationRateRadians
        {
            get { return radarRotationRate; }            set { radarRotationRate = value; }        }

        /// <summary>
        /// Executes any pending actions, or continues executing actions that are
        /// in process. This call returns after the actions have been started.
        /// <p/>
        /// Note that advanced robots <em>must</em> call this function in order to
        /// Execute pending set* calls like e.g. {@code setVelocityRate()}, {@code SetFire()},
        /// {@code setTurnRate()} etc. Otherwise, these calls will never get executed.
        /// <p/>
        /// Any previous calls to "movement" functions outside of {@code RateControlRobot},
        /// such as {@code SetAhead()}, {@code SetTurnLeft()}, {@code SetTurnRadarLeftRadians()}
        /// etc. will be overridden when this method is called on this robot class.
        /// <p/>
        /// In this example the robot will move while turning:
        /// <pre>
        ///   setVelocityRate(6);
        ///   setTurnRate(7);
        ///
        ///   while (true) {
        ///       Execute();
        ///   }
        /// </pre>
        /// </summary>
        public override void Execute()
        {
            MaxVelocity = velocityRate;
            if (velocityRate > 0)
            {
                SetAhead(Double.PositiveInfinity);
            }
            else if (velocityRate < 0)
            {
                SetBack(Double.PositiveInfinity);
            }
            else
            {
                SetAhead(0);
            }

            SetTurnGunRightRadians(gunRotationRate);
            SetTurnRadarRightRadians(radarRotationRate);
            SetTurnRightRadians(turnRate);

            base.Execute();
        }
    }
}
//happy