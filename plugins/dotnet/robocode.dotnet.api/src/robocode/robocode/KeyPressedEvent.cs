#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using net.sf.robocode.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using Robocode.RobotInterfaces;

namespace Robocode
{
    /// <summary>
    /// A KeyPressedEvent is sent to <see cref="Robot.OnKeyPressed(KeyEvent)"/>
    /// when a key has been pressed on the keyboard.
    /// <seealso cref="KeyReleasedEvent"/>
    /// <seealso cref="KeyTypedEvent"/>
    /// </summary>
    [Serializable]
    public sealed class KeyPressedEvent : KeyEvent
    {
        private const int DEFAULT_PRIORITY = 98;

        /// <summary>
        /// Called by the game to create a new KeyPressedEvent.
        /// </summary>
        public KeyPressedEvent(char keyChar, int keyCode, int keyLocation, int id, int modifiersEx, long when)
            : base(keyChar, keyCode, keyLocation, id, modifiersEx, when)
        {
        }

        /// <inheritdoc />
        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <inheritdoc />
        internal override void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            if (statics.IsInteractiveRobot())
            {
                IInteractiveEvents listener = ((IInteractiveRobot) robot).GetInteractiveEventListener();

                if (listener != null)
                {
                    listener.OnKeyPressed(this);
                }
            }
        }

        /// <inheritdoc />
        internal override byte SerializationType
        {
            get { return RbSerializerN.KeyPressedEvent_TYPE; }
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
                var obj = (KeyPressedEvent) objec;
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
                return new KeyPressedEvent(keyChar, keyCode, keyLocation, id, modifiersEx, when);
            }
        }
    }
}
//doc