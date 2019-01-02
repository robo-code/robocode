/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
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
 * This event is sent to {@link Robot#onBulletHit(BulletHitEvent) onBulletHit}
 * when one of your bullets has hit another robot.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public final class BulletHitEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 50;

	private final String name;
	private final double energy;
	private Bullet bullet;

	/**
	 * Called by the game to create a new {@code BulletHitEvent}.
	 *
	 * @param name   the name of the robot your bullet hit
	 * @param energy the remaining energy of the robot that your bullet has hit
	 * @param bullet the bullet that hit the robot
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
	 * @return energy the remaining energy of the robot that your bullet has hit
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
	 * @return energy the remaining energy of the robot that your bullet has hit
	 * @deprecated Use {@link #getEnergy()} instead.
	 */
	@Deprecated
	public double getRobotLife() {
		return energy;
	}

	/**
	 * @return the name of the robot your bullet hit.
	 * @deprecated Use {@link #getName()} instead.
	 */
	@Deprecated
	public String getRobotName() {
		return name;
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
			listener.onBulletHit(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.BulletHitEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			BulletHitEvent obj = (BulletHitEvent) object;

			return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT + serializer.sizeOf(obj.name)
					+ RbSerializer.SIZEOF_DOUBLE;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			BulletHitEvent obj = (BulletHitEvent) object;

			serializer.serialize(buffer, obj.bullet.getBulletId());
			serializer.serialize(buffer, obj.name);
			serializer.serialize(buffer, obj.energy);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			Bullet bullet = new Bullet(0, 0, 0, 0, null, null, false, buffer.getInt());
			String name = serializer.deserializeString(buffer);
			double energy = buffer.getDouble();

			return new BulletHitEvent(name, energy, bullet);
		}
	}
}
