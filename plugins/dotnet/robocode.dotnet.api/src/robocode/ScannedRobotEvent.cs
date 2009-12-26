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
    /// A ScannedRobotEvent is sent to {@link Robot#onScannedRobot(ScannedRobotEvent)
    /// onScannedRobot()} when you scan a robot.
    /// You can use the information contained in this evnt to determine what to do.
    /// <p>
    /// <b>Note</b>: You should not inherit from this class in your own evnt class!
    /// The internal logic of this evnt class might change. Hence, your robot might
    /// not work in future Robocode versions, if you choose to inherit from this class.
    ///
    /// @author Mathew A. Nelson (original)
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

        /// <summary>
        /// This constructor is only provided in order to preserve backwards compatibility with old robots that
        /// inherits from this Event class. 
        /// <p>
        /// <b>Note</b>: You should not inherit from this class in your own evnt class!
        /// The internal logic of this evnt class might change. Hence, your robot might
        /// not work in future Robocode versions, if you choose to inherit from this class.
        ///
        /// @deprecated Use {@link #ScannedRobotEvent(string, double, double, double, double, double)} instead.
        /// </summary>
        public ScannedRobotEvent()
        {
            name = null;
            energy = 0;
            bearing = 0;
            distance = 0;
            heading = 0;
            velocity = 0;
        }

        /// <summary>
        /// Called by the game to create a new ScannedRobotEvent.
        ///
        /// @param name	 the name of the scanned robot
        /// @param energy   the energy of the scanned robot
        /// @param bearing  the bearing of the scanned robot, in radians
        /// @param distance the distance from your robot to the scanned robot
        /// @param heading  the heading of the scanned robot
        /// @param velocity the velocity of the scanned robot
        /// </summary>
        public ScannedRobotEvent(string name, double energy, double bearing, double distance, double heading,
                                 double velocity)
        {
            this.name = name;
            this.energy = energy;
            this.bearing = bearing;
            this.distance = distance;
            this.heading = heading;
            this.velocity = velocity;
        }

        /// <summary>
        /// Returns the bearing to the robot you scanned, relative to your robot's
        /// heading, in degrees (-180 <= getBearing() < 180)
        ///
        /// @return the bearing to the robot you scanned, in degrees
        /// </summary>
        public double getBearing()
        {
            return bearing*180.0/Math.PI;
        }

        /// <summary>
        /// Returns the bearing to the robot you scanned, relative to your robot's
        /// heading, in radians (-PI <= getBearingRadians() < PI)
        ///
        /// @return the bearing to the robot you scanned, in radians
        /// </summary>
        public double getBearingRadians()
        {
            return bearing;
        }

        /// <summary>
        /// Returns the distance to the robot (your center to his center).
        ///
        /// @return the distance to the robot.
        /// </summary>
        public double getDistance()
        {
            return distance;
        }

        /// <summary>
        /// Returns the energy of the robot.
        ///
        /// @return the energy of the robot
        /// </summary>
        public double getEnergy()
        {
            return energy;
        }

        /// <summary>
        /// Returns the heading of the robot, in degrees (0 <= getHeading() < 360)
        ///
        /// @return the heading of the robot, in degrees
        /// </summary>
        public double getHeading()
        {
            return heading*180.0/Math.PI;
        }

        /// <summary>
        /// Returns the heading of the robot, in radians (0 <= getHeading() < 2 * PI)
        ///
        /// @return the heading of the robot, in radians
        /// </summary>
        public double getHeadingRadians()
        {
            return heading;
        }


        /// <summary>
        /// Returns the name of the robot.
        ///
        /// @return the name of the robot
        /// </summary>
        public string getName()
        {
            return name;
        }


        /// <summary>
        /// Returns the velocity of the robot.
        ///
        /// @return the velocity of the robot
        /// </summary>
        public double getVelocity()
        {
            return velocity;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
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
                return (int) (getDistance() - ((ScannedRobotEvent) evnt).getDistance());
            }
            // No difference found
            return 0;
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
                listener.onScannedRobot(this);
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override byte getSerializationType()
        {
            return RbSerializerN.ScannedRobotEvent_TYPE;
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

                return RbSerializerN.SIZEOF_TYPEINFO + serializer.sizeOf(obj.name) + 5*RbSerializerN.SIZEOF_DOUBLE;
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
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                string name = serializer.deserializeString(buffer);
                double energy = buffer.getDouble();
                double heading = buffer.getDouble();
                double bearing = buffer.getDouble();
                double distance = buffer.getDouble();
                double velocity = buffer.getDouble();

                return new ScannedRobotEvent(name, energy, bearing, distance, heading, velocity);
            }
        }
    }
}
//happy