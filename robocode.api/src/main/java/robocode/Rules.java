/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import static java.lang.Math.abs;
import static java.lang.Math.max;


/**
 * Constants and methods that defines the rules of Robocode.
 * Constants are used for rules that will not change.
 * Methods are provided for rules that can be changed between battles or which depends
 * on some other factor.
 *
 * @author Luis Crespo (original)
 * @author Flemming N. Larsen (original)
 *
 * @since 1.1.4
 */
public final class Rules {

	// Hide the constructor in the Javadocs as it should not be used
	private Rules() {}

	/**
	 * The acceleration of a robot, i.e. the increase of velocity when the
	 * robot moves forward, which is 1 pixel/turn.
	 */
	public static final double ACCELERATION = 1;

	/**
	 * The deceleration of a robot, i.e. the decrease of velocity when the
	 * robot moves backwards (or brakes), which is 2 pixels/turn.
	 */
	public static final double DECELERATION = 2;

	/**
	 * The maximum velocity of a robot, which is 8 pixels/turn.
	 */
	public static final double MAX_VELOCITY = 8;

	/**
	 * The radar scan radius, which is 1200 pixels.
	 * Robots which is more than 1200 pixels away cannot be seen on the radar.
	 */
	public static final double RADAR_SCAN_RADIUS = 1200;

	/**
	 * The minimum bullet power, i.e the amount of energy required for firing a
	 * bullet, which is 0.1 energy points.
	 */
	public static final double MIN_BULLET_POWER = 0.1;

	/**
	 * The maximum bullet power, i.e. the maximum amount of energy that can be
	 * transferred to a bullet when it is fired, which is 3 energy points.
	 */
	public static final double MAX_BULLET_POWER = 3;

	/**
	 * The maximum turning rate of the robot, in degrees, which is
	 * 10 degress/turn.
	 * Note, that the turn rate of the robot depends on it's velocity.
	 *
	 * @see #MAX_TURN_RATE_RADIANS
	 * @see #getTurnRate(double)
	 * @see #getTurnRateRadians(double)
	 */
	public static final double MAX_TURN_RATE = 10;

	/**
	 * The maximum turning rate of the robot measured in radians instead of
	 * degrees.
	 *
	 * @see #MAX_TURN_RATE
	 * @see #getTurnRate(double)
	 * @see #getTurnRateRadians(double)
	 */
	public static final double MAX_TURN_RATE_RADIANS = Math.toRadians(MAX_TURN_RATE);

	/**
	 * The turning rate of the gun measured in degrees, which is
	 * 20 degrees/turn.
	 * Note, that if setAdjustGunForRobotTurn(true) has been called, the gun
	 * turn is independent of the robot turn.
	 * In this case the gun moves relatively to the screen. If
	 * setAdjustGunForRobotTurn(false) has been called or
	 * setAdjustGunForRobotTurn() has not been called at all (this is the
	 * default), then the gun turn is dependent on the robot turn, and in this
	 * case the gun moves relatively to the robot body.
	 *
	 * @see #GUN_TURN_RATE_RADIANS
	 * @see Robot#setAdjustGunForRobotTurn(boolean)
	 */
	public static final double GUN_TURN_RATE = 20;

	/**
	 * The turning rate of the gun measured in radians instead of degrees.
	 *
	 * @see #GUN_TURN_RATE
	 */
	public static final double GUN_TURN_RATE_RADIANS = Math.toRadians(GUN_TURN_RATE);

	/**
	 * The turning rate of the radar measured in degrees, which is
	 * 45 degrees/turn.
	 * Note, that if setAdjustRadarForRobotTurn(true) and/or
	 * setAdjustRadarForGunTurn(true) has been called, the radar turn is
	 * independent of the robot and/or gun turn. If both methods has been set to
	 * true, the radar moves relatively to the screen.
	 * If setAdjustRadarForRobotTurn(false) and/or setAdjustRadarForGunTurn(false)
	 * has been called or not called at all (this is the default), then the
	 * radar turn is dependent on the robot and/or gun turn, and in this case
	 * the radar moves relatively to the gun and/or robot body.
	 *
	 * @see #RADAR_TURN_RATE_RADIANS
	 * @see Robot#setAdjustGunForRobotTurn(boolean)
	 * @see Robot#setAdjustRadarForGunTurn(boolean)
	 */
	public static final double RADAR_TURN_RATE = 45;

	/**
	 * The turning rate of the radar measured in radians instead of degrees.
	 *
	 * @see #RADAR_TURN_RATE
	 */
	public static final double RADAR_TURN_RATE_RADIANS = Math.toRadians(RADAR_TURN_RATE);

	/**
	 * The amount of damage taken when a robot hits or is hit by another robot,
	 * which is 0.6 energy points.
	 */
	public static final double ROBOT_HIT_DAMAGE = 0.6;

	/**
	 * The amount of bonus damage dealt by a robot ramming an opponent by moving forward into it,
	 * which is 2 x {@link ROBOT_HIT_DAMAGE} = 1.2 energy points.
	 */
	public static final double ROBOT_HIT_BONUS = 2 * ROBOT_HIT_DAMAGE;

	/**
	 * Returns the turn rate of a robot given a specific velocity measured in
	 * degrees/turn.
	 *
	 * @param velocity the velocity of the robot.
	 * @return turn rate in degrees/turn.
	 * @see #getTurnRateRadians(double)
	 */
	public static double getTurnRate(double velocity) {
		return MAX_TURN_RATE - 0.75 * abs(velocity);
	}

	/**
	 * Returns the turn rate of a robot given a specific velocity measured in
	 * radians/turn.
	 *
	 * @param velocity the velocity of the robot.
	 * @return turn rate in radians/turn.
	 * @see #getTurnRate(double)
	 */
	public static double getTurnRateRadians(double velocity) {
		return Math.toRadians(getTurnRate(velocity));
	}

	/**
	 * Returns the amount of damage taken when robot hits a wall with a
	 * specific velocity.
	 *
	 * @param velocity the velocity of the robot.
	 * @return wall hit damage in energy points.
	 */
	public static double getWallHitDamage(double velocity) {
		return max(abs(velocity) / 2 - 1, 0);
	}

	/**
	 * Returns the amount of damage of a bullet given a specific bullet power.
	 *
	 * @param bulletPower the energy power of the bullet.
	 * @return bullet damage in energy points.
	 */
	public static double getBulletDamage(double bulletPower) {
		double damage = 4 * bulletPower;

		if (bulletPower > 1) {
			damage += 2 * (bulletPower - 1);
		}
		return damage;
	}

	/**
	 * Returns the amount of bonus given when a robot's bullet hits an opponent
	 * robot given a specific bullet power.
	 *
	 * @param bulletPower the energy power of the bullet.
	 * @return bullet hit bonus in energy points.
	 */
	public static double getBulletHitBonus(double bulletPower) {
		return 3 * bulletPower;
	}

	/**
	 * Returns the speed of a bullet given a specific bullet power measured in pixels/turn.
	 *
	 * @param bulletPower the energy power of the bullet.
	 * @return bullet speed in pixels/turn
	 */
	public static double getBulletSpeed(double bulletPower) {
		bulletPower = Math.min(Math.max(bulletPower, MIN_BULLET_POWER), MAX_BULLET_POWER);
		return 20 - 3 * bulletPower;
	}

	/**
	 * Returns the heat produced by firing the gun given a specific bullet
	 * power.
	 *
	 * @param bulletPower the energy power of the bullet.
	 * @return gun heat
	 */
	public static double getGunHeat(double bulletPower) {
		return 1 + (bulletPower / 5);
	}
}
