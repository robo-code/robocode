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


/**
 * This event is sent to {@link robocode.Robot#onBulletHit onBulletHit}
 * when one of your bullets hits another robot.
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
	 * Returns the Bullet that hit.
	 * 
	 * @return Bullet
	 */
	public Bullet getBullet() {
		return bullet;
	}

	/**
	 * Returns the remaining energy of the robot you hit (after the damage done by your bullet).
	 * @return energy of the robot you hit
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * @deprecated use getEnergy()
	 */
	public double getLife() {
		return energy;
	}

	/**
	 * Returns the name of the robot you hit.
	 * 
	 * @return the name of the robot you hit.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @deprecated use getEnergy()
	 */
	public double getRobotLife() {
		return energy;
	}

	/**
	 * @deprecated use getName()
	 */
	public String getRobotName() {
		return name;
	}
}
