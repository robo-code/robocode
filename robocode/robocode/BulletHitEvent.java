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
 * This event is sent to {@link Robot#onBulletHit(BulletHitEvent) onBulletHit}
 * when one of your bullets has hit another robot.
 *
 * @author Mathew A. Nelson (original)
 */
public class BulletHitEvent extends Event {
	private String name;
	private double energy;
	private Bullet bullet;

	/**
	 * Called by the game to create a new BulletHitEvent.
	 */
	public BulletHitEvent(String name, double energy, Bullet bullet) {
		super();
		this.name = name;
		this.energy = energy;
		this.bullet = bullet;
	}

	/**
	 * Returns the bullet of yours that hit the robot.
	 *
	 * @return the bullet that hit the robot
	 */
	public Bullet getBullet() {
		return bullet;
	}

	/**
	 * Returns the remaining energy of the robot your bullet has hit (after the
	 * damage done by your bullet).
	 *
	 * @return energy the remaining energy of the robot that your bullet has hit
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * @deprecated Use {@link #getEnergy()} instead.
	 */
	@Deprecated
	public double getLife() {
		return energy;
	}

	/**
	 * Returns the name of the robot your bullet hit.
	 *
	 * @return the name of the robot your bullet hit.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @deprecated Use {@link #getEnergy()} instead.
	 */
	@Deprecated
	public double getRobotLife() {
		return energy;
	}

	/**
	 * @deprecated Use {@link #getName()} instead.
	 */
	@Deprecated
	public String getRobotName() {
		return name;
	}
}
