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
 *******************************************************************************/
package robocode;


import robocode.peer.BulletPeer;


/**
 * Represents a bullet.  This is returned from fireBullet(), and all the bullet-related events.
 *
 * @see robocode.Robot#fireBullet
 * @see robocode.BulletHitBulletEvent
 * @see robocode.BulletHitEvent
 * @see robocode.BulletMissedEvent
 *
 * @author Mathew A. Nelson
 */
public class Bullet {
	private robocode.peer.BulletPeer peer = null;

	/**
	 * Called by the game to create a Bullet object
	 */
	public Bullet(BulletPeer peer) {
		this.peer = peer;
	}

	/**
	 * Returns the direction the bullet is/was heading, in degrees (0 <= getHeading() < 360)
	 * This is not relative to the direction you are facing.
	 * @return direction the bullet is/was heading.
	 */
	public double getHeading() {
		return Math.toDegrees(peer.getHeading());
	}

	/**
	 * Returns the direction the bullet is/was heading, in radians (0 <= getHeadingRadians() < 2 * Math.PI)
	 * This is not relative to the direction you are facing.
	 * @return direction the bullet is/was heading.
	 */
	public double getHeadingRadians() {
		return peer.getHeading();
	}

	/**
	 * Returns the name of the robot that fired this bullet
	 * 
	 * @return the name of the robot that fired this bullet
	 */
	public String getName() {
		return peer.getOwner().getName();
	}

	/**
	 * Returns the power of this bullet.
	 * 
	 * The bullet will do (4 * power) damage if it hits another robot.
	 * If power is greater than 1, it will do an additional 2 * (power - 1) damage.
	 * You will get (3 * power) back if you hit the other robot.
	 * 
	 * @return power of the bullet.
	 */
	public double getPower() {
		return peer.getPower();
	}

	/**
	 * Returns the velocity of this bullet.
	 * Currently, this is a constant.
	 *
	 * @return velocity of the bullet that hit you
	 */
	public double getVelocity() {
		return peer.getVelocity();
	}

	/**
	 * Returns the name of the robot that this bullet hit, or null.
	 * 
	 * @return the name of the robot that fired this bullet
	 */
	public String getVictim() {
		if (peer.getVictim() == null) {
			return null;
		} else { 
			return peer.getVictim().getName();
		}
	}

	/**
	 * Returns the x position of the bullet.
	 * 
	 * @return the x position of the bullet.
	 */
	public double getX() {
		return peer.getX();
	}

	/**
	 * Returns the y position of the bullet.
	 * 
	 * @return the y position of the bullet.
	 */
	public double getY() {
		return peer.getY();
	}

	/**
	 * Returns true if the bullet is still on the battlefield, false otherwise.
	 * @return true if the bullet is still on the battlefield.
	 */
	public boolean isActive() {
		return peer.isActive();
	}
}
