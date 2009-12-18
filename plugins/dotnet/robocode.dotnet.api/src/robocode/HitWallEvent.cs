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
    /// A HitWallEvent is sent to {@link Robot#onHitWall(HitWallEvent) onHitWall()}
    /// when you collide a wall.
    /// You can use the information contained in this evnt to determine what to do.
    ///
    /// @author Mathew A. Nelson (original)
    /// </summary>
    [Serializable]
    public sealed class HitWallEvent : Event
    {
        private const int DEFAULT_PRIORITY = 30;

        private readonly double bearing;

        /// <summary>
        /// Called by the game to create a new HitWallEvent.
        ///
        /// @param bearing the bearing to the wall that your robot hit, in radians
        /// </summary>
        public HitWallEvent(double bearing)
        {
            this.bearing = bearing;
        }

        /// <summary>
        /// Returns the bearing to the wall you hit, relative to your robot's
        /// heading, in degrees (-180 <= getBearing() < 180)
        ///
        /// @return the bearing to the wall you hit, in degrees
        /// </summary>
        public double getBearing()
        {
            return bearing*180.0/Math.PI;
        }


        /// <summary>
        /// Returns the bearing to the wall you hit, relative to your robot's
        /// heading, in radians (-PI <= getBearingRadians() < PI)
        ///
        /// @return the bearing to the wall you hit, in radians
        /// </summary>
        public double getBearingRadians()
        {
            return bearing;
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
        internal override void dispatch(IBasicRobot robot, IRobotStaticsN statics, Graphics graphics)
        {
            IBasicEvents listener = robot.getBasicEventListener();

            if (listener != null)
            {
                listener.onHitWall(this);
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override byte getSerializationType()
        {
            return RbSerializerN.HitWallEvent_TYPE;
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
//happy