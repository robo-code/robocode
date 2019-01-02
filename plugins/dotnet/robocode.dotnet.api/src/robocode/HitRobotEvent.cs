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
    /// A HitRobotEvent is sent to <see cref="Robot.OnHitRobot(HitRobotEvent)"/>
    /// when your robot collides with another robot.
    /// You can use the information contained in this event to determine what to do.
    /// </summary>
    [Serializable]
    public sealed class HitRobotEvent : Event
    {
        private const int DEFAULT_PRIORITY = 40;

        private readonly string name;
        private readonly double bearing;
        private readonly double energy;
        private readonly bool atFault;

        /// <summary>
        /// Called by the game to create a new HitRobotEvent.
        /// </summary>
        public HitRobotEvent(string name, double bearing, double energy, bool atFault)
        {
            this.name = name;
            this.bearing = bearing;
            this.energy = energy;
            this.atFault = atFault;
        }

        /// <summary>
        /// Returns the bearing to the robot you hit, relative to your robot's
        /// heading, in degrees (-180 &lt;= getBearing() &lt; 180)
        /// </summary>
        public double Bearing
        {
            get { return bearing*180.0/Math.PI; }
        }

        /// <summary>
        /// Returns the bearing to the robot you hit, relative to your robot's
        /// heading, in radians (-PI &lt;= getBearingRadians() &lt; PI)
        /// </summary>
        public double BearingRadians
        {
            get { return bearing; }
        }

        /// <summary>
        /// Returns the amount of energy of the robot you hit.
        /// </summary>
        public double Energy
        {
            get { return energy; }
        }

        /// <summary>
        /// Returns the name of the robot you hit.
        /// </summary>
        public string Name
        {
            get { return name; }
        }

        /// <summary>
        /// Checks if your robot was moving towards the robot that was hit.
        /// <p/>
        /// If <see cref="IsMyFault"/> returns true then your robot's movement (including
        /// turning) will have stopped and been marked complete.
        /// <p/>
        /// Note: If two robots are moving toward each other and collide, they will
        /// each receive two HitRobotEvents. The first will be the one if isMyFault()
        /// returns true.
        /// </summary>
        public bool IsMyFault
        {
            get { return atFault; }
        }

        /// <inheritdoc />
        public override int CompareTo(Event evnt)
        {
            int res = base.CompareTo(evnt);

            if (res != 0)
            {
                return res;
            }

            // Compare the IsMyFault, if the events are HitRobotEvents
            // The isMyFault has higher priority when it is set compared to when it is not set
            if (evnt is HitRobotEvent)
            {
                int compare1 = (this).IsMyFault ? -1 : 0;
                int compare2 = ((HitRobotEvent) evnt).IsMyFault ? -1 : 0;

                return compare1 - compare2;
            }

            // No difference found
            return 0;
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
                listener.OnHitRobot(this);
            }
        }

        internal override byte SerializationType
        {
            get { return RbSerializerN.HitRobotEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                var obj = (HitRobotEvent) objec;

                return RbSerializerN.SIZEOF_TYPEINFO + serializer.sizeOf(obj.name) + 2*RbSerializerN.SIZEOF_DOUBLE
                       + RbSerializerN.SIZEOF_BOOL;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
                var obj = (HitRobotEvent) objec;

                serializer.serialize(buffer, obj.name);
                serializer.serialize(buffer, obj.bearing);
                serializer.serialize(buffer, obj.energy);
                serializer.serialize(buffer, obj.atFault);
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                string robotName = serializer.deserializeString(buffer);
                double bearing = buffer.getDouble();
                double energy = buffer.getDouble();
                bool atFault = serializer.deserializeBoolean(buffer);

                return new HitRobotEvent(robotName, bearing, energy, atFault);
            }
        }
    }
}
//doc