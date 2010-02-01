#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;
using Robocode.Util;

namespace Robocode
{
    /// <summary>
    ///   This advanced robot type allows you to set a rate for each of the robot's movements.
    ///   <p />
    ///   You can set the rate for:
    ///   <ul>
    ///     <li>
    ///       velocity - pixels per turn
    ///     </li>
    ///     <li>
    ///       robot turn - radians per turn
    ///     </li>
    ///     <li>
    ///       gun rotation - radians per turn
    ///     </li>
    ///     <li>
    ///       radar rotation - radians per turn
    ///     </li>
    ///   </ul>
    ///   When you set a rate for one of the above movements, the movement will continue the move by
    ///   specified rate for ever, until the rate is changed. In order to move Ahead or right, the
    ///   rate must be set to a positive value. If a negative value is used instead, the movement
    ///   will go Back or to the left. In order to Stop the movement, the rate must be
    ///   set to 0.
    /// 
    ///   Note: When calling
    ///   <see cref="VelocityRate" />
    ///   ,
    ///   <see cref="TurnRate" />
    ///   ,
    ///   <see cref="GunRotationRate" />
    ///   ,
    ///   <see cref="RadarRotationRate" />
    ///   and variants, Any previous calls to "movement" functions outside of
    ///   <see cref="RateControlRobot" />
    ///   , such as
    ///   <see cref="AdvancedRobot.SetAhead(double)" />
    ///   ,
    ///   <see cref="AdvancedRobot.SetTurnLeft(double)" />
    ///   ,
    ///   <see cref="AdvancedRobot.SetTurnRadarRightRadians(double)" />
    ///   and similar will be overridden when calling the
    ///   <see cref="Execute()" />
    ///   on this robot class.
    /// 
    ///   Look into the source code for the samplecs.VelociRobot
    ///   in order to see how to use this
    ///   robot type.
    /// </summary>
    public abstract class RateControlRobot : AdvancedRobot
    {
        private double velocityRate; // Pixels per turn
        private double turnRate; // Radians per turn
        private double gunRotationRate; // Radians per turn
        private double radarRotationRate; // Radians per turn

        /// 
        ///<summary>
        ///  Sets the speed the robot will move (forward), in pixels per turn.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot will move backwards
        ///  <p />
        ///  <example>
        ///    <pre>
        ///      // Set the robot to move forward 2 pixels per turn
        ///      setVelocityRate(2);
        ///
        ///      // Set the robot to move backwards 8 pixels per turn
        ///      // (overrides the previous order)
        ///      setVelocityRate(-8);
        ///
        ///      ...
        ///      // Executes the last setVelocityRate()
        ///      Execute();
        ///    </pre>
        ///  </example>
        ///  Note: This method overrules
        ///  <see cref="AdvancedRobot.SetAhead(double)" />
        ///  and
        ///  <see cref="AdvancedRobot.SetBack(double)" />
        ///  <seealso cref="VelocityRate" />
        ///  <seealso cref="TurnRate" />
        ///  <seealso cref="GunRotationRate" />
        ///  <seealso cref="RadarRotationRate" />
        ///  <seealso cref="AdvancedRobot.SetAhead(double)" />
        ///  <seealso cref="AdvancedRobot.SetBack(double)" />
        ///</summary>
        public double VelocityRate
        {
            get { return velocityRate; }
            set { velocityRate = value; }
        }

        /// 
        ///<summary>
        ///  Sets the robot's clockwise (right) rotation per turn, in degrees.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot turns counterclockwise (left)
        ///  <p />
        ///  <example>
        ///    <pre>
        ///      // Set the robot to turn right 10 degrees per turn
        ///      setTurnRate(10);
        ///
        ///      // Set the robot to turn left 4 degrees per turn
        ///      // (overrides the previous order)
        ///      setTurnRate(-5);
        ///
        ///      ...
        ///      // Executes the last setTurnRate()
        ///      Execute();
        ///    </pre>
        ///  </example>
        ///  <seealso cref="TurnRate" />
        ///  <seealso cref="VelocityRate" />
        ///  <seealso cref="GunRotationRate" />
        ///  <seealso cref="RadarRotationRate" />
        ///  <seealso cref="AdvancedRobot.SetTurnRight(double)" />
        ///  <seealso cref="AdvancedRobot.SetTurnLeft(double)" />
        ///</summary>
        public double TurnRate
        {
            get { return Utils.ToRadians(turnRate); }
            set { turnRate = Utils.ToRadians(value); }
        }

