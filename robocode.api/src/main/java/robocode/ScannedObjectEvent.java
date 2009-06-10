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
import robocode.robotinterfaces.IBasicEvents2;
import robocode.robotinterfaces.IBasicRobot;

/**
 * This event is fired when your robot scans an object that can be scanned.
 * 
 * @author Joshua Galecki (original)
 *
 */
public class ScannedObjectEvent extends Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String objectType;
	private double bearing;
	private double distance;

	private final static int DEFAULT_PRIORITY = 20;
	
	public ScannedObjectEvent(String type, double bearing, double distance) {
		objectType = type;
		this.bearing = bearing;
		this.distance = distance;
	}

	public double getDistance() {
		return distance;
	}

	public double getBearing() {
		return bearing;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
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
		IBasicEvents2 listener = (IBasicEvents2) robot.getBasicEventListener();

		if (listener != null) {
			listener.onScannedObject(this);
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

			return HitObstacleEvent.SIZEOF_TYPEINFO ;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			HitObstacleEvent obj = (HitObstacleEvent) object;

		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			double bearing = buffer.getDouble();

			return new HitObstacleEvent(bearing);
		}
	}
}
