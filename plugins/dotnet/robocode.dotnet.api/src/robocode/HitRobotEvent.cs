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
    /// A HitRobotEvent is sent to {@link Robot#onHitRobot(HitRobotEvent) onHitRobot()}
    /// when your robot collides with another robot.
    /// You can use the information contained in this evnt to determine what to do.
    ///
    /// @author Mathew A. Nelson (original)
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
        ///
        /// @param name	the name of the robot you hit
        /// @param bearing the bearing to the robot that your robot hit, in radians
        /// @param energy  the amount of energy of the robot you hit
        /// @param atFault {@code true} if your robot was moving toward the other
        ///                robot; {@code false} otherwise
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
        /// heading, in degrees (-180 <= getBearing() < 180)
        ///
        /// @return the bearing to the robot you hit, in degrees
        /// </summary>
        public double getBearing()
        {
            return bearing*180.0/Math.PI;
        }

        /// <summary>
        /// Returns the bearing to the robot you hit, relative to your robot's
        /// heading, in radians (-PI <= getBearingRadians() < PI)
        ///
        /// @return the bearing to the robot you hit, in radians
        /// </summary>
        public double getBearingRadians()
        {
            return bearing;
        }

        /// <summary>
        /// Returns the amount of energy of the robot you hit.
        ///
        /// @return the amount of energy of the robot you hit
        /// </summary>
        public double getEnergy()
        {
            return energy;
        }

        /// <summary>
        /// Returns the name of the robot you hit.
        ///
        /// @return the name of the robot you hit
        /// </summary>
        public string getName()
        {
            return name;
        }

        /// <summary>
        /// Checks if your robot was moving towards the robot that was hit.
        /// <p/>
        /// If isMyFault() returns {@code true} then your robot's movement (including
        /// turning) will have stopped and been marked complete.
        /// <p/>
        /// Note: If two robots are moving toward each other and collide, they will
        /// each receive two HitRobotEvents. The first will be the one if isMyFault()
        /// returns {@code true}.
        ///
        /// @return {@code true} if your robot was moving towards the robot that was
        ///         hit; {@code false} otherwise.
        /// </summary>
        public bool isMyFault()
        {
            return atFault;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public override int CompareTo(Event evnt)
        {
            int res = base.CompareTo(evnt);

            if (res != 0)
            {
                return res;
            }

            // Compare the isMyFault, if the events are HitRobotEvents
            // The isMyFault has higher priority when it is set compared to when it is not set
            if (evnt is HitRobotEvent)
            {
                int compare1 = (this).isMyFault() ? -1 : 0;
                int compare2 = ((HitRobotEvent) evnt).isMyFault() ? -1 : 0;

                return compare1 - compare2;
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
                listener.onHitRobot(this);
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override byte getSerializationType()
        {
            return RbSerializerN.HitRobotEvent_TYPE;
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
//happy