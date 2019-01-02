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
 * A HitWallEvent is sent to {@link Robot#onHitWall(HitWallEvent) onHitWall()}
 * when you collide a wall.
 * You can use the information contained in this event to determine what to do.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public final class HitWallEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 30;

	private final double bearing;

	/**
	 * Called by the game to create a new HitWallEvent.
	 *
	 * @param bearing the bearing to the wall that your robot hit, in radians
	 */
	public HitWallEvent(double bearing) {
		this.bearing = bearing;
	}

	/**
	 * Returns the bearing to the wall you hit, relative to your robot's
	 * heading, in degrees (-180 <= getBearing() < 180)
	 *
	 * @return the bearing to the wall you hit, in degrees
	 */
	public double getBearing() {
		return bearing * 180.0 / Math.PI;
	}

	/**
	 * @return the bearing to the wall you hit, in degrees
	 * @deprecated Use {@link #getBearing()} instead.
	 */
	@Deprecated
	public double getBearingDegrees() {
		return getBearing();
	}

	/**
	 * Returns the bearing to the wall you hit, relative to your robot's
	 * heading, in radians (-PI <= getBearingRadians() < PI)
	 *
	 * @return the bearing to the wall you hit, in radians
	 */
	public double getBearingRadians() {
		return bearing;
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
			listener.onHitWall(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.HitWallEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_DOUBLE;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			HitWallEvent obj = (HitWallEvent) object;

			serializer.serialize(buffer, obj.bearing);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			double bearing = buffer.getDouble();

			return new HitWallEvent(bearing);
		}
	}
}
