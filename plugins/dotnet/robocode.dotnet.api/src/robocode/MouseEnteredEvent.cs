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
using java.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    /// <summary>
    /// A MouseEnteredEvent is sent to {@link Robot#onMouseEntered(java.awt.event.MouseEvent)
    /// onMouseEntered()} when the mouse has entered the battle view.
    ///
    /// @author Pavel Savara (original)
    /// @see MouseClickedEvent
    /// @see MousePressedEvent
    /// @see MouseReleasedEvent
    /// @see MouseExitedEvent
    /// @see MouseMovedEvent
    /// @see MouseDraggedEvent
    /// @see MouseWheelMovedEvent
    /// @since 1.6.1
    /// </summary>
    [Serializable]
    public sealed class MouseEnteredEvent : MouseEvent
    {
        private const int DEFAULT_PRIORITY = 98;

        /// <summary>
        /// Called by the game to create a new MouseDraggedEvent.
        ///
        /// @param source the source mouse evnt originating from the AWT.
        /// </summary>
        public MouseEnteredEvent(int button, int clickCount, int x, int y, int id, int modifiersEx, long when)
            : base(button, clickCount, x, y, id, modifiersEx, when)
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
                    listener.onMouseDragged(this);
                }
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override byte getSerializationType()
        {
            return RbSerializer.MouseDraggedEvent_TYPE;
        }

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelper
        {
            public int sizeOf(RbSerializer serializer, object objec)
            {
                return RbSerializer.SIZEOF_TYPEINFO + 6*RbSerializer.SIZEOF_INT + RbSerializer.SIZEOF_LONG;
            }

            public void serialize(RbSerializer serializer, IByteBuffer buffer, object objec)
            {
                var obj = (MouseDraggedEvent) objec;

                serializer.serialize(buffer, obj.getButton());
                serializer.serialize(buffer, obj.getClickCount());
                serializer.serialize(buffer, obj.getX());
                serializer.serialize(buffer, obj.getY());
                serializer.serialize(buffer, obj.getID());
                serializer.serialize(buffer, obj.getModifiersEx());
                serializer.serialize(buffer, obj.getWhen());
            }

            public object deserialize(RbSerializer serializer, IByteBuffer buffer)
            {
                int button = buffer.getInt();
                int clickCount = buffer.getInt();
                int x = buffer.getInt();
                int y = buffer.getInt();
                int id = buffer.getInt();
                int modifiersEx = buffer.getInt();
                long when = buffer.getLong();

                return new MouseEnteredEvent(button, clickCount, x, y, id, modifiersEx, when);
            }
        }
    }
}

//happy
