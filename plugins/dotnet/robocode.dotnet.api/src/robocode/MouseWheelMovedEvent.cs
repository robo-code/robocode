/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using net.sf.robocode.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using Robocode.RobotInterfaces;

namespace Robocode
{
    /// <summary>
    /// A MouseWheelMovedEvent is sent to <see cref="Robot.OnMouseWheelMoved(MouseWheelMovedEvent)"/>
    /// when the mouse wheel is rotated inside the battle view.
    /// <seealso cref="MouseClickedEvent"/>
    /// <seealso cref="MousePressedEvent"/>
    /// <seealso cref="MouseReleasedEvent"/>
    /// <seealso cref="MouseEnteredEvent"/>
    /// <seealso cref="MouseExitedEvent"/>
    /// <seealso cref="MouseMovedEvent"/>
    /// <seealso cref="MouseDraggedEvent"/>
    /// </summary>
    [Serializable]
    public sealed class MouseWheelMovedEvent : MouseEvent
    {
        private const int DEFAULT_PRIORITY = 98;
        private readonly int scrollType;
        private readonly int scrollAmount;
        private readonly int wheelRotation;

        /// <summary>
        /// Called by the game to create a new MouseWheelMovedEvent.
        /// </summary>
        public MouseWheelMovedEvent(int clickCount, int x, int y, int scrollType, int scrollAmount, int wheelRotation,
                                    int id, int modifiersEx, long when)
            : base(-1, clickCount, x, y, id, modifiersEx, when)
        {
            this.scrollType = scrollType;
            this.scrollAmount = scrollAmount;
            this.wheelRotation = wheelRotation;
        }

        internal int ScrollType
        {
            get { return scrollType; }
        }

        internal int ScrollAmount
        {
            get { return scrollAmount; }
        }

        /// <summary>
        /// Indicates how far the mouse wheel was rotated.
        /// </summary>
        public int WheelRotation
        {
            get { return wheelRotation; }
        }


        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        internal override void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            if (statics.IsInteractiveRobot())
            {
                IInteractiveEvents listener = ((IInteractiveRobot) robot).GetInteractiveEventListener();

                if (listener != null)
                {
                    listener.OnMouseWheelMoved(this);
                }
            }
        }

        internal override byte SerializationType
        {
            get { return RbSerializerN.MouseWheelMovedEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                return RbSerializerN.SIZEOF_TYPEINFO + 8*RbSerializerN.SIZEOF_INT + RbSerializerN.SIZEOF_LONG;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
                var obj = (MouseWheelMovedEvent) objec;

                serializer.serialize(buffer, obj.ClickCount);
                serializer.serialize(buffer, obj.X);
                serializer.serialize(buffer, obj.Y);
                serializer.serialize(buffer, obj.ScrollType);
                serializer.serialize(buffer, obj.ScrollAmount);
                serializer.serialize(buffer, obj.WheelRotation);
                serializer.serialize(buffer, obj.ID);
                serializer.serialize(buffer, obj.ModifiersEx);
                serializer.serialize(buffer, obj.When);
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                int clickCount = buffer.getInt();
                int x = buffer.getInt();
                int y = buffer.getInt();
                int scrollType = buffer.getInt();
                int scrollAmount = buffer.getInt();
                int wheelRotation = buffer.getInt();
                int id = buffer.getInt();
                int modifiersEx = buffer.getInt();
                long when = buffer.getLong();

                return new MouseWheelMovedEvent(clickCount, x, y, scrollType, scrollAmount, wheelRotation, id,
                                                modifiersEx, when);
            }
        }
    }
}
//doc