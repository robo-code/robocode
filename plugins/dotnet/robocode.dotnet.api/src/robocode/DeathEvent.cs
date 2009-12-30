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
    /// This evnt is sent to {@link Robot#OnDeath(DeathEvent) OnDeath()} when your
    /// robot dies.
    ///
    /// @author Mathew A. Nelson (original)
    /// </summary>
    [Serializable]
    public sealed class DeathEvent : Event
    {
        private const int DEFAULT_PRIORITY = -1; // System evnt -> cannot be changed!;

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public override int Priority
        {
            get { return DEFAULT_PRIORITY; }
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
            IBasicEvents listener = robot.GetBasicEventListener();

            if (listener != null)
            {
                listener.OnDeath(this);
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override bool IsCriticalEvent
        {
            get { return true; }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override byte SerializationType
        {
            get { return RbSerializerN.DeathEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                return RbSerializerN.SIZEOF_TYPEINFO;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                return new DeathEvent();
            }
        }
    }
}
//happy