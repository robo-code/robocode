/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode;

/**
 * A HitByBulletEvent is sent to {@link robocode.Robot#onHitByBullet} when you are hit by a bullet.
 * You can use the information contained in this event to determine what to do.
 */
public class HitByBulletEvent extends Event {
	private double bearing = 0.0;
	private Bullet bullet = null;
/**
 * Called by the game to create a new HitByBulletEvent.
 */
public HitByBulletEvent(double bearing, Bullet bullet) {
	super();
	this.bearing = bearing;
	this.bullet = bullet;
}
/**
 * Returns the bearing to the bullet.
 * If you were to turnRight(e.getBearing()),
 * you would be facing the direction the bullet came from.
 * The calculation used here is:  (bullet's heading + 180) - (your heading)
 * -180 < bearing <= 180
 * @return bearing to the bullet.
 */
public double getBearing() {
	return bearing * 180.0 / Math.PI;
}
/**
 * Returns the bearing to the bullet.
 * If you were to turnRight(e.getBearing()),
 * you would be facing the direction the bullet came from.
 * The calculation used here is:  (bullet's heading + Math.PI) - (your heading)
 * -Math.PI < bearing <= Math.PI
 * @return bearing to the bullet.
 */
public double getBearingRadians() {
	return bearing;
}
/**
 * Returns the Bullet that hit you.
 * @return Bullet
 */
public Bullet getBullet() {
	return bullet;
}
/**
 * Returns the direction the bullet was heading when it hit you, in degrees (0 <= getHeading() < 360)
 * This is not relative to the direction you are facing.
 * The robot that fired this bullet was in the opposite direction of getHeading() when it fired this bullet.
 * @return direction the bullet was heading.
 */
public double getHeading() {
	return bullet.getHeading();
}
/**
 * @deprecated use getHeading
 */
public double getHeadingDegrees() {
	return getHeading();
}
/**
 * Returns the direction the bullet was heading when it hit you, in radians (0 <= getHeading() < 2 * PI)
 * This is not relative to the direction you are facing.
 * The robot that fired this bullet was in the opposite direction of getHeading() when it fired this bullet.
 * @return direction the bullet was heading.

 */
public double getHeadingRadians() {
	return bullet.getHeadingRadians();
}
/**
 * Returns the name of the robot that fired this bullet
 * @return the name of the robot that fired this bullet
 */
public java.lang.String getName() {
	return bullet.getName();
}
/**
 * Returns the power of this bullet.
 * The damage you take (in fact, already took) is 4 * power, plus 2 * (power-1) if power > 1.
 * The robot that fired the bullet receives 3 * power back.
 * @return power of the bullet.
 */
public double getPower() {
	return bullet.getPower();
}
/**
 * Returns the velocity of this bullet.
 * Currently, this is a constant.
 * @return velocity of the bullet that hit you
 */
public double getVelocity() {
	return bullet.getVelocity();
}
}
