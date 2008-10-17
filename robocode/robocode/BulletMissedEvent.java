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


import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.IBasicEvents;
import robocode.peer.RobotStatics;

import java.awt.*;


/**
 * This event is sent to {@link Robot#onBulletMissed(BulletMissedEvent)
 * onBulletMissed} when one of your bullets has missed, i.e. when the bullet has
 * reached the border of the battlefield.
 *
 * @author Mathew A. Nelson (original)
 */
public final class BulletMissedEvent extends Event {
	private final Bullet bullet;

	/**
	 * Called by the game to create a new {@code BulletMissedEvent}.
	 *
	 * @param bullet the bullet that missed
	 */
	public BulletMissedEvent(Bullet bullet) {
		this.bullet = bullet;
	}

	/**
	 * Returns the bullet that missed.
	 *
	 * @return the bullet that missed
	 */
	public Bullet getBullet() {
		return bullet;
	}

	private static int classPriority = 60;

	@Override
	protected final int getClassPriorityImpl() {
		return classPriority;
	}

	@Override
	protected void setClassPriorityImpl(int priority) {
		classPriority = priority;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispatch(IBasicRobot robot, RobotStatics statics, Graphics2D graphics) {
		IBasicEvents listener = robot.getBasicEventListener();

		if (listener != null) {
			listener.onBulletMissed(this);
		}
	}
}
