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
    /// This event is sent to <see cref="Robot.OnDeath(DeathEvent)"/> when your robot dies.
    /// </summary>
    [Serializable]
    public sealed class DeathEvent : Event
    {
        private const int DEFAULT_PRIORITY = -1; // System event -> cannot be changed!;

        /// <inheritdoc />
        public override int Priority
        {
            get { return DEFAULT_PRIORITY; }
        }

        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        internal override void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            IBasicEvents listener = robot.GetBasicEventListener();

            if (listener != null)
            {
                listener.OnDeath(this);
            }
        }

        internal override bool IsCriticalEvent
        {
            get { return true; }
        }

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
//doc