        /// <summary>
        ///   Sets the robot's clockwise (right) rotation per turn, in radians.
        ///   <p />
        ///   This call returns immediately, and will not Execute until you call
        ///   Execute() or take an action that executes.
        ///   <p />
        ///   Note that both positive and negative values can be given as input,
        ///   where negative values means that the robot turns counterclockwise (left)
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Set the robot to turn right pi / 32 radians per turn
        ///       setTurnRateRadians(Math.PI / 32);
        ///       <p />
        ///       // Set the robot to turn left pi / 20 radians per turn
        ///       // (overrides the previous order)
        ///       setTurnRateRadians(-Math.PI / 20);
        ///       <p />
        ///       ...
        ///       // Executes the last setTurnRateRadians()
        ///       Execute();
        ///     </pre>
        ///   </example>
        /// 
        ///   <seealso cref="TurnRateRadians" />
        ///   <seealso cref="VelocityRate" />
        ///   <seealso cref="GunRotationRateRadians" />
        ///   <seealso cref="RadarRotationRateRadians" />
        ///   <seealso cref="AdvancedRobot.SetTurnRightRadians(double)" />
        ///   <seealso cref="AdvancedRobot.SetTurnLeftRadians(double)" />
        /// </summary>
        public double TurnRateRadians
        {
            get { return turnRate; }
            set { turnRate = value; }
        }

        /// <summary>
        ///   Sets the gun's clockwise (right) rotation per turn, in degrees.
        ///   <p />
        ///   This call returns immediately, and will not Execute until you call
        ///   Execute() or take an action that executes.
        ///   <p />
        ///   Note that both positive and negative values can be given as input,
        ///   where negative values means that the gun turns counterclockwise (left)
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Set the gun to turn right 15 degrees per turn
        ///       setGunRotationRate(15);
        ///       <p />
        ///       // Set the gun to turn left 9 degrees per turn
        ///       // (overrides the previous order)
        ///       setGunRotationRate(-9);
        ///       <p />
        ///       ...
        ///       // Executes the last setGunRotationRate()
        ///       Execute();
        ///     </pre>
        ///   </example>
        ///   <seealso cref="GunRotationRate" />
        ///   <seealso cref="VelocityRate" />
        ///   <seealso cref="TurnRate" />
        ///   <seealso cref="RadarRotationRate" />
        ///   <seealso cref="AdvancedRobot.SetTurnGunRight(double)" />
        ///   <seealso cref="AdvancedRobot.SetTurnGunLeft(double)" />
        /// </summary>
        public double GunRotationRate
        {
            get { return Utils.ToDegrees(gunRotationRate); }
            set { gunRotationRate = Utils.ToRadians(value); }
        }

        /// <summary>
        ///   Sets the gun's clockwise (right) rotation per turn, in radians.
        ///   <p />
        ///   This call returns immediately, and will not Execute until you call
        ///   Execute() or take an action that executes.
        ///   <p />
        ///   Note that both positive and negative values can be given as input,
        ///   where negative values means that the gun turns counterclockwise (left)
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Set the gun to turn right pi / 16 radians per turn
        ///       setGunRotationRateRadians(Math.PI / 16);
        ///       <p />
        ///       // Set the gun to turn left pi / 12 radians per turn
        ///       // (overrides the previous order)
        ///       setGunRotationRateRadians(-Math.PI / 12);
        ///       <p />
        ///       ...
        ///       // Executes the last setGunRotationRateRadians()
        ///       Execute();
        ///     </pre>
        ///   </example>
        ///   <seealso cref="GunRotationRateRadians()" />
        ///   <seealso cref="VelocityRate" />
        ///   <seealso cref="TurnRateRadians" />
        ///   <seealso cref="RadarRotationRateRadians" />
        ///   <seealso cref="AdvancedRobot.SetTurnGunRightRadians(double)" />
        ///   <seealso cref="AdvancedRobot.SetTurnGunLeftRadians(double)" />
        /// </summary>
        public double GunRotationRateRadians
        {
            get { return gunRotationRate; }
            set { gunRotationRate = value; }
        }

