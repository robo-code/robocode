/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Collections.Generic;
using net.sf.robocode.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using Robocode.RobotInterfaces;

namespace Robocode
{
    ///<summary>
    ///  This event is sent to <see cref="Robot.OnBulletHitBullet(BulletHitBulletEvent)"/>
    ///  when one of your bullets has hit another bullet.
    ///</summary>
    [Serializable]
    public sealed class BulletHitBulletEvent : Event
    {
        private const int DEFAULT_PRIORITY = 55;

        private Bullet bullet;
        private readonly Bullet hitBullet;

        ///<summary>
        ///  Called by the game to create a new BulletHitEvent.
        ///</summary>
        public BulletHitBulletEvent(Bullet bullet, Bullet hitBullet)
        {
            this.bullet = bullet;
            this.hitBullet = hitBullet;
        }

        ///<summary>
        ///  Returns your bullet that hit another bullet.
        ///</summary>
        public Bullet Bullet
        {
            get { return bullet; }
        }

        ///<summary>
        ///  Returns the bullet that was hit by your bullet.
        ///</summary>
        public Bullet HitBullet
        {
            get { return hitBullet; }
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
                listener.OnBulletHitBullet(this);
            }
        }

        // Needed for .NET version
        internal override void UpdateBullets(Dictionary<int, Bullet> bullets)
        {
            // we need to pass same instance
            bullet = bullets[bullet.getBulletId()];
        }

        internal override byte SerializationType
        {
            get { return RbSerializerN.BulletHitBulletEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                var obj = (BulletHitBulletEvent) objec;

                return RbSerializerN.SIZEOF_TYPEINFO + RbSerializerN.SIZEOF_INT
                       + serializer.sizeOf(RbSerializerN.Bullet_TYPE, obj.hitBullet);
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
                var obj = (BulletHitBulletEvent) objec;

                // no need to transmit whole bullet, rest of it is already known to proxy side
                serializer.serialize(buffer, obj.bullet.getBulletId());
                serializer.serialize(buffer, RbSerializerN.Bullet_TYPE, obj.hitBullet);
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                var bullet = new Bullet(0, 0, 0, 0, null, null, false, buffer.getInt());
                var hitBullet = (Bullet) serializer.deserializeAny(buffer);

                return new BulletHitBulletEvent(bullet, hitBullet);
            }
        }
    }
}

//doc
