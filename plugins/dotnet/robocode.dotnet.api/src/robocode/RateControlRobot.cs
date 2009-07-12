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
    /// specified rate for ever, until the rate is changed. In order to move ahead or right, the
    /// rate must be set to a positive value. If a negative value is used instead, the movement
    /// will go back or to the left. In order to stop the movement, the rate must be
    /// set to 0.
    /// </p>
    /// Note: When calling {@code setVelocityRate()}, {@code setTurnRate()}, {@code setGunRotationRate()},
    /// {@code setRadarRotationRate()} and variants, Any previous calls to "movement" functions outside of
    /// {@code RateControlRobot}, such as {@code setAhead()}, {@code setTurnLeft()},
    /// {@code setTurnRadarRightRadians()} and similar will be overridden when calling the
    /// {@link #execute() execute()} on this robot class.
    /// </p>
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
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
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
        ///   execute();
        /// </pre>
        ///
        /// Note: This method overrules {@link robocode.AdvancedRobot#setAhead(double)} and
        /// {@link robocode.AdvancedRobot#setBack(double)}.
        ///
        /// @param velocityRate pixels per turn the robot will move.
        /// 
        /// @see #getVelocityRate()
        /// @see #setTurnRate(double)
        /// @see #setGunRotationRate(double)
        /// @see #setRadarRotationRate(double)
        /// @see AdvancedRobot#setAhead(double)
        /// @see AdvancedRobot#setBack(double)
        /// </summary>
        public void setVelocityRate(double velocityRate)
        {
            this.velocityRate = velocityRate;
        }

        /// <summary>
        /// Returns the speed the robot will move, in pixels per turn.
        /// Positive values means that the robot will move forward.
        /// Negative values means that the robot will move backwards.
        /// If the value is 0, the robot will stand still.
        ///
        /// @return The speed of the robot in pixels per turn
        ///
        /// @see #setVelocityRate(double)
        /// </summary>
        public double getVelocityRate()
        {
            return velocityRate;
        }

        /// <summary>
        /// Sets the robot's clockwise (right) rotation per turn, in degrees.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
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
        ///   execute();
        /// </pre>
        ///
        /// @param turnRate angle of the clockwise rotation, in degrees.
        ///
        /// @see #getTurnRate()
        /// @see #setVelocityRate(double)
        /// @see #setGunRotationRate(double)
        /// @see #setRadarRotationRate(double)
        /// @see AdvancedRobot#setTurnRight(double)
        /// @see AdvancedRobot#setTurnLeft(double)
        /// </summary>
        public void setTurnRate(double turnRate)
        {
            this.turnRate = Utils.toRadians(turnRate);
        }

        /// <summary>
        /// Gets the robot's clockwise rotation per turn, in degrees.
        /// Positive values means that the robot will turn to the right.
        /// Negative values means that the robot will turn to the left.
        /// If the value is 0, the robot will not turn.
        ///
        /// @return Angle of the clockwise rotation, in degrees.
        ///
        /// @see #setTurnRate(double)
        /// </summary>
        public double getTurnRate()
        {
            return Utils.toRadians(turnRate);
        }

        /// <summary>
        /// Sets the robot's clockwise (right) rotation per turn, in radians.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
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
        ///   execute();
        /// </pre>
        /// 
        /// @param turnRate angle of the clockwise rotation, in radians.
        ///
        /// @see #getTurnRateRadians()()
        /// @see #setVelocityRate(double)
        /// @see #setGunRotationRateRadians(double)
        /// @see #setRadarRotationRateRadians(double)
        /// @see AdvancedRobot#setTurnRightRadians(double)
        /// @see AdvancedRobot#setTurnLeftRadians(double)
        /// </summary>
        public void setTurnRateRadians(double turnRate)
        {
            this.turnRate = turnRate;
        }

        /// <summary>
        /// Gets the robot's clockwise rotation per turn, in radians.
        /// Positive values means that the robot will turn to the right.
        /// Negative values means that the robot will turn to the left.
        /// If the value is 0, the robot will not turn.
        /// 
        /// @return Angle of the clockwise rotation, in radians.
        /// 
        /// @see #getTurnRateRadians()
        /// </summary>
        public double getTurnRateRadians()
        {
            return turnRate;
        }

        /// <summary>
        /// Sets the gun's clockwise (right) rotation per turn, in degrees.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
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
        ///   execute();
        /// </pre>
        /// 
        /// @param gunRotationRate angle of the clockwise rotation, in degrees.
        ///
        /// @see #getGunRotationRate()
        /// @see #setVelocityRate(double)
        /// @see #setTurnRate(double)
        /// @see #setRadarRotationRate(double)
        /// @see AdvancedRobot#setTurnGunRight(double)
        /// @see AdvancedRobot#setTurnGunLeft(double)
        /// </summary>
        public void setGunRotationRate(double gunRotationRate)
        {
            this.gunRotationRate = Utils.toRadians(gunRotationRate);
        }

        /// <summary>
        /// Gets the gun's clockwise rotation per turn, in degrees.
        /// Positive values means that the gun will turn to the right.
        /// Negative values means that the gun will turn to the left.
        /// If the value is 0, the gun will not turn.
        /// 
        /// @return Angle of the clockwise rotation, in degrees.
        /// 
        /// @see #setGunRotationRate(double)
        /// </summary>
        public double getGunRotationRate()
        {
            return Utils.toRadians(gunRotationRate);
        }

        /// <summary>
        /// Sets the gun's clockwise (right) rotation per turn, in radians.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
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
        ///   execute();
        /// </pre>
        /// 
        /// @param gunRotationRate angle of the clockwise rotation, in radians.
        ///
        /// @see #getGunRotationRateRadians()
        /// @see #setVelocityRate(double)
        /// @see #setTurnRateRadians(double)
        /// @see #setRadarRotationRateRadians(double)
        /// @see AdvancedRobot#setTurnGunRightRadians(double)
        /// @see AdvancedRobot#setTurnGunLeftRadians(double)
        /// </summary>
        public void setGunRotationRateRadians(double gunRotationRate)
        {
            this.gunRotationRate = gunRotationRate;
        }

        /// <summary>
        /// Gets the gun's clockwise rotation per turn, in radians.
        /// Positive values means that the gun will turn to the right.
        /// Negative values means that the gun will turn to the left.
        /// If the value is 0, the gun will not turn.
        /// 
        /// @return Angle of the clockwise rotation, in radians.
        /// 
        /// @see #setGunRotationRateRadians(double)
        /// </summary>
        public double getGunRotationRateRadians()
        {
            return gunRotationRate;
        }

        /// <summary>
        /// Sets the radar's clockwise (right) rotation per turn, in degrees.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
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
        ///   execute();
        /// </pre>
        /// 
        /// @param radarRotationRate angle of the clockwise rotation, in degrees.
        ///
        /// @see #getRadarRotationRate()
        /// @see #setVelocityRate(double)
        /// @see #setTurnRate(double)
        /// @see #setGunRotationRate(double)
        /// @see AdvancedRobot#setTurnRadarRight(double)
        /// @see AdvancedRobot#setTurnRadarLeft(double)
        /// </summary>
        public void setRadarRotationRate(double radarRotationRate)
        {
            this.radarRotationRate = Utils.toRadians(radarRotationRate);
        }

        /// <summary>
        /// Gets the radar's clockwise rotation per turn, in degrees.
        /// Positive values means that the radar will turn to the right.
        /// Negative values means that the radar will turn to the left.
        /// If the value is 0, the radar will not turn.
        /// 
        /// @return Angle of the clockwise rotation, in degrees.
        /// 
        /// @see #setRadarRotationRate(double)
        /// </summary>
        public double getRadarRotationRate()
        {
            return Utils.toRadians(radarRotationRate);
        }

        /// <summary>
        /// Sets the radar's clockwise (right) rotation per turn, in radians.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
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
        ///   execute();
        /// </pre>
        /// 
        /// @param gunRotationRate angle of the clockwise rotation, in radians.
        ///
        /// @see #getRadarRotationRateRadians()
        /// @see #setVelocityRate(double)
        /// @see #setTurnRateRadians(double)
        /// @see #setGunRotationRateRadians(double)
        /// @see AdvancedRobot#setTurnRadarRightRadians(double)
        /// @see AdvancedRobot#setTurnRadarLeftRadians(double)
        /// </summary>
        public void setRadarRotationRateRadians(double radarRotationRate)
        {
            this.radarRotationRate = radarRotationRate;
        }

        /// <summary>
        /// Gets the radar's clockwise rotation per turn, in radians.
        /// Positive values means that the radar will turn to the right.
        /// Negative values means that the radar will turn to the left.
        /// If the value is 0, the radar will not turn.
        /// 
        /// @return Angle of the clockwise rotation, in radians.
        /// 
        /// @see #setRadarRotationRateRadians(double)
        /// </summary>
        public double getRadarRotationRateRadians()
        {
            return radarRotationRate;
        }

        /// <summary>
        /// Executes any pending actions, or continues executing actions that are
        /// in process. This call returns after the actions have been started.
        /// <p/>
        /// Note that advanced robots <em>must</em> call this function in order to
        /// execute pending set* calls like e.g. {@code setVelocityRate()}, {@code setFire()},
        /// {@code setTurnRate()} etc. Otherwise, these calls will never get executed.
        /// <p/>
        /// Any previous calls to "movement" functions outside of {@code RateControlRobot},
        /// such as {@code setAhead()}, {@code setTurnLeft()}, {@code setTurnRadarLeftRadians()}
        /// etc. will be overridden when this method is called on this robot class.
        /// <p/>
        /// In this example the robot will move while turning:
        /// <pre>
        ///   setVelocityRate(6);
        ///   setTurnRate(7);
        ///
        ///   while (true) {
        ///       execute();
        ///   }
        /// </pre>
        /// </summary>
        public override void execute()
        {
            setMaxVelocity(velocityRate);
            if (velocityRate > 0)
            {
                setAhead(Double.PositiveInfinity);
            }
            else if (velocityRate < 0)
            {
                setBack(Double.PositiveInfinity);
            }
            else
            {
                setAhead(0);
            }

            setTurnGunRightRadians(gunRotationRate);
            setTurnRadarRightRadians(radarRotationRate);
            setTurnRightRadians(turnRate);

            base.execute();
        }
    }
}
//happy