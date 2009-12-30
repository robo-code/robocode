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
using System;
using System.Drawing;
using net.sf.robocode.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    /// <summary>
    /// A KeyTypedEvent is sent to {@link Robot#OnKeyTyped(java.awt.event.KeyEvent)
    /// OnKeyTyped()} when a key has been typed (pressed and released) on the keyboard.
    ///
    /// @author Pavel Savara (original)
    /// @see KeyPressedEvent
    /// @see KeyReleasedEvent
    /// @since 1.6.1
    /// </summary>
    [Serializable]
    public sealed class KeyTypedEvent : KeyEvent
    {
        private const int DEFAULT_PRIORITY = 98;

        /// <summary>
        /// Called by the game to create a new KeyTypedEvent.
        ///
        /// @param source the source key evnt originating from the AWT.
        /// </summary>
        public KeyTypedEvent(char keyChar, int keyCode, int keyLocation, int id, int modifiersEx, long when)
            : base(keyChar, keyCode, keyLocation, id, modifiersEx, when)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            if (statics.IsInteractiveRobot())
            {
                IInteractiveEvents listener = ((IInteractiveRobot) robot).GetInteractiveEventListener();

                if (listener != null)
                {
                    listener.OnKeyTyped(this);
                }
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override byte SerializationType
        {
            get { return RbSerializerN.KeyTypedEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                return RbSerializerN.SIZEOF_TYPEINFO + RbSerializerN.SIZEOF_CHAR + RbSerializerN.SIZEOF_INT
                       + RbSerializerN.SIZEOF_INT + RbSerializerN.SIZEOF_LONG + RbSerializerN.SIZEOF_INT +
                       RbSerializerN.SIZEOF_INT;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
                var obj = (KeyTypedEvent) objec;

                serializer.serialize(buffer, obj.KeyChar);
                serializer.serialize(buffer, obj.KeyCode);
                serializer.serialize(buffer, obj.KeyLocation);
                serializer.serialize(buffer, obj.ID);
                serializer.serialize(buffer, obj.ModifiersEx);
                serializer.serialize(buffer, obj.When);
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                char keyChar = buffer.getChar();
                int keyCode = buffer.getInt();
                int keyLocation = buffer.getInt();
                int id = buffer.getInt();
                int modifiersEx = buffer.getInt();
                long when = buffer.getLong();

                return new KeyTypedEvent(keyChar, keyCode, keyLocation, id, modifiersEx, when);
            }
        }
    }
}
//happy