        /// <summary>
        ///   Sets the radar's clockwise (right) rotation per turn, in degrees.
        ///   <p />
        ///   This call returns immediately, and will not Execute until you call
        ///   Execute() or take an action that executes.
        ///   <p />
        ///   Note that both positive and negative values can be given as input,
        ///   where negative values means that the radar turns counterclockwise (left)
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Set the radar to turn right 45 degrees per turn
        ///       setRadarRotationRate(45);
        ///       <p />
        ///       // Set the radar to turn left 15 degrees per turn
        ///       // (overrides the previous order)
        ///       setRadarRotationRate(-15);
        ///       <p />
        ///       ...
        ///       // Executes the last setRadarRotationRate()
        ///       Execute();
        ///     </pre>
        ///   </example>
        ///   <seealso cref="RadarRotationRate()" />
        ///   <seealso cref="VelocityRate" />
        ///   <seealso cref="TurnRate" />
        ///   <seealso cref="GunRotationRate" />
        ///   <seealso cref="AdvancedRobot.SetTurnRadarRight(double)" />
        ///   <seealso cref="AdvancedRobot.SetTurnRadarLeft(double)" />
        /// </summary>
        public double RadarRotationRate
        {
            get { return Utils.ToDegrees(radarRotationRate); }
            set { radarRotationRate = Utils.ToRadians(value); }
        }

        /// <summary>
        ///   Sets the radar's clockwise (right) rotation per turn, in radians.
        ///   <p />
        ///   This call returns immediately, and will not Execute until you call
        ///   Execute() or take an action that executes.
        ///   <p />
        ///   Note that both positive and negative values can be given as input,
        ///   where negative values means that the radar turns counterclockwise (left)
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Set the radar to turn right pi / 4 radians per turn
        ///       setRadarRotationRateRadians(Math.PI / 4);
        ///       <p />
        ///       // Set the radar to turn left pi / 8 radians per turn
        ///       // (overrides the previous order)
        ///       setRadarRotationRateRadians(-Math.PI / 8);
        ///       <p />
        ///       ...
        ///       // Executes the last setRadarRotationRateRadians()
        ///       Execute();
        ///     </pre>
        ///   </example>
        ///   <seealso cref="RadarRotationRateRadians" />
        ///   <seealso cref="VelocityRate" />
        ///   <seealso cref="TurnRateRadians" />
        ///   <seealso cref="GunRotationRateRadians" />
        ///   <seealso cref="AdvancedRobot.SetTurnRadarRightRadians(double)" />
        ///   <seealso cref="AdvancedRobot.SetTurnRadarLeftRadians(double)" />
        /// </summary>
        public double RadarRotationRateRadians
        {
            get { return radarRotationRate; }
            set { radarRotationRate = value; }
        }

        /// 
        ///<summary>
        ///  Executes any pending actions, or continues executing actions that are
        ///  in process. This call returns after the actions have been started.
        ///  <p />
        ///  Note that advanced robots
        ///  <em>must</em>
        ///  call this function in order to
        ///  Execute pending set* calls like e.g.
        ///  <see cref="VelocityRate" />
        ///  ,
        ///  <see cref="AdvancedRobot.SetFire(double)" />
        ///  ,
        ///  <see cref="TurnRate" />
        ///  etc. Otherwise, these calls will never get executed.
        ///  <p />
        ///  Any previous calls to "movement" functions outside of
        ///  <see cref="RateControlRobot" />
        ///  ,
        ///  such as
        ///  <see cref="AdvancedRobot.SetAhead(double)" />
        ///  ,
        ///  <see cref="AdvancedRobot.SetTurnLeft(double)" />
        ///  ,
        ///  <see cref="AdvancedRobot.SetTurnRadarLeftRadians(double)" />
        ///  etc. will be overridden when this method is called on this robot class.
        ///  <p />
        ///  <example>
        ///    In this example the robot will move while turning:
        ///    <pre>
        ///      setVelocityRate(6);
        ///      setTurnRate(7);
        ///
        ///      while (true) {
        ///      Execute();
        ///      }
        ///    </pre>
        ///  </example>
        ///</summary>
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

//doc