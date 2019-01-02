/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Drawing;
using net.sf.robocode.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using Robocode.RobotInterfaces;

namespace Robocode
{
    /// <summary>
    /// This event is sent to <see cref="Robot.OnRobotDeath(RobotDeathEvent)"/>
    /// when another robot (not your robot) dies.
    /// </summary>
    [Serializable]
    public sealed class RobotDeathEvent : Event
    {
        private const int DEFAULT_PRIORITY = 70;

        private readonly string robotName;

        /// <summary>
        /// Called by the game to create a new RobotDeathEvent.
        /// </summary>
        /// <param name="robotName">the name of the robot that died</param>
        public RobotDeathEvent(string robotName)
        {
            this.robotName = robotName;
        }

        /// <summary>
        /// Returns the name of the robot that died.
        /// </summary>
        public string Name
        {
            get { return robotName; }
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
                listener.OnRobotDeath(this);
            }
        }

        internal override byte SerializationType
        {
            get { return RbSerializerN.RobotDeathEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                var obj = (RobotDeathEvent) objec;

                return RbSerializerN.SIZEOF_TYPEINFO + serializer.sizeOf(obj.robotName);
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
                var obj = (RobotDeathEvent) objec;

                serializer.serialize(buffer, obj.robotName);
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                string name = serializer.deserializeString(buffer);

                return new RobotDeathEvent(name);
            }
        }
    }
}
//doc