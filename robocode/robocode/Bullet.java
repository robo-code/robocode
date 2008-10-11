/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Javadocs
 *******************************************************************************/
package robocode;


import robocode.peer.BulletPeer;


/**
 * Represents a bullet. This is returned from {@link Robot#fireBullet(double)}
 * and {@link AdvancedRobot#setFireBullet(double)}, and all the bullet-related
 * events.
 *
 * @author Mathew A. Nelson (original)
 * @see Robot#fireBullet(double)
 * @see AdvancedRobot#setFireBullet(double)
 * @see BulletHitEvent
 * @see BulletMissedEvent
 * @see BulletHitBulletEvent
 */
public class Bullet {
	private BulletPeer peer;
    private double heading;
    private double x;
    private double y;
    private double power;
    private String name;

	/**
	 * Called by the game to create a new {@code Bullet} object
	 */
    public Bullet(double heading, double x, double y, double power, String name) {
        this.heading = heading;
        this.x = x;
        this.y = y;
        this.power = power;
        this.name = name;
    }

    public void setPeer(BulletPeer peer){
		this.peer = peer;
		heading = peer.getHeading();
		name = peer.getOwner().getName();
		power = peer.getPower();
	}

	/**
	 * Returns the direction the bullet is/was heading, in degrees
	 * (0 <= getHeading() < 360). This is not relative to the direction you are
	 * facing.
	 *
	 * @return the direction the bullet is/was heading, in degrees
	 */
	public double getHeading() {
		return Math.toDegrees(heading);
	}

	/**
	 * Returns the direction the bullet is/was heading, in radians
	 * (0 <= getHeadingRadians() < 2 * Math.PI). This is not relative to the
	 * direction you are facing.
	 *
	 * @return the direction the bullet is/was heading, in radians
	 */
	public double getHeadingRadians() {
		return heading;
	}

	/**
	 * Returns the name of the robot that fired this bullet.
	 *
	 * @return the name of the robot that fired this bullet
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the power of this bullet.
	 * <p/>
	 * The bullet will do (4 * power) damage if it hits another robot.
	 * If power is greater than 1, it will do an additional 2 * (power - 1)
	 * damage. You will get (3 * power) back if you hit the other robot.
	 *
	 * @return the power of the bullet
	 */
	public double getPower() {
		return power;
	}

	/**
	 * Returns the velocity of this bullet. The velocity of the bullet is
	 * constant once it has been fired.
	 *
	 * @return the velocity of the bullet
	 */
	public double getVelocity() {
        return Rules.getBulletSpeed(getPower());
	}

	/**
	 * Returns the name of the robot that this bullet hit, or {@code null} if
	 * the bullet has not hit a robot.
	 *
	 * @return the name of the robot that this bullet hit, or {@code null} if
	 *         the bullet has not hit a robot.
	 */
	public String getVictim() {
		return peer == null ? null : (peer.getVictim() == null) ? null : peer.getVictim().getName();
	}

	/**
	 * Returns the X position of the bullet.
	 *
	 * @return the X position of the bullet
	 */
	public double getX() {
		return peer == null ? x : peer.getX();
	}

	/**
	 * Returns the Y position of the bullet.
	 *
	 * @return the Y position of the bullet
	 */
	public double getY() {
		return peer == null ? y : peer.getY();
	}

	/**
	 * Checks if this bullet is still active on the battlefield.
	 *
	 * @return {@code true} if the bullet is still active on the battlefield;
	 *         {@code false} otherwise
	 */
	public boolean isActive() {
		return peer == null || peer.isActive();
	}
}
