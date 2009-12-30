#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;
using System.Collections.Generic;
using net.sf.robocode.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    ///<summary>
    ///  This evnt is sent to {@link Robot#OnBulletMissed(BulletMissedEvent)
    ///  OnBulletMissed} when one of your bullets has missed, i.e. when the bullet has
    ///  reached the border of the battlefield.
    ///
    ///  @author Mathew A. Nelson (original)
    ///</summary>
    [Serializable]
    public sealed class BulletMissedEvent : Event
    {
        private const int DEFAULT_PRIORITY = 60;

        private Bullet bullet;

        /// 
        ///<summary>
        ///  Called by the game to create a new {@code BulletMissedEvent}.
        ///
        ///  @param bullet the bullet that missed
        ///</summary>
        public BulletMissedEvent(Bullet bullet)
        {
            this.bullet = bullet;
        }

        /// 
        ///<summary>
        ///  Returns the bullet that missed.
        ///
        ///  @return the bullet that missed
        ///</summary>
        public Bullet Bullet
        {
            get { return bullet; }
        }

        /// <summary>{@inheritDoc}</summary>
        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <summary>{@inheritDoc}</summary>
        internal override void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            IBasicEvents listener = robot.GetBasicEventListener();

            if (listener != null)
            {
                listener.OnBulletMissed(this);
            }
        }

        /// <summary>{@inheritDoc}</summary>
        internal override void UpdateBullets(Dictionary<int, Bullet> bullets)
        {
            // we need to pass same instance
            bullet = bullets[bullet.getBulletId()];
        }

        /// <summary>{@inheritDoc}</summary>
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

//happy