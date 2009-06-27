/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *     Flemming N. Larsen
 *     - Javadocs
 *******************************************************************************/
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
 * A MouseDraggedEvent is sent to {@link Robot#onMouseDragged(java.awt.event.MouseEvent)
 * onMouseDragged()} when the mouse is dragged inside the battle view.
 *
 * @author Pavel Savara (original)
 * @see MouseClickedEvent
 * @see MousePressedEvent
 * @see MouseReleasedEvent
 * @see MouseEnteredEvent
 * @see MouseExitedEvent
 * @see MouseMovedEvent
 * @see MouseWheelMovedEvent
 * @since 1.6.1
 */
public final class MouseDraggedEvent extends MouseEvent {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 98;

	/**
	 * Called by the game to create a new MouseDraggedEvent.
	 *
	 * @param source the source mouse event originating from the AWT.
	 */
	public MouseDraggedEvent(java.awt.event.MouseEvent source) {
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
				listener.onMouseDragged(getSourceEvent());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.MouseDraggedEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {

		public int sizeOf(RbSerializer serializer, Object object) {
			return RbSerializer.SIZEOF_TYPEINFO + 6 * RbSerializer.SIZEOF_INT + RbSerializer.SIZEOF_LONG;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			MouseDraggedEvent obj = (MouseDraggedEvent) object;
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

			return new MouseDraggedEvent(
					new java.awt.event.MouseEvent(SafeComponent.getSafeEventComponent(), id, when, modifiersEx, x, y,
					clickCount, false, button));
		}
	}

}
