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
using java.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    /// <summary>
    /// A HitByBulletEvent is sent to {@link Robot#onHitByBullet(HitByBulletEvent)
    /// onHitByBullet()} when your robot has been hit by a bullet.
    /// You can use the information contained in this evnt to determine what to do.
    ///
    /// @author Mathew A. Nelson (original)
    /// </summary>
    public sealed class HitByBulletEvent : Event
    {
        private const int DEFAULT_PRIORITY = 20;

        private readonly double bearing;
        private readonly Bullet bullet;

        /// <summary>
        /// Called by the game to create a new HitByBulletEvent.
        ///
        /// @param bearing the bearing of the bullet that hit your robot, in radians
        /// @param bullet  the bullet that has hit your robot
        /// </summary>
        public HitByBulletEvent(double bearing, Bullet bullet)
        {
            this.bearing = bearing;
            this.bullet = bullet;
        }

        /// <summary>
        /// Returns the bearing to the bullet, relative to your robot's heading,
        /// in degrees (-180 < getBearing() <= 180)
        /// <p/>
        /// If you were to turnRight(e.getBearing()), you would be facing the
        /// direction the bullet came from. The calculation used here is:
        /// (bullet's heading in degrees + 180) - (your heading in degrees)
        ///
        /// @return the bearing to the bullet, in degrees
        /// </summary>
        public double getBearing()
        {
            return bearing*180.0/Math.PI;
        }

        /// <summary>
        /// Returns the bearing to the bullet, relative to your robot's heading,
        /// in radians (-Math.PI < getBearingRadians() <= Math.PI)
        /// <p/>
        /// If you were to turnRightRadians(e.getBearingRadians()), you would be
        /// facing the direction the bullet came from. The calculation used here is:
        /// (bullet's heading in radians + Math.PI) - (your heading in radians)
        ///
        /// @return the bearing to the bullet, in radians
        /// </summary>
        public double getBearingRadians()
        {
            return bearing;
        }

        /// <summary>
        /// Returns the bullet that hit your robot.
        ///
        /// @return the bullet that hit your robot
        /// </summary>
        public Bullet getBullet()
        {
            return bullet;
        }

        /// <summary>
        /// Returns the heading of the bullet when it hit you, in degrees
        /// (0 <= getHeading() < 360)
        /// <p/>
        /// Note: This is not relative to the direction you are facing. The robot
        /// that fired the bullet was in the opposite direction of getHeading() when
        /// it fired the bullet.
        ///
        /// @return the heading of the bullet, in degrees
        /// </summary>
        public double getHeading()
        {
            return bullet.getHeading();
        }

        /// <summary>
        /// Returns the heading of the bullet when it hit you, in radians
        /// (0 <= getHeadingRadians() < 2 * PI)
        /// <p/>
        /// Note: This is not relative to the direction you are facing. The robot
        /// that fired the bullet was in the opposite direction of
        /// getHeadingRadians() when it fired the bullet.
        ///
        /// @return the heading of the bullet, in radians
        /// </summary>
        public double getHeadingRadians()
        {
            return bullet.getHeadingRadians();
        }

        /// <summary>
        /// Returns the name of the robot that fired the bullet.
        ///
        /// @return the name of the robot that fired the bullet
        /// </summary>
        public string getName()
        {
            return bullet.getName();
        }

        /// <summary>
        /// Returns the power of this bullet. The damage you take (in fact, already
        /// took) is 4 * power, plus 2 * (power-1) if power > 1. The robot that fired
        /// the bullet receives 3 * power back.
        ///
        /// @return the power of the bullet
        /// </summary>
        public double getPower()
        {
            return bullet.getPower();
        }

        /// <summary>
        /// Returns the velocity of this bullet.
        ///
        /// @return the velocity of the bullet
        /// </summary>
        public double getVelocity()
        {
            return bullet.getVelocity();
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
        internal override void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics graphics)
        {
            IBasicEvents listener = robot.getBasicEventListener();

            if (listener != null)
            {
                listener.onHitByBullet(this);
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override byte getSerializationType()
        {
            return RbSerializer.HitByBulletEvent_TYPE;
        }

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelper
        {
            public int sizeOf(RbSerializer serializer, object objec)
            {
                var obj = (HitByBulletEvent) objec;

                return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(RbSerializer.Bullet_TYPE, obj.bullet)
                       + RbSerializer.SIZEOF_DOUBLE;
            }

            public void serialize(RbSerializer serializer, IByteBuffer buffer, object objec)
            {
                var obj = (HitByBulletEvent) objec;

                serializer.serialize(buffer, RbSerializer.Bullet_TYPE, obj.bullet);
                serializer.serialize(buffer, obj.bearing);
            }

            public object deserialize(RbSerializer serializer, IByteBuffer buffer)
            {
                var bullet = (Bullet) serializer.deserializeAny(buffer);
                double bearing = buffer.getDouble();

                return new HitByBulletEvent(bearing, bullet);
            }
        }
    }
}
//happy