/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Javadoc
 *******************************************************************************/
package robocode;


/**
 * A HitByBulletEvent is sent to {@link Robot#onHitByBullet onHitByBullet} when
 * your robot has been hit by a bullet. You can use the information contained in
 * this event to determine what to do.
 *
 * @author Mathew A. Nelson (original)
 */
public class HitByBulletEvent extends Event {
	private double bearing;
	private Bullet bullet;

	/**
	 * Called by the game to create a new HitByBulletEvent.
	 * 
	 * @param bearing the bearing of the bullet that hit your robot, in radians
	 * @param bullet the bullet that has hit your robot
	 */
	public HitByBulletEvent(double bearing, Bullet bullet) {
		super();
		this.bearing = bearing;
		this.bullet = bullet;
	}

	/**
	 * Returns the bearing to the bullet, relative to your robot's heading,
	 * in degrees (-180 < getBearing() <= 180)
	 * <p>
	 * If you were to turnRight(e.getBearing()), you would be facing the
	 * direction the bullet came from. The calculation used here is:
	 * (bullet's heading in degrees + 180) - (your heading in degrees)
	 *
	 * @return the bearing to the bullet, in degrees
	 */
	public double getBearing() {
		return bearing * 180.0 / Math.PI;
	}

	/**
	 * Returns the bearing to the bullet, relative to your robot's heading,
	 * in radians (-Math.PI < getBearingRadians() <= Math.PI)
	 * <p>
	 * If you were to turnRightRadians(e.getBearingRadians()), you would be
	 * facing the direction the bullet came from. The calculation used here is:
	 * (bullet's heading in radians + Math.PI) - (your heading in radians)
	 *
	 * @return the bearing to the bullet, in radians
	 */
	public double getBearingRadians() {
		return bearing;
	}

	/**
	 * Returns the bullet that hit your robot.
	 *
	 * @return the bullet that hit your robot
	 */
	public Bullet getBullet() {
		return bullet;
	}

	/**
	 * Returns the heading of the bullet when it hit you, in degrees
	 * (0 <= getHeading() < 360)
	 * <p>
	 * Note: This is not relative to the direction you are facing. The robot
	 * that fired the bullet was in the opposite direction of getHeading() when
	 * it fired the bullet.
	 *
	 * @return the heading of the bullet, in degrees
	 */
	public double getHeading() {
		return bullet.getHeading();
	}

	/**
	 * @deprecated Use {@link #getHeading()} instead.
	 */
	@Deprecated
	public double getHeadingDegrees() {
		return getHeading();
	}

	/**
	 * Returns the heading of the bullet when it hit you, in radians
	 * (0 <= getHeadingRadians() < 2 * PI)
	 * <p>
	 * Note: This is not relative to the direction you are facing. The robot
	 * that fired the bullet was in the opposite direction of
	 * getHeadingRadians() when it fired the bullet.
	 *
	 * @return the heading of the bullet, in radians
	 */
	public double getHeadingRadians() {
		return bullet.getHeadingRadians();
	}

	/**
	 * Returns the name of the robot that fired the bullet.
	 *
	 * @return the name of the robot that fired the bullet
	 */
	public String getName() {
		return bullet.getName();
	}

	/**
	 * Returns the power of this bullet. The damage you take (in fact, already
	 * took) is 4 * power, plus 2 * (power-1) if power > 1. The robot that fired
	 * the bullet receives 3 * power back.
	 *
	 * @return the power of the bullet
	 */
	public double getPower() {
		return bullet.getPower();
	}

	/**
	 * Returns the velocity of this bullet.
	 *
	 * @return the velocity of the bullet
	 */
	public double getVelocity() {
		return bullet.getVelocity();
	}
}