/*******************************************************************************
 * Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *******************************************************************************/
package robocode;

import static java.lang.Math.PI;

import java.awt.Graphics2D;
import java.nio.ByteBuffer;

import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IBasicEvents4;
import robocode.robotinterfaces.IBasicRobot;
import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

/**
 * Extension on {@link robocode.ScannedRobotEvent}
 * @author Thales B.V. / Jiri Waning / Thomas Hakkers
 * @since 1.8.3.0 Alpha 1
 * @version 0.1
 */
public class ScannedShipEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 10; // _FOR_SCANNED_SHIP_EVENT
	
	/* ScannedRobotEvent - Duplicate */
	protected final String name;
	protected final double energy;
	protected final double heading;
	protected final double distance;
	protected final double velocity;
	
	/* ScannedShipEvent - Unique */
	private final double bearingFront;
	private final double bearingBack;
	private final double bearingRadar;
	
	/**
	 * Called by the game to create a new ScannedShipEvent.
	 * 
	 * @param name	   The name of the scanned ship.
	 * @param energy   The energy of the scanned ship.
	 * @param bearingFront  The bearing of the scanned ship, in radians, for the weapon on the front end.
	 * @param bearingBack   The bearing of the scanned ship, in radians, for the weapon on the back end.
	 * @param bearingRadar 	The bearing of the scanned ship, in radians, for the radar.
	 * @param distance The distance from your ship to the scanned ship.
	 * @param heading  The heading of the scanned ship.
	 * @param velocity The velocity of the scanned ship.
	 */
	public ScannedShipEvent(String name, double energy, double bearingFront, double bearingBack, double bearingRadar, double distance, double heading, double velocity) {
		this.name = name;
		this.energy = energy;
		this.heading = heading;
		this.distance = distance;
		this.velocity = velocity;
		
		this.bearingFront = bearingFront;
		this.bearingBack = bearingBack;
		this.bearingRadar = bearingRadar;
	}

	/**
	 * Returns the bearing to the ship you scanned, measured from the front
	 * weapon; relative to your ship's front weapon zero (absolute north!),
	 * in degrees (-180 <= getBearing() < 180)
	 * 
	 * @return The bearing to the ship you scanned, in degrees, measured from
	 *         the front weapon.
	 */
	public double getBearingFront() {
		return bearingFront * 180 / PI;
	}
	
	/**
	 * Returns the bearing to the robot you scanned, measured from the front
	 * weapon; relative to your robot's front weapon zero (absolute north!),
	 * in radians (-PI <= getBearing() < PI)
	 * 
	 * @return The bearing to the robot you scanned, in radians, measured from
	 *         the front weapon.
	 */
	public double getBearingFrontRadians() {
		return bearingFront;
	}
	
	/**
	 * Returns the bearing to the robot you scanned, measured from the back
	 * weapon; relative to your robot's back weapon zero (absolute south!),
	 * in degrees (-180 <= getBearing() < 180)
	 * 
	 * @return The bearing to the robot you scanned, in degrees, measured from
	 *         the back weapon.
	 */
	public double getBearingBack() {
		return bearingBack * 180.0d / PI;
	}
	
	/**
	 * Returns the bearing to the robot you scanned, measured from the back
	 * weapon; relative to your robot's back weapon zero (absolute south!),
	 * in radians (-PI <= getBearing() < PI)
	 * 
	 * @return The bearing to the robot you scanned, in radians, measured from
	 *         the back weapon.
	 */
	public double getBearingBackRadians() {
		return bearingBack;
	}
	
	/**
	 * Returns the distance to the other ship's origin. (Your center to his center.)
	 * @return The distance to the other ship's origin.
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Returns the energy of the ship.
	 * @return The energy of the ship.
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * Returns the heading of the ship, in degrees. (0 <= getHeading() < 360)
	 * @return The heading of the ship, in degrees.
	 */
	public double getHeading() {
		return heading * 180.0 / Math.PI;
	}

	/**
	 * Returns the heading of the ship, in radians. (0 <= getHeading() < TWO_PI)
	 * @return The heading of the ship, in radians.
	 */
	public double getHeadingRadians() {
		return heading;
	}
	
	public double getBearingRadians(){
		return bearingRadar;
	}
	
	public double getBearingDegrees(){
		return Math.toDegrees(bearingRadar);
	}
	
	/**
	 * Returns the name of the ship.
	 * @return The name of the ship.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the velocity of the ship.
	 * @return The velocity of the ship.
	 */
	public double getVelocity() {
		return velocity;
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
		// Compare the distance, if the events are ScannedShipEvents
		if (event instanceof ScannedShipEvent) {
			ScannedShipEvent obj = (ScannedShipEvent) event;
			return (this.name == obj.name &&
					this.energy == obj.energy &&
					this.heading == obj.heading &&
					this.distance == obj.distance &&
					this.velocity == obj.velocity &&
					this.bearingFront == obj.bearingFront &&
					this.bearingBack == obj.bearingBack) ? 1 : 0;
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
		IBasicEvents listener = robot.getBasicEventListener();

		if (listener != null) {
			((IBasicEvents4) listener).onScannedShip(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.ScannedShipEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			ScannedShipEvent obj = (ScannedShipEvent) object;
			
			return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(obj.name) + 6 * RbSerializer.SIZEOF_DOUBLE;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			ScannedShipEvent obj = (ScannedShipEvent) object;

			serializer.serialize(buffer, obj.name);
			serializer.serialize(buffer, obj.energy);
			serializer.serialize(buffer, obj.heading);
			serializer.serialize(buffer, obj.distance);
			serializer.serialize(buffer, obj.velocity);
			
			serializer.serialize(buffer, obj.bearingFront);
			serializer.serialize(buffer, obj.bearingBack);
			serializer.serialize(buffer, obj.bearingRadar);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			String name = serializer.deserializeString(buffer);
			double energy = buffer.getDouble();
			double heading = buffer.getDouble();
			double distance = buffer.getDouble();
			double velocity = buffer.getDouble();
			
			double bearingFront = buffer.getDouble();
			double bearingBack = buffer.getDouble();
			double bearingRadar = buffer.getDouble();

			return new ScannedShipEvent(name, energy, bearingFront, bearingBack, bearingRadar, distance, heading, velocity);
		}
	}
}
