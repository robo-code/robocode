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
    /// A MouseReleasedEvent is sent to {@link Robot#OnMouseReleased(java.awt.event.MouseEvent)
    /// OnMouseReleased()} when the mouse is released inside the battle view.
    ///
    /// @author Pavel Savara (original)
    /// @see MouseClickedEvent
    /// @see MousePressedEvent
    /// @see MouseEnteredEvent
    /// @see MouseExitedEvent
    /// @see MouseMovedEvent
    /// @see MouseDraggedEvent
    /// @see MouseWheelMovedEvent
    /// @since 1.6.1
    /// </summary>
    [Serializable]
    public sealed class MouseReleasedEvent : MouseEvent
    {
        private const int DEFAULT_PRIORITY = 98;

        /// <summary>
        /// Called by the game to create a new MouseDraggedEvent.
        ///
        /// @param source the source mouse evnt originating from the AWT.
        /// </summary>
        public MouseReleasedEvent(int button, int clickCount, int x, int y, int id, int modifiersEx, long when)
            : base(button, clickCount, x, y, id, modifiersEx, when)
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
                    listener.OnMouseReleased(this);
                }
            }
        }

        /// <inheritdoc />
        internal override byte SerializationType
        {
            get { return RbSerializerN.MouseReleasedEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                return RbSerializerN.SIZEOF_TYPEINFO + 6*RbSerializerN.SIZEOF_INT + RbSerializerN.SIZEOF_LONG;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
                var obj = (MouseReleasedEvent)objec;

                serializer.serialize(buffer, obj.Button);
                serializer.serialize(buffer, obj.ClickCount);
                serializer.serialize(buffer, obj.X);
                serializer.serialize(buffer, obj.Y);
                serializer.serialize(buffer, obj.ID);
                serializer.serialize(buffer, obj.ModifiersEx);
                serializer.serialize(buffer, obj.When);
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                int button = buffer.getInt();
                int clickCount = buffer.getInt();
                int x = buffer.getInt();
                int y = buffer.getInt();
                int id = buffer.getInt();
                int modifiersEx = buffer.getInt();
                long when = buffer.getLong();

                return new MouseReleasedEvent(button, clickCount, x, y, id, modifiersEx, when);
            }
        }
    }
}

//happy