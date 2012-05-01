/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 * 		Joshua Galecki
 * 		-Initial implementation
 *******************************************************************************/

package robocode;


import java.awt.Graphics2D;
import java.nio.ByteBuffer;

import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.IObjectEvents;


/**
 * This event is fired off when your robot hits an object that
 * halts the robot, like a wall would.
 * 
 * @author Joshua Galecki
 */
public final class HitObstacleEvent extends Event {

	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 39;

	static final int SIZEOF_TYPEINFO = 0;

	private final double bearing;
	private final String obstacleType;

	public HitObstacleEvent(double bearing, String obstacleType) {
		this.bearing = bearing;
		this.obstacleType = obstacleType;
	}

	public String getObstacleType() {
		return obstacleType;
	}

	/**
	 * Returns the bearing to the object you hit, relative to your robot's
	 * heading, in degrees (-180 <= getBearing() < 180)
	 *
	 * @return the bearing to the object you hit, in degrees
	 */
	public double getBearing() {
		return bearing * 180.0 / Math.PI;
	}
	
	/**
	 * Returns the bearing to the object you hit, relative to your robot's
	 * heading, in radians (-PI <= getBearingRadians() < PI)
	 *
	 * @return the bearing to the object you hit, in radians
	 */
	public double getBearingRadians() {
		return bearing;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(Event event) {
		final int res = super.compareTo(event);

		return res;
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
		try {
			IObjectEvents listener = (IObjectEvents) robot.getBasicEventListener();
	
			if (listener != null) {
				listener.onHitObstacle(this);
			}

		} catch (ClassCastException ex) {// The robot does not use objects, do nothing;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.HitRobotEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			HitRobotEvent obj = (HitRobotEvent) object;

			return HitObstacleEvent.SIZEOF_TYPEINFO;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			HitObstacleEvent obj = (HitObstacleEvent) object;

		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			double bearing = buffer.getDouble();
			String type = buffer.toString();
			
			return new HitObstacleEvent(bearing, type);
		}
	}
}
