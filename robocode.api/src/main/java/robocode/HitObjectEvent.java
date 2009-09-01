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
 * This event is fired when the robot hits an object that does not
 * stop the robot.
 * 
 * @author Joshua Galecki (original)
 */
public final class HitObjectEvent extends Event {

	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 39;

	private static final int SIZEOF_TYPEINFO = 0;
	private final String type;

	public HitObjectEvent(String type) {
		this.type = type;
	}

	/**
	 * Returns a string with the object type.
	 * @return a string with the object type.
	 */
	public String getType() {
		return type;
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
				listener.onHitObject(this);
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

			return HitObjectEvent.SIZEOF_TYPEINFO;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			HitObjectEvent obj = (HitObjectEvent) object;

		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			String type = buffer.toString();

			return new HitObjectEvent(type);
		}
	}
}
