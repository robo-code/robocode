/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
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
 * A KeyPressedEvent is sent to {@link Robot#onKeyPressed(java.awt.event.KeyEvent)
 * onKeyPressed()} when a key has been pressed on the keyboard.
 *
 * @see KeyReleasedEvent
 * @see KeyTypedEvent
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.1
 */
public final class KeyPressedEvent extends KeyEvent {
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 98;

	/**
	 * Called by the game to create a new KeyPressedEvent.
	 *
	 * @param source the source key event originating from the AWT.
	 */
	public KeyPressedEvent(java.awt.event.KeyEvent source) {
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
				listener.onKeyPressed(getSourceEvent());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.KeyPressedEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {

		public int sizeOf(RbSerializer serializer, Object object) {
			return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_CHAR + RbSerializer.SIZEOF_INT
					+ RbSerializer.SIZEOF_INT + RbSerializer.SIZEOF_LONG + RbSerializer.SIZEOF_INT + RbSerializer.SIZEOF_INT;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			KeyPressedEvent obj = (KeyPressedEvent) object;
			java.awt.event.KeyEvent src = obj.getSourceEvent();

			serializer.serialize(buffer, src.getKeyChar());
			serializer.serialize(buffer, src.getKeyCode());
			serializer.serialize(buffer, src.getKeyLocation());
			serializer.serialize(buffer, src.getID());
			serializer.serialize(buffer, src.getModifiersEx());
			serializer.serialize(buffer, src.getWhen());
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			char keyChar = buffer.getChar();
			int keyCode = buffer.getInt();
			int keyLocation = buffer.getInt();
			int id = buffer.getInt();
			int modifiersEx = buffer.getInt();
			long when = buffer.getLong();

			return new KeyPressedEvent(
					new java.awt.event.KeyEvent(SafeComponent.getSafeEventComponent(), id, when, modifiersEx, keyCode, keyChar,
					keyLocation));
		}
	}
}
