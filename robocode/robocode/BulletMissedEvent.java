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


import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Hashtable;


/**
 * This event is sent to {@link Robot#onBulletMissed(BulletMissedEvent)
 * onBulletMissed} when one of your bullets has missed, i.e. when the bullet has
 * reached the border of the battlefield.
 *
 * @author Mathew A. Nelson (original)
 */
public final class BulletMissedEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 60;

	private Bullet bullet;

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	final int getDefaultPriority() {
		return DEFAULT_PRIORITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
		IBasicEvents listener = robot.getBasicEventListener();

		if (listener != null) {
			listener.onBulletMissed(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void updateBullets(Hashtable<Integer, Bullet> bullets) {
		// we need to pass same instance
		bullet = bullets.get(bullet.getBulletId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.BulletMissedEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			BulletMissedEvent obj = (BulletMissedEvent) object;

			serializer.serialize(buffer, obj.bullet.getBulletId());
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			Bullet bullet = new Bullet(0, 0, 0, 0, null, null, false, buffer.getInt());

			return new BulletMissedEvent(bullet);
		}
	}
}
