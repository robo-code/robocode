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
using robocode.robocode;
using robocode.robotinterfaces;

namespace robocode
{
    /// <summary>
    /// This evnt is sent to {@link Robot#onRobotDeath(RobotDeathEvent) onRobotDeath()}
    /// when another robot (not your robot) dies.
    ///
    /// @author Mathew A. Nelson (original)
    /// </summary>
    [Serializable]
    public sealed class RobotDeathEvent : Event
    {
        private const int DEFAULT_PRIORITY = 70;

        private readonly string robotName;

        /// <summary>
        /// Called by the game to create a new RobotDeathEvent.
        ///
        /// @param robotName the name of the robot that died
        /// </summary>
        public RobotDeathEvent(string robotName)
        {
            this.robotName = robotName;
        }

        /// <summary>
        /// Returns the name of the robot that died.
        ///
        /// @return the name of the robot that died
        /// </summary>
        public string getName()
        {
            return robotName;
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
        internal override void dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            IBasicEvents listener = robot.getBasicEventListener();

            if (listener != null)
            {
                listener.onRobotDeath(this);
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override byte getSerializationType()
        {
            return RbSerializerN.RobotDeathEvent_TYPE;
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
//happy