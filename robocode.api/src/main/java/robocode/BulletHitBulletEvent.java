/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IBasicRobot;

import java.awt.*;
import java.nio.ByteBuffer;


/**
 * This event is sent to {@link Robot#onBulletHitBullet(BulletHitBulletEvent)
 * onBulletHitBullet} when one of your bullets has hit another bullet.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public final class BulletHitBulletEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 55;

	private Bullet bullet;
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
		return DEFAULT_PRIORITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
		IBasicEvents listener = robot.getBasicEventListener();

		if (listener != null) {
			listener.onBulletHitBullet(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.BulletHitBulletEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			BulletHitBulletEvent obj = (BulletHitBulletEvent) object;

			return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT
					+ serializer.sizeOf(RbSerializer.Bullet_TYPE, obj.hitBullet);
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			BulletHitBulletEvent obj = (BulletHitBulletEvent) object;

			// no need to transmit whole bullet, rest of it is already known to proxy side
			serializer.serialize(buffer, obj.bullet.getBulletId());
			serializer.serialize(buffer, RbSerializer.Bullet_TYPE, obj.hitBullet);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			Bullet bullet = new Bullet(0, 0, 0, 0, null, null, false, buffer.getInt());
			Bullet hitBullet = (Bullet) serializer.deserializeAny(buffer);

			return new BulletHitBulletEvent(bullet, hitBullet);
		}
	}
}
