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
    /// A ScannedRobotEvent is sent to <see cref="Robot.OnScannedRobot(ScannedRobotEvent)"/>
    /// OnScannedRobot()} when you scan a robot.
    /// You can use the information contained in this event to determine what to do.
    /// <p/>
    /// <b>Note</b>: You should not inherit from this class in your own event class!
    /// The internal logic of this event class might change. Hence, your robot might
    /// not work in future Robocode versions, if you choose to inherit from this class.
    /// </summary>
    public class ScannedRobotEvent : Event
    {
        private const int DEFAULT_PRIORITY = 10;

        private readonly string name;
        private readonly double energy;
        private readonly double heading;
        private readonly double bearing;
        private readonly double distance;
        private readonly double velocity;
        private readonly bool isSentryRobot;

        [Obsolete("ScannedRobotEvent() is obsolete.\n" +
            "Please use ScannedRobotEvent(string, double, double, double, double, double, bool) instead.")]
        internal ScannedRobotEvent()
            : this(null, 0, 0, 0, 0, 0, false)
        {
        }

        /// <summary>
        /// Called by the game to create a new ScannedRobotEvent.
        /// </summary>
        /// <param name="name">The name of the scanned robot</param>
        /// <param name="energy">The energy of the scanned robot</param>
        /// <param name="bearing">The bearing of the scanned robot, in radians</param>
        /// <param name="distance">The distance from your robot to the scanned robot</param>
        /// <param name="heading">The heading of the scanned robot</param>
        /// <param name="velocity">The velocity of the scanned robot</param>
        [Obsolete("ScannedRobotEvent(string, double, double, double, double, double) is obsolete.\n" +
            "Please use ScannedRobotEvent(string, double, double, double, double, double, bool) instead.")]
        public ScannedRobotEvent(string name, double energy, double bearing, double distance, double heading, double velocity) :
            this(name, energy, bearing, distance, heading, velocity, false)
        {
        }

        /// <summary>
        /// Called by the game to create a new ScannedRobotEvent.
        /// </summary>
        /// <param name="name">The name of the scanned robot</param>
        /// <param name="energy">The energy of the scanned robot</param>
        /// <param name="bearing">The bearing of the scanned robot, in radians</param>
        /// <param name="distance">The distance from your robot to the scanned robot</param>
        /// <param name="heading">The heading of the scanned robot</param>
        /// <param name="velocity">The velocity of the scanned robot</param>
        /// <param name="isSentryRobot">Flag specifying if the scanned robot is a sentry robot</param>
        public ScannedRobotEvent(string name, double energy, double bearing, double distance, double heading, double velocity, bool isSentryRobot)
        {
            this.name = name;
            this.energy = energy;
            this.bearing = bearing;
            this.distance = distance;
            this.heading = heading;
            this.velocity = velocity;
            this.isSentryRobot = isSentryRobot;
        }

        /// <summary>
        /// Returns the bearing to the robot you scanned, relative to your robot's
        /// heading, in degrees (-180 &lt;= getBearing() &lt; 180)
        /// </summary>
        public double Bearing
        {
            get { return bearing*180.0/Math.PI; }
        }

        /// <summary>
        /// Returns the bearing to the robot you scanned, relative to your robot's
        /// heading, in radians (-PI &lt;= getBearingRadians() &lt; PI)
        /// </summary>
        public double BearingRadians
        {
            get { return bearing; }
        }

        /// <summary>
        /// Returns the distance to the robot (your center to his center).
        /// </summary>
        public double Distance
        {
            get { return distance; }
        }

        /// <summary>
        /// Returns the energy of the robot.
        /// </summary>
        public double Energy
        {
            get { return energy; }
        }

        /// <summary>
        /// Returns the heading of the robot, in degrees (0 &lt;= getHeading() &lt; 360)
        /// </summary>
        public double Heading
        {
            get { return heading*180.0/Math.PI; }
        }

        /// <summary>
        /// Returns the heading of the robot, in radians (0 &lt;= getHeading() &lt; 2 * PI)
        /// </summary>
        public double HeadingRadians
        {
            get { return heading; }
        }


        /// <summary>
        /// Returns the name of the robot.
        /// </summary>
        public string Name
        {
            get { return name; }
        }


        /// <summary>
        /// Returns the velocity of the robot.
        /// </summary>
        public double Velocity
        {
            get { return velocity; }
        }

        /// <summary>
        /// <code>true</code> if the scanned robot is a sentry robot; <code>false</code> otherwise.
        /// </summary>
        public bool IsSentryRobot
        {
            get { return isSentryRobot; }
        }

        /// <inheritdoc />
        public override sealed int CompareTo(Event evnt)
        {
            int res = base.CompareTo(evnt);

            if (res != 0)
            {
                return res;
            }
            // Compare the distance, if the events are ScannedRobotEvents
            // The shorter distance to the robot, the higher priority
            if (evnt is ScannedRobotEvent)
            {
                return (int) (Distance - ((ScannedRobotEvent) evnt).Distance);
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
                listener.OnScannedRobot(this);
            }
        }

        internal override byte SerializationType
        {
            get { return RbSerializerN.ScannedRobotEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                var obj = (ScannedRobotEvent) objec;

                return RbSerializerN.SIZEOF_TYPEINFO + serializer.sizeOf(obj.name) + 5 * RbSerializerN.SIZEOF_DOUBLE + RbSerializerN.SIZEOF_BOOL;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
                var obj = (ScannedRobotEvent) objec;

                serializer.serialize(buffer, obj.name);
                serializer.serialize(buffer, obj.energy);
                serializer.serialize(buffer, obj.heading);
                serializer.serialize(buffer, obj.bearing);
                serializer.serialize(buffer, obj.distance);
                serializer.serialize(buffer, obj.velocity);
                serializer.serialize(buffer, obj.isSentryRobot);
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                string name = serializer.deserializeString(buffer);
                double energy = buffer.getDouble();
                double heading = buffer.getDouble();
                double bearing = buffer.getDouble();
                double distance = buffer.getDouble();
                double velocity = buffer.getDouble();
                bool isSentryRobot = serializer.deserializeBoolean(buffer);

                return new ScannedRobotEvent(name, energy, bearing, distance, heading, velocity, isSentryRobot);
            }
        }
    }
}
//doc