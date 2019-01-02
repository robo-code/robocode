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
 * A HitRobotEvent is sent to {@link Robot#onHitRobot(HitRobotEvent) onHitRobot()}
 * when your robot collides with another robot.
 * You can use the information contained in this event to determine what to do.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public final class HitRobotEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 40;

	private final String robotName;
	private final double bearing;
	private final double energy;
	private final boolean atFault;

	/**
	 * Called by the game to create a new HitRobotEvent.
	 *
	 * @param name	the name of the robot you hit
	 * @param bearing the bearing to the robot that your robot hit, in radians
	 * @param energy  the amount of energy of the robot you hit
	 * @param atFault {@code true} if your robot was moving toward the other
	 *                robot; {@code false} otherwise
	 */
	public HitRobotEvent(String name, double bearing, double energy, boolean atFault) {
		this.robotName = name;
		this.bearing = bearing;
		this.energy = energy;
		this.atFault = atFault;
	}

	/**
	 * Returns the bearing to the robot you hit, relative to your robot's
	 * heading, in degrees (-180 <= getBearing() < 180)
	 *
	 * @return the bearing to the robot you hit, in degrees
	 */
	public double getBearing() {
		return bearing * 180.0 / Math.PI;
	}

	/**
	 * @return the bearing to the robot you hit, in degrees
	 * @deprecated Use {@link #getBearing()} instead.
	 */
	@Deprecated
	public double getBearingDegrees() {
		return getBearing();
	}

	/**
	 * Returns the bearing to the robot you hit, relative to your robot's
	 * heading, in radians (-PI <= getBearingRadians() < PI)
	 *
	 * @return the bearing to the robot you hit, in radians
	 */
	public double getBearingRadians() {
		return bearing;
	}

	/**
	 * Returns the amount of energy of the robot you hit.
	 *
	 * @return the amount of energy of the robot you hit
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * Returns the name of the robot you hit.
	 *
	 * @return the name of the robot you hit
	 */
	public String getName() {
		return robotName;
	}

	/**
	 * @return the name of the robot you hit
	 * @deprecated Use {@link #getName()} instead.
	 */
	@Deprecated
	public String getRobotName() {
		return robotName;
	}

	/**
	 * Checks if your robot was moving towards the robot that was hit.
	 * <p>
	 * If {@link #isMyFault()} returns {@code true} then your robot's movement
	 * (including turning) will have stopped and been marked complete.
	 * <p>
	 * Note: If two robots are moving toward each other and collide, they will
	 * each receive two HitRobotEvents. The first will be the one if
	 * {@link #isMyFault()} returns {@code true}.
	 *
	 * @return {@code true} if your robot was moving towards the robot that was
	 *         hit; {@code false} otherwise.
	 */
	public boolean isMyFault() {
		return atFault;
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

		// Compare the isMyFault, if the events are HitRobotEvents
		// The isMyFault has higher priority when it is set compared to when it is not set
		if (event instanceof HitRobotEvent) {
			int compare1 = (this).isMyFault() ? -1 : 0;
			int compare2 = ((HitRobotEvent) event).isMyFault() ? -1 : 0;

			return compare1 - compare2;
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
			listener.onHitRobot(this);
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

			return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(obj.robotName) + 2 * RbSerializer.SIZEOF_DOUBLE
					+ RbSerializer.SIZEOF_BOOL;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			HitRobotEvent obj = (HitRobotEvent) object;

			serializer.serialize(buffer, obj.robotName);
			serializer.serialize(buffer, obj.bearing);
			serializer.serialize(buffer, obj.energy);
			serializer.serialize(buffer, obj.atFault);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			String robotName = serializer.deserializeString(buffer);
			double bearing = buffer.getDouble();
			double energy = buffer.getDouble();
			boolean atFault = serializer.deserializeBoolean(buffer);

			return new HitRobotEvent(robotName, bearing, energy, atFault);
		}
	}
}
