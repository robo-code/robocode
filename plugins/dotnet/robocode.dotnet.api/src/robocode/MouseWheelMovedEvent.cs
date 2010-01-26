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
    /// A MouseWheelMovedEvent is sent to {@link Robot#OnMouseWheelMoved(java.awt.event.MouseWheelEvent e)
    /// OnMouseWheelMoved()} when the mouse wheel is rotated inside the battle view.
    ///
    /// @author Pavel Savara (original)
    /// @see MouseClickedEvent
    /// @see MousePressedEvent
    /// @see MouseReleasedEvent
    /// @see MouseEnteredEvent
    /// @see MouseExitedEvent
    /// @see MouseMovedEvent
    /// @see MouseDraggedEvent
    /// @since 1.6.1
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
        ///
        /// @param source the source mouse evnt originating from the AWT.
        /// </summary>
        public MouseWheelMovedEvent(int clickCount, int x, int y, int scrollType, int scrollAmount, int wheelRotation,
                                    int id, int modifiersEx, long when)
            : base(-1, clickCount, x, y, id, modifiersEx, when)
        {
            this.scrollType = scrollType;
            this.scrollAmount = scrollAmount;
            this.wheelRotation = wheelRotation;
        }

        public int ScrollType
        {
            get { return scrollType; }
        }

        public int ScrollAmount
        {
            get { return scrollAmount; }
        }

        public int WheelRotation
        {
            get { return wheelRotation; }
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
                    listener.OnMouseWheelMoved(this);
                }
            }
        }

        /// <inheritdoc />
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
//happy