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


import robocode.peer.BulletPeer;


/**
 * Represents a bullet. This is returned from fireBullet() and setFireBullet(),
 * and all the bullet-related events.
 *
 * @see Robot#fireBullet
 * @see AdvancedRobot#setFireBullet
 * @see BulletHitEvent
 * @see BulletMissedEvent
 * @see BulletHitBulletEvent
 *
 * @author Mathew A. Nelson (original)
 */
public class Bullet {
	private BulletPeer peer;

	/**
	 * Called by the game to create a Bullet object
	 */
	public Bullet(BulletPeer peer) {
		this.peer = peer;
	}

	/**
	 * Returns the direction the bullet is/was heading, in degrees
	 * (0 <= getHeading() < 360). This is not relative to the direction you are
	 * facing.
	 *
	 * @return the direction the bullet is/was heading, in degrees
	 */
	public double getHeading() {
		return Math.toDegrees(peer.getHeading());
	}

	/**
	 * Returns the direction the bullet is/was heading, in radians
	 * (0 <= getHeadingRadians() < 2 * Math.PI). This is not relative to the
	 * direction you are facing.
	 *
	 * @return the direction the bullet is/was heading, in radians
	 */
	public double getHeadingRadians() {
		return peer.getHeading();
	}

	/**
	 * Returns the name of the robot that fired this bullet.
	 *
	 * @return the name of the robot that fired this bullet
	 */
	public String getName() {
		return peer.getOwner().getName();
	}

	/**
	 * Returns the power of this bullet.
	 * <p>
	 * The bullet will do (4 * power) damage if it hits another robot.
	 * If power is greater than 1, it will do an additional 2 * (power - 1)
	 * damage. You will get (3 * power) back if you hit the other robot.
	 *
	 * @return the power of the bullet
	 */
	public double getPower() {
		return peer.getPower();
	}

	/**
	 * Returns the velocity of this bullet. The velocity of the bullet is
	 * constant once it has been fired.
	 *
	 * @return the velocity of the bullet
	 */
	public double getVelocity() {
		return peer.getVelocity();
	}

	/**
	 * Returns the name of the robot that this bullet hit, or {@code null} if
	 * the bullet has not hit a robot.
	 *
	 * @return the name of the robot that this bullet hit, or {@code null} if
	 *    the bullet has not hit a robot.
	 */
	public String getVictim() {
		return (peer.getVictim() != null) ? peer.getVictim().getName() : null;
	}

	/**
	 * Returns the X position of the bullet.
	 *
	 * @return the X position of the bullet
	 */
	public double getX() {
		return peer.getX();
	}

	/**
	 * Returns the Y position of the bullet.
	 *
	 * @return the Y position of the bullet
	 */
	public double getY() {
		return peer.getY();
	}

	/**
	 * Checks if this bullet is still active on the battlefield.
	 *
	 * @return {@code true} if the bullet is still active on the battlefield;
	 *    {@code false} otherwise
	 */
	public boolean isActive() {
		return peer.isActive();
	}
}