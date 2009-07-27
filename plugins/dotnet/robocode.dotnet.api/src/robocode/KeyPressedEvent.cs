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
using System.Drawing;
using java.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    /// <summary>
    /// A KeyPressedEvent is sent to {@link Robot#onKeyPressed(java.awt.event.KeyEvent)
    /// onKeyPressed()} when a key has been pressed on the keyboard.
    ///
    /// @author Pavel Savara (original)
    /// @see KeyReleasedEvent
    /// @see KeyTypedEvent
    /// @since 1.6.1
    /// </summary>
    public sealed class KeyPressedEvent : KeyEvent
    {
        private const int DEFAULT_PRIORITY = 98;

        /// <summary>
        /// Called by the game to create a new KeyPressedEvent.
        ///
        /// @param source the source key evnt originating from the AWT.
        /// </summary>
        public KeyPressedEvent(char keyChar, int keyCode, int keyLocation, int id, int modifiersEx, long when)
            : base(keyChar, keyCode, keyLocation, id, modifiersEx, when)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override int getDefaultPriority()
        {
            return DEFAULT_PRIORITY;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics graphics)
        {
            if (statics.isInteractiveRobot())
            {
                IInteractiveEvents listener = ((IInteractiveRobot) robot).getInteractiveEventListener();

                if (listener != null)
                {
                    listener.onKeyPressed(this);
                }
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override byte getSerializationType()
        {
            return RbSerializer.KeyPressedEvent_TYPE;
        }

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelper
        {
            public int sizeOf(RbSerializer serializer, object objec)
            {
                return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_CHAR + RbSerializer.SIZEOF_INT
                       + RbSerializer.SIZEOF_INT + RbSerializer.SIZEOF_LONG + RbSerializer.SIZEOF_INT +
                       RbSerializer.SIZEOF_INT;
            }

            public void serialize(RbSerializer serializer, IByteBuffer buffer, object objec)
            {
                var obj = (KeyPressedEvent) objec;
                serializer.serialize(buffer, obj.getKeyChar());
                serializer.serialize(buffer, obj.getKeyCode());
                serializer.serialize(buffer, obj.getKeyLocation());
                serializer.serialize(buffer, obj.getID());
                serializer.serialize(buffer, obj.getModifiersEx());
                serializer.serialize(buffer, obj.getWhen());
            }

            public object deserialize(RbSerializer serializer, IByteBuffer buffer)
            {
                char keyChar = buffer.getChar();
                int keyCode = buffer.getInt();
                int keyLocation = buffer.getInt();
                int id = buffer.getInt();
                int modifiersEx = buffer.getInt();
                long when = buffer.getLong();
                return new KeyPressedEvent(keyChar, keyCode, keyLocation, id, modifiersEx, when);
            }
        }
    }
}
//happy