/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen & Luis Crespo
 *     - Initial API and implementation
 *******************************************************************************/
using System;
using robocode.util;

namespace robocode
{
    /// <summary>
    /// Constants and methods that defines the rules of Robocode.
    /// Constants are used for rules that will not change.
    /// Methods are provided for rules that can be changed between battles or which depends
    /// on some other factor.
    ///
    /// @author Luis Crespo (original)
    /// @author Flemming N. Larsen (original)
    /// @since 1.1.4
    /// </summary>
    public static class Rules
    {
        // Hide the constructor in the Javadocs as it should not be used

        /// <summary>
        /// The acceleration of a robot, i.e. the increase of velocity when the
        /// robot moves forward, which is 1 pixel/turn.
        /// </summary>
        public static readonly double ACCELERATION = 1;

        /// <summary>
        /// The deceleration of a robot, i.e. the decrease of velocity when the
        /// robot moves backwards (or brakes), which is 2 pixels/turn.
        /// </summary>
        public static readonly double DECELERATION = 2;

        /// <summary>
        /// The maximum velocity of a robot, which is 8 pixels/turn.
        /// </summary>
        public static readonly double MAX_VELOCITY = 8;

        /// <summary>
        /// The radar Scan radius, which is 1200 pixels.
        /// Robots which is more than 1200 pixels away cannot be seen on the radar.
        /// </summary>
        public static readonly double RADAR_SCAN_RADIUS = 1200;

        /// <summary>
        /// The minimum bullet power, i.e the amount of energy required for firing a
        /// bullet, which is 0.1 energy points.
        /// </summary>
        public static readonly double MIN_BULLET_POWER = 0.1;

        /// <summary>
        /// The maximum bullet power, i.e. the maximum amount of energy that can be
        /// transferred to a bullet when it is fired, which is 3 energy points.
        /// </summary>
        public static readonly double MAX_BULLET_POWER = 3;

        /// <summary>
        /// The maximum turning rate of the robot, in degrees, which is
        /// 10 degress/turn.
        /// Note, that the turn rate of the robot depends on it's velocity.
        ///
        /// @see #MAX_TURN_RATE_RADIANS
        /// @see #GetTurnRate(double)
        /// @see #GetTurnRateRadians(double)
        /// </summary>
        public static readonly double MAX_TURN_RATE = 10;

        /// <summary>
        /// The maximum turning rate of the robot measured in radians instead of
        /// degrees.
        ///
        /// @see #MAX_TURN_RATE
        /// @see #GetTurnRate(double)
        /// @see #GetTurnRateRadians(double)
        /// </summary>
        public static readonly double MAX_TURN_RATE_RADIANS = Utils.ToRadians(MAX_TURN_RATE);

        /// <summary>
        /// The turning rate of the gun measured in degrees, which is
        /// 20 degrees/turn.
        /// Note, that if setAdjustGunForRobotTurn(true) has been called, the gun
        /// turn is independent of the robot turn.
        /// In this case the gun moves relatively to the screen. If
        /// setAdjustGunForRobotTurn(false) has been called or
        /// setAdjustGunForRobotTurn() has not been called at all (this is the
        /// default), then the gun turn is dependent on the robot turn, and in this
        /// case the gun moves relatively to the robot body.
        ///
        /// @see #GUN_TURN_RATE_RADIANS
        /// @see Robot#setAdjustGunForRobotTurn(bool)
        /// </summary>
        public static readonly double GUN_TURN_RATE = 20;

        /// <summary>
        /// The turning rate of the gun measured in radians instead of degrees.
        ///
        /// @see #GUN_TURN_RATE
        /// </summary>
        public static readonly double GUN_TURN_RATE_RADIANS = Utils.ToRadians(GUN_TURN_RATE);

