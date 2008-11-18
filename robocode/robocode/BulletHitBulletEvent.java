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


import robocode.peer.RobotStatics;
import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;


/**
 * This event is sent to {@link Robot#onBulletHitBullet(BulletHitBulletEvent)
 * onBulletHitBullet} when one of your bullets has hit another bullet.
 *
 * @author Mathew A. Nelson (original)
 */
public final class BulletHitBulletEvent extends Event {
	private static int classPriority = 55;

	private final Bullet bullet;
	private final Bullet hitBullet;

	/**
	 * Called by the game to create a new {@code BulletHitEvent}.
	 *
	 * @param bullet	your bullet that hit another bullet
	 * @param hitBullet the bullet that was hit by your bullet
	 */
	public BulletHitBulletEvent(Bullet bullet, Bullet hitBullet) {
		super();
		this.bullet = bullet;
		this.hitBullet = hitBullet;
	}

	/**
	 * Returns your bullet that hit another bullet.
	 *
	 * @return your bullet
	 */
	public Bullet getBullet() {
		return bullet;
	}

	/**
	 * Returns the bullet that was hit by your bullet.
	 *
	 * @return the bullet that was hit
	 */
	public Bullet getHitBullet() {
		return hitBullet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final int getDefaultPriority() {
		return classPriority;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	void dispatch(IBasicRobot robot, RobotStatics statics, Graphics2D graphics) {
		IBasicEvents listener = robot.getBasicEventListener();

		if (listener != null) {
			listener.onBulletHitBullet(this);
		}
	}
}
