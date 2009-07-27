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
using System.Collections.Generic;
using System.Drawing;
using java.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    /// <summary>
    /// This evnt is sent to {@link Robot#onBulletHit(BulletHitEvent) onBulletHit}
    /// when one of your bullets has hit another robot.
    ///
    /// @author Mathew A. Nelson (original)
    /// </summary>
    public sealed class BulletHitEvent : Event
    {
        private const int DEFAULT_PRIORITY = 50;

        private readonly string name;
        private readonly double energy;
        private Bullet bullet;

        /// <summary>
        /// Called by the game to create a new {@code BulletHitEvent}.
        ///
        /// @param name   the name of the robot your bullet hit
        /// @param energy the remaining energy of the robot that your bullet has hit
        /// @param bullet the bullet that hit the robot
        /// </summary>
        public BulletHitEvent(string name, double energy, Bullet bullet)
        {
            this.name = name;
            this.energy = energy;
            this.bullet = bullet;
        }

        /// <summary>
        /// Returns the bullet of yours that hit the robot.
        ///
        /// @return the bullet that hit the robot
        /// </summary>
        public Bullet getBullet()
        {
            return bullet;
        }

        /// <summary>
        /// Returns the remaining energy of the robot your bullet has hit (after the
        /// damage done by your bullet).
        ///
        /// @return energy the remaining energy of the robot that your bullet has hit
        /// </summary>
        public double getEnergy()
        {
            return energy;
        }

        /// <summary>
        /// Returns the name of the robot your bullet hit.
        ///
        /// @return the name of the robot your bullet hit.
        /// </summary>
        public string getName()
        {
            return name;
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
                listener.onBulletHit(this);
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override void updateBullets(Dictionary<int, Bullet> bullets)
        {
            // we need to pass same instance
            bullet = bullets[bullet.getBulletId()];
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override byte getSerializationType()
        {
            return RbSerializer.BulletHitEvent_TYPE;
        }

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelper
        {
            public int sizeOf(RbSerializer serializer, object objec)
            {
                var obj = (BulletHitEvent) objec;

                return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT + serializer.sizeOf(obj.name)
                       + RbSerializer.SIZEOF_DOUBLE;
            }

            public void serialize(RbSerializer serializer, IByteBuffer buffer, object objec)
            {
                var obj = (BulletHitEvent) objec;

                serializer.serialize(buffer, obj.bullet.getBulletId());
                serializer.serialize(buffer, obj.name);
                serializer.serialize(buffer, obj.energy);
            }

            public object deserialize(RbSerializer serializer, IByteBuffer buffer)
            {
                var bullet = new Bullet(0, 0, 0, 0, null, null, false, buffer.getInt());
                string name = serializer.deserializeString(buffer);
                double energy = buffer.getDouble();

                return new BulletHitEvent(name, energy, bullet);
            }
        }
    }
}
//happy