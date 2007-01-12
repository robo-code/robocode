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
 * This event is sent to {@link robocode.Robot#onBulletMissed onBulletMissed}
 * when one of your bullets has misses.
 *
 * @author Mathew A. Nelson (original)
 */
public class BulletMissedEvent extends Event {
	private Bullet bullet;

	/**
	 * Called by the game to create a new BulletMissedEvent.
	 */
	public BulletMissedEvent(Bullet bullet) {
		this.bullet = bullet;
	}

	/**
	 * Returns the Bullet that missed.
	 * 
	 * @return Bullet
	 */
	public Bullet getBullet() {
		return bullet;
	}
}
