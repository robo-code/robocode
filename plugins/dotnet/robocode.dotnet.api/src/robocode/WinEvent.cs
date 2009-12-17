/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Javadocs
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
    /// This evnt is sent to {@link Robot#onWin(WinEvent) onWin()} when your robot
    /// wins the round in a battle.
    ///
    /// @author Mathew A. Nelson (original)
    /// </summary>
    [Serializable]
    public sealed class WinEvent : Event
    {
        private const int DEFAULT_PRIORITY = 100; // System evnt -> cannot be changed!;

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public override int getPriority()
        {
            return DEFAULT_PRIORITY;
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
            IBasicEvents listener = robot.getBasicEventListener();

            if (listener != null)
            {
                listener.onWin(this);
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override bool isCriticalEvent()
        {
            return true;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override byte getSerializationType()
        {
            return RbnSerializer.WinEvent_TYPE;
        }

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelper
        {
            public int sizeOf(RbnSerializer serializer, object objec)
            {
                return RbnSerializer.SIZEOF_TYPEINFO;
            }

            public void serialize(RbnSerializer serializer, ByteBuffer buffer, object objec)
            {
            }

            public object deserialize(RbnSerializer serializer, ByteBuffer buffer)
            {
                return new WinEvent();
            }
        }
    }
}
//happy