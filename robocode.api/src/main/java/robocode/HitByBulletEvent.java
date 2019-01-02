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
 * A HitByBulletEvent is sent to {@link Robot#onHitByBullet(HitByBulletEvent)
 * onHitByBullet()} when your robot has been hit by a bullet.
 * You can use the information contained in this event to determine what to do.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public final class HitByBulletEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 20;

	private final double bearing;
	private final Bullet bullet;

	/**
	 * Called by the game to create a new HitByBulletEvent.
	 *
	 * @param bearing the bearing of the bullet that hit your robot, in radians
	 * @param bullet  the bullet that has hit your robot
	 */
	public HitByBulletEvent(double bearing, Bullet bullet) {
		super();
		this.bearing = bearing;
		this.bullet = bullet;
	}

	/**
	 * Returns the bearing to the bullet, relative to your robot's heading,
	 * in degrees (-180 < getBearing() <= 180).
	 * <p>
	 * If you were to turnRight(event.getBearing()), you would be facing the
	 * direction the bullet came from. The calculation used here is:
	 * (bullet's heading in degrees + 180) - (your heading in degrees)
	 *
	 * @return the bearing to the bullet, in degrees
	 */
	public double getBearing() {
		return bearing * 180.0 / Math.PI;
	}

	/**
	 * Returns the bearing to the bullet, relative to your robot's heading,
	 * in radians (-Math.PI < getBearingRadians() <= Math.PI).
	 * <p>
	 * If you were to turnRightRadians(event.getBearingRadians()), you would be
	 * facing the direction the bullet came from. The calculation used here is:
	 * (bullet's heading in radians + Math.PI) - (your heading in radians)
	 *
	 * @return the bearing to the bullet, in radians
	 */
	public double getBearingRadians() {
		return bearing;
	}

	/**
	 * Returns the bullet that hit your robot.
	 *
	 * @return the bullet that hit your robot
	 */
	public Bullet getBullet() {
		return bullet;
	}

	/**
	 * Returns the heading of the bullet when it hit you, in degrees
	 * (0 <= getHeading() < 360).
	 * <p>
	 * Note: This is not relative to the direction you are facing. The robot
	 * that fired the bullet was in the opposite direction of getHeading() when
	 * it fired the bullet.
	 *
	 * @return the heading of the bullet, in degrees
	 */
	public double getHeading() {
		return bullet.getHeading();
	}

	/**
	 * @return the heading of the bullet, in degrees
	 * @deprecated Use {@link #getHeading()} instead.
	 */
	@Deprecated
	public double getHeadingDegrees() {
		return getHeading();
	}

	/**
	 * Returns the heading of the bullet when it hit you, in radians
	 * (0 <= getHeadingRadians() < 2 * PI).
	 * <p>
	 * Note: This is not relative to the direction you are facing. The robot
	 * that fired the bullet was in the opposite direction of
	 * getHeadingRadians() when it fired the bullet.
	 *
	 * @return the heading of the bullet, in radians
	 */
	public double getHeadingRadians() {
		return bullet.getHeadingRadians();
	}

	/**
	 * Returns the name of the robot that fired the bullet.
	 *
	 * @return the name of the robot that fired the bullet
	 */
	public String getName() {
		return bullet.getName();
	}

	/**
	 * Returns the power of this bullet. The damage you take (in fact, already
	 * took) is 4 * power, plus 2 * (power-1) if power > 1. The robot that fired
	 * the bullet receives 3 * power back.
	 *
	 * @return the power of the bullet
	 */
	public double getPower() {
		return bullet.getPower();
	}

	/**
	 * Returns the velocity of this bullet.
	 *
	 * @return the velocity of the bullet
	 */
	public double getVelocity() {
		return bullet.getVelocity();
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
			listener.onHitByBullet(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.HitByBulletEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			HitByBulletEvent obj = (HitByBulletEvent) object;

			return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(RbSerializer.Bullet_TYPE, obj.bullet)
					+ RbSerializer.SIZEOF_DOUBLE;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			HitByBulletEvent obj = (HitByBulletEvent) object;

			serializer.serialize(buffer, RbSerializer.Bullet_TYPE, obj.bullet);
			serializer.serialize(buffer, obj.bearing);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			Bullet bullet = (Bullet) serializer.deserializeAny(buffer);
			double bearing = buffer.getDouble();

			return new HitByBulletEvent(bearing, bullet);
		}
	}
}
