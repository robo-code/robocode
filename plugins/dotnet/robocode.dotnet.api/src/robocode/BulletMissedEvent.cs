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
    ///  This event is sent to <see cref="Robot.OnBulletMissed(BulletMissedEvent)"/>
    ///  when one of your bullets has missed, i.e. when the bullet has
    ///  reached the border of the battlefield.
    ///</summary>
    [Serializable]
    public sealed class BulletMissedEvent : Event
    {
        private const int DEFAULT_PRIORITY = 60;

        private Bullet bullet;

        /// 
        ///<summary>
        ///  Called by the game to create a new BulletMissedEvent.
        ///</summary>
        public BulletMissedEvent(Bullet bullet)
        {
            this.bullet = bullet;
        }

        /// 
        ///<summary>
        ///  Returns the bullet that missed.
        ///</summary>
        public Bullet Bullet
        {
            get { return bullet; }
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
                listener.OnBulletMissed(this);
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
            get { return RbSerializerN.BulletMissedEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                return RbSerializerN.SIZEOF_TYPEINFO + RbSerializerN.SIZEOF_INT;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
                var obj = (BulletMissedEvent) objec;

                serializer.serialize(buffer, obj.bullet.getBulletId());
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                var bullet = new Bullet(0, 0, 0, 0, null, null, false, buffer.getInt());

                return new BulletMissedEvent(bullet);
            }
        }
    }
}
//doc