        /// <summary>
        /// The turning rate of the radar measured in degrees, which is
        /// 45 degrees/turn.
        /// Note, that if setAdjustRadarForRobotTurn(true) and/or
        /// setAdjustRadarForGunTurn(true) has been called, the radar turn is
        /// independent of the robot and/or gun turn. If both methods has been set to
        /// true, the radar moves relatively to the screen.
        /// If setAdjustRadarForRobotTurn(false) and/or setAdjustRadarForGunTurn(false)
        /// has been called or not called at all (this is the default), then the
        /// radar turn is dependent on the robot and/or gun turn, and in this case
        /// the radar moves relatively to the gun and/or robot body.
        ///
        /// @see #RADAR_TURN_RATE_RADIANS
        /// @see Robot#setAdjustGunForRobotTurn(bool)
        /// @see Robot#setAdjustRadarForGunTurn(bool)
        /// </summary>
        public static readonly double RADAR_TURN_RATE = 45;

        /// <summary>
        /// The turning rate of the radar measured in radians instead of degrees.
        ///
        /// @see #RADAR_TURN_RATE
        /// </summary>
        public static readonly double RADAR_TURN_RATE_RADIANS = Utils.ToRadians(RADAR_TURN_RATE);

        /// <summary>
        /// The amount of damage taken when a robot hits or is hit by another robot,
        /// which is 0.6 energy points.
        /// </summary>
        public static readonly double ROBOT_HIT_DAMAGE = 0.6;

        /// <summary>
        /// The amount of bonus given when a robot moving forward hits an opponent
        /// robot (ramming), which is 1.2 energy points.
        /// </summary>
        public static readonly double ROBOT_HIT_BONUS = 1.2;

        /// <summary>
        /// Returns the turn rate of a robot given a specific velocity measured in
        /// degrees/turn.
        ///
        /// @param velocity the velocity of the robot.
        /// @return turn rate in degrees/turn.
        /// @see #GetTurnRateRadians(double)
        /// </summary>
        public static double GetTurnRate(double velocity)
        {
            return MAX_TURN_RATE - 0.75*velocity;
        }

        /// <summary>
        /// Returns the turn rate of a robot given a specific velocity measured in
        /// radians/turn.
        ///
        /// @param velocity the velocity of the robot.
        /// @return turn rate in radians/turn.
        /// @see #GetTurnRate(double)
        /// </summary>
        public static double GetTurnRateRadians(double velocity)
        {
            return Utils.ToRadians(GetTurnRate(velocity));
        }

        /// <summary>
        /// Returns the amount of damage taken when robot hits a wall with a
        /// specific velocity.
        ///
        /// @param velocity the velocity of the robot.
        /// @return wall hit damage in energy points.
        /// </summary>
        public static double GetWallHitDamage(double velocity)
        {
            return Math.Max(Math.Abs(velocity)/2 - 1, 0);
        }

        /// <summary>
        /// Returns the amount of damage of a bullet given a specific bullet power.
        ///
        /// @param bulletPower the energy power of the bullet.
        /// @return bullet damage in energy points.
        /// </summary>
        public static double GetBulletDamage(double bulletPower)
        {
            double damage = 4*bulletPower;

            if (bulletPower > 1)
            {
                damage += 2*(bulletPower - 1);
            }
            return damage;
        }

        /// <summary>
        /// Returns the amount of bonus given when a robot's bullet hits an opponent
        /// robot given a specific bullet power.
        ///
        /// @param bulletPower the energy power of the bullet.
        /// @return bullet hit bonus in energy points.
        /// </summary>
        public static double GetBulletHitBonus(double bulletPower)
        {
            return 3*bulletPower;
        }

        /// <summary>
        /// Returns the speed of a bullet given a specific bullet power measured in
        /// pixels/turn
        ///
        /// @param bulletPower the energy power of the bullet.
        /// @return bullet speed in pixels/turn
        /// </summary>
        public static double GetBulletSpeed(double bulletPower)
        {
            return 20 - 3*bulletPower;
        }

        /// <summary>
        /// Returns the heat produced by firing the gun given a specific bullet
        /// power.
        ///
        /// @param bulletPower the energy power of the bullet.
        /// @return gun heat
        /// </summary>
        public static double GetGunHeat(double bulletPower)
        {
            return 1 + (bulletPower/5);
        }
    }
}