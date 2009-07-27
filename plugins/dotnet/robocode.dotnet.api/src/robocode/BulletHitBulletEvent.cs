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
    /// This evnt is sent to {@link Robot#onBulletHitBullet(BulletHitBulletEvent)
    /// onBulletHitBullet} when one of your bullets has hit another bullet.
    ///
    /// @author Mathew A. Nelson (original)
    /// </summary>
    public sealed class BulletHitBulletEvent : Event
    {
        private const int DEFAULT_PRIORITY = 55;

        private Bullet bullet;
        private readonly Bullet hitBullet;

        /// <summary>
        /// Called by the game to create a new {@code BulletHitEvent}.
        ///
        /// @param bullet	your bullet that hit another bullet
        /// @param hitBullet the bullet that was hit by your bullet
        /// </summary>
        public BulletHitBulletEvent(Bullet bullet, Bullet hitBullet)
        {
            this.bullet = bullet;
            this.hitBullet = hitBullet;
        }

        /// <summary>
        /// Returns your bullet that hit another bullet.
        ///
        /// @return your bullet
        /// </summary>
        public Bullet getBullet()
        {
            return bullet;
        }

        /// <summary>
        /// Returns the bullet that was hit by your bullet.
        ///
        /// @return the bullet that was hit
        /// </summary>
        public Bullet getHitBullet()
        {
            return hitBullet;
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
                listener.onBulletHitBullet(this);
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
            return RbSerializer.BulletHitBulletEvent_TYPE;
        }

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelper
        {
            public int sizeOf(RbSerializer serializer, object objec)
            {
                var obj = (BulletHitBulletEvent) objec;

                return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT
                       + serializer.sizeOf(RbSerializer.Bullet_TYPE, obj.hitBullet);
            }

            public void serialize(RbSerializer serializer, IByteBuffer buffer, object objec)
            {
                var obj = (BulletHitBulletEvent) objec;

                // no need to transmit whole bullet, rest of it is already known to proxy side
                serializer.serialize(buffer, obj.bullet.getBulletId());
                serializer.serialize(buffer, RbSerializer.Bullet_TYPE, obj.hitBullet);
            }

            public object deserialize(RbSerializer serializer, IByteBuffer buffer)
            {
                var bullet = new Bullet(0, 0, 0, 0, null, null, false, buffer.getInt());
                var hitBullet = (Bullet) serializer.deserializeAny(buffer);

                return new BulletHitBulletEvent(bullet, hitBullet);
            }
        }
    }
}
//happy