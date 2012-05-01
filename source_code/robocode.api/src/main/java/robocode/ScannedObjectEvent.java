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
 * This event is fired when your robot scans an object that can be scanned.
 * 
 * @author Joshua Galecki (original)
 *
 */
public class ScannedObjectEvent extends Event {

	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 20;
	
	private final double bearing;
	private final double distance;
	private final String objectType;
	private final boolean robotStopper;
	private final boolean bulletStopper;
	private final boolean scanStopper;
	private final boolean dynamic;
	
	public ScannedObjectEvent(String type, double bearing, double distance, boolean robotStopper,
			boolean bulletStopper, boolean scanStopper, boolean dynamic) {

		super();

		this.objectType = type;
		this.bearing = bearing;
		this.distance = distance;
		this.robotStopper = robotStopper;
		this.bulletStopper = bulletStopper;
		this.scanStopper = scanStopper;
		this.dynamic = dynamic;
	}

	/**
	 * Returns the distance to the closest scanned point of the object.
	 * @return the distance to the closest scanned point of the object.
	 */
	public double getDistance() {
		return distance;
	}
	
	/**
	 * Returns the angle to the closest scanned point of the object.
	 * @return the angle to the closest scanned point of the object.
	 */
	public double getBearing() {
		return bearing * 180 / Math.PI;
	}
	
	/**
	 * Returns a string with the object type.
	 * @return a string with the object type.
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * Returns whether the object will prevent robots from passing through.
	 * @return whether the object will prevent robots from passing through.
	 */
	public boolean isRobotStopper() {
		return robotStopper;
	}

	/**
	 * Returns whether the object will prevent bullets from passing through.
	 * @return whether the object will prevent bullets from passing through.
	 */
	public boolean isBulletStopper() {
		return bulletStopper;
	}

	/**
	 * Returns whether the object will prevent radar scans from passing through.
	 * @return whether the object will prevent radar scans from passing through.
	 */
	public boolean isScanStopper() {
		return scanStopper;
	}

	/**
	 * Returns whether the object will ever change position.
	 * @return whether the object will ever change position.
	 */
	public boolean isDynamic() {
		return dynamic;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int compareTo(Event event) {
		final int res = super.compareTo(event);

		if (res != 0) {
			return res;
		}
		// Compare the distance, if the events are ScannedObjectEvent
		// The shorter distance to the robot, the higher priority
		if (event instanceof ScannedObjectEvent) {
			return (int) (this.getDistance() - ((ScannedObjectEvent) event).getDistance());
		}
		// No difference found
		return 0;
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
				listener.onScannedObject(this);
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
			// HitRobotEvent obj = (HitRobotEvent) object;

			return HitObstacleEvent.SIZEOF_TYPEINFO;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {// HitObstacleEvent obj = (HitObstacleEvent) object;
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			String type = buffer.toString();
			double bearing = buffer.getDouble();
			double distance = buffer.getDouble();

			// TODO: I'm unsure what needs to be done for serialization. For now, it will be faked

			return new ScannedObjectEvent(type, bearing, distance, false, false, false, false);
		}
	}
}
