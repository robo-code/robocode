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
    /// A HitWallEvent is sent to <see cref="Robot.OnHitWall(HitWallEvent)"/>
    /// when you collide a wall.
    /// You can use the information contained in this event to determine what to do.
    /// </summary>
    [Serializable]
    public sealed class HitWallEvent : Event
    {
        private const int DEFAULT_PRIORITY = 30;

        private readonly double bearing;

        /// <summary>
        /// Called by the game to create a new HitWallEvent.
        /// </summary>
        public HitWallEvent(double bearing)
        {
            this.bearing = bearing;
        }

        /// <summary>
        /// Returns the bearing to the wall you hit, relative to your robot's
        /// heading, in degrees (-180 &lt;= getBearing() &lt; 180)
        /// </summary>
        public double Bearing
        {
            get { return bearing*180.0/Math.PI; }
        }


        /// <summary>
        /// Returns the bearing to the wall you hit, relative to your robot's
        /// heading, in radians (-PI &lt;= getBearingRadians() &lt; PI)
        /// </summary>
        public double BearingRadians
        {
            get { return bearing; }
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
                listener.OnHitWall(this);
            }
        }

        internal override byte SerializationType
        {
            get { return RbSerializerN.HitWallEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                return RbSerializerN.SIZEOF_TYPEINFO + RbSerializerN.SIZEOF_DOUBLE;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
                var obj = (HitWallEvent) objec;

                serializer.serialize(buffer, obj.bearing);
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                double bearing = buffer.getDouble();

                return new HitWallEvent(bearing);
            }
        }
    }
}
//doc