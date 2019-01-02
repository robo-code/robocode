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
    /// A HitByBulletEvent is sent to <see cref="Robot.OnHitByBullet(HitByBulletEvent)"/>
    /// when your robot has been hit by a bullet.
    /// You can use the information contained in this event to determine what to do.
    /// </summary>
    [Serializable]
    public sealed class HitByBulletEvent : Event
    {
        private const int DEFAULT_PRIORITY = 20;

        private readonly double bearing;
        private readonly Bullet bullet;

        /// <summary>
        /// Called by the game to create a new HitByBulletEvent.
        /// </summary>
        public HitByBulletEvent(double bearing, Bullet bullet)
        {
            this.bearing = bearing;
            this.bullet = bullet;
        }

        /// <summary>
        /// Returns the bearing to the bullet, relative to your robot's heading,
        /// in degrees (-180 &lt; getBearing() &lt;= 180).
        /// <p/>
        /// If you were to TurnRight(event.Bearing), you would be facing the
        /// direction the bullet came from. The calculation used here is:
        /// (bullet's heading in degrees + 180) - (your heading in degrees)
        /// </summary>
        public double Bearing
        {
            get { return bearing*180.0/Math.PI; }
        }

        /// <summary>
        /// Returns the bearing to the bullet, relative to your robot's heading,
        /// in radians (-Math.PI &lt; getBearingRadians() &lt;= Math.PI).
        /// <p/>
        /// If you were to TurnRightRadians(event.BearingRadians), you would be
        /// facing the direction the bullet came from. The calculation used here is:
        /// (bullet's heading in radians + Math.PI) - (your heading in radians)
        /// </summary>
        public double BearingRadians
        {
            get { return bearing; }
        }

        /// <summary>
        /// Returns the bullet that hit your robot.
        /// </summary>
        public Bullet Bullet
        {
            get { return bullet; }
        }

        /// <summary>
        /// Returns the heading of the bullet when it hit you, in degrees
        /// (0 &lt;= getHeading() &lt; 360).
        /// <p/>
        /// Note: This is not relative to the direction you are facing. The robot
        /// that fired the bullet was in the opposite direction of getHeading() when
        /// it fired the bullet.
        /// </summary>
        public double Heading
        {
            get { return bullet.Heading; }
        }

        /// <summary>
        /// Returns the heading of the bullet when it hit you, in radians
        /// (0 &lt;= getHeadingRadians() &lt; 2 * PI).
        /// <p/>
        /// Note: This is not relative to the direction you are facing. The robot
        /// that fired the bullet was in the opposite direction of
        /// getHeadingRadians() when it fired the bullet.
        /// </summary>
        public double HeadingRadians
        {
            get { return bullet.HeadingRadians; }
        }

        /// <summary>
        /// Returns the name of the robot that fired the bullet.
        /// </summary>
        public string Name
        {
            get { return bullet.Name; }
        }

        /// <summary>
        /// Returns the power of this bullet. The damage you take (in fact, already
        /// took) is 4 * power, plus 2 * (power-1) if power > 1. The robot that fired
        /// the bullet receives 3 * power back.
        /// </summary>
        public double Power
        {
            get { return bullet.Power; }
        }

        /// <summary>
        /// Returns the velocity of this bullet.
        /// </summary>
        public double Velocity
        {
            get { return bullet.Velocity; }
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
                listener.OnHitByBullet(this);
            }
        }

        internal override byte SerializationType
        {
            get { return RbSerializerN.HitByBulletEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                var obj = (HitByBulletEvent) objec;

                return RbSerializerN.SIZEOF_TYPEINFO + serializer.sizeOf(RbSerializerN.Bullet_TYPE, obj.bullet)
                       + RbSerializerN.SIZEOF_DOUBLE;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
                var obj = (HitByBulletEvent) objec;

                serializer.serialize(buffer, RbSerializerN.Bullet_TYPE, obj.bullet);
                serializer.serialize(buffer, obj.bearing);
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                var bullet = (Bullet) serializer.deserializeAny(buffer);
                double bearing = buffer.getDouble();

                return new HitByBulletEvent(bearing, bullet);
            }
        }
    }
}
//doc