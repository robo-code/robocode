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
 * This event is sent to {@link robocode.Robot#onBulletHitBullet onBulletHitBullet}
 * when one of your bullets hits another bullet.
 *
 * @author Mathew A. Nelson (original)
 */
public class BulletHitBulletEvent extends Event {
	private Bullet bullet;
	private Bullet hitBullet;

	/**
	 * Called by the game to create a new BulletHitEvent.
	 */
	public BulletHitBulletEvent(Bullet bullet, Bullet hitBullet) {
		super();
		this.bullet = bullet;
		this.hitBullet = hitBullet;
	}

	/**
	 * Returns the Bullet.
	 *
	 * @return Bullet
	 */
	public Bullet getBullet() {
		return bullet;
	}

	/**
	 * Returns the Bullet that was hit.
	 *
	 * @return Bullet
	 */
	public Bullet getHitBullet() {
		return hitBullet;
	}
}
