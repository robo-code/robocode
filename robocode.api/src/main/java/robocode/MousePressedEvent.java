/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.security.SafeComponent;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.robotinterfaces.IBasicRobot;
import robocode.robotinterfaces.IInteractiveEvents;
import robocode.robotinterfaces.IInteractiveRobot;

import java.awt.*;
import java.nio.ByteBuffer;


/**
 * A MousePressedEvent is sent to {@link Robot#onMousePressed(java.awt.event.MouseEvent)
 * onMousePressed()} when the mouse is pressed inside the battle view.
 *
 * @see MouseClickedEvent
 * @see MouseReleasedEvent
 * @see MouseEnteredEvent
 * @see MouseExitedEvent
 * @see MouseMovedEvent
 * @see MouseDraggedEvent
 * @see MouseWheelMovedEvent
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.1
 */
public final class MousePressedEvent extends MouseEvent {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 98;

	/**
	 * Called by the game to create a new MousePressedEvent.
	 *
	 * @param source the source mouse event originating from the AWT.
	 */
	public MousePressedEvent(java.awt.event.MouseEvent source) {
		super(source);
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
		if (statics.isInteractiveRobot()) {
			IInteractiveEvents listener = ((IInteractiveRobot) robot).getInteractiveEventListener();

			if (listener != null) {
				listener.onMousePressed(getSourceEvent());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.MousePressedEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {

		public int sizeOf(RbSerializer serializer, Object object) {
			return RbSerializer.SIZEOF_TYPEINFO + 6 * RbSerializer.SIZEOF_INT + RbSerializer.SIZEOF_LONG;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			MousePressedEvent obj = (MousePressedEvent) object;
			java.awt.event.MouseEvent src = obj.getSourceEvent();

			serializer.serialize(buffer, src.getButton());
			serializer.serialize(buffer, src.getClickCount());
			serializer.serialize(buffer, src.getX());
			serializer.serialize(buffer, src.getY());
			serializer.serialize(buffer, src.getID());
			serializer.serialize(buffer, src.getModifiersEx());
			serializer.serialize(buffer, src.getWhen());
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			int button = buffer.getInt();
			int clickCount = buffer.getInt();
			int x = buffer.getInt();
			int y = buffer.getInt();
			int id = buffer.getInt();
			int modifiersEx = buffer.getInt();
			long when = buffer.getLong();

			return new MousePressedEvent(
					new java.awt.event.MouseEvent(SafeComponent.getSafeEventComponent(), id, when, modifiersEx, x, y,
					clickCount, false, button));
		}
	}
}
