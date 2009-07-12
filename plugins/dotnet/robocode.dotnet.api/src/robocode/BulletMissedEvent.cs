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
    /// This evnt is sent to {@link Robot#onBulletMissed(BulletMissedEvent)
    /// onBulletMissed} when one of your bullets has missed, i.e. when the bullet has
    /// reached the border of the battlefield.
    ///
    /// @author Mathew A. Nelson (original)
    /// </summary>
    public sealed class BulletMissedEvent : Event
    {
        private const int DEFAULT_PRIORITY = 60;

        private Bullet bullet;

        /// <summary>
        /// Called by the game to create a new {@code BulletMissedEvent}.
        ///
        /// @param bullet the bullet that missed
        /// </summary>
        public BulletMissedEvent(Bullet bullet)
        {
            this.bullet = bullet;
        }

        /// <summary>
        /// Returns the bullet that missed.
        ///
        /// @return the bullet that missed
        /// </summary>
        public Bullet getBullet()
        {
            return bullet;
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
                listener.onBulletMissed(this);
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
            return RbSerializer.BulletMissedEvent_TYPE;
        }

        private static IISerializableHelper createHiddenSerializer()
        {
            return new ISerializableHelper();
        }

        private class ISerializableHelper : IISerializableHelper
        {
            public int sizeOf(RbSerializer serializer, object objec)
            {
                return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT;
            }

            public void serialize(RbSerializer serializer, IByteBuffer buffer, object objec)
            {
                var obj = (BulletMissedEvent) objec;

                serializer.serialize(buffer, obj.bullet.getBulletId());
            }

            public object deserialize(RbSerializer serializer, IByteBuffer buffer)
            {
                var bullet = new Bullet(0, 0, 0, 0, null, null, false, buffer.getInt());

                return new BulletMissedEvent(bullet);
            }
        }
    }
}
//happy