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
 * This event is sent to {@link Robot#onRobotDeath(RobotDeathEvent) onRobotDeath()}
 * when another robot (not your robot) dies.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public final class RobotDeathEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 70;

	private final String robotName;

	/**
	 * Called by the game to create a new RobotDeathEvent.
	 *
	 * @param robotName the name of the robot that died
	 */
	public RobotDeathEvent(String robotName) {
		super();
		this.robotName = robotName;
	}

	/**
	 * Returns the name of the robot that died.
	 *
	 * @return the name of the robot that died
	 */
	public String getName() {
		return robotName;
	}

	/**
	 * @return the name of the robot that died
	 * @deprecated Use {@link #getName()} instead.
	 */
	@Deprecated
	public String getRobotName() {
		return robotName;
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
			listener.onRobotDeath(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.RobotDeathEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			RobotDeathEvent obj = (RobotDeathEvent) object;

			return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(obj.robotName);
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			RobotDeathEvent obj = (RobotDeathEvent) object;

			serializer.serialize(buffer, obj.robotName);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			String name = serializer.deserializeString(buffer);

			return new RobotDeathEvent(name);
		}
	}
}
