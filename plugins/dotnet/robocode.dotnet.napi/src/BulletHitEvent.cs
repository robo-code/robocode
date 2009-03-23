using System;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class BulletHitEvent : Event
    {
        private const int DEFAULT_PRIORITY = 50;
        private const long serialVersionUID = 1L;
        private Bullet bullet;
       private double energy;
       private string name;

        public BulletHitEvent(string name, double energy, Bullet bullet)
        {
            this.name = name;
            this.energy = energy;
            this.bullet = bullet;
        }

        internal static string access100(BulletHitEvent event1)
        {
            return event1.name;
        }

        internal static Bullet access200(BulletHitEvent event1)
        {
            return event1.bullet;
        }

        internal static double access300(BulletHitEvent event1)
        {
            return event1.energy;
        }

        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics, IGraphics graphics2D)
        {
            IBasicEvents events = robot1.getBasicEventListener();
            if (events != null)
            {
                events.onBulletHit(this);
            }
        }

        public Bullet getBullet()
        {
            return bullet;
        }

        internal override sealed int getDefaultPriority()
        {
            return 50;
        }

        public double getEnergy()
        {
            return energy;
        }

        public string getName()
        {
            return name;
        }

        public string getRobotName()
        {
            return name;
        }

        internal override byte getSerializationType()
        {
            return 0x22;
        }

        internal override void updateBullets(Dictionary<int, Bullet> hashtable1)
        {
            bullet = hashtable1[bullet.getBulletId()];
        }

    
        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(BulletHitEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer1, ByteBuffer buffer1)
            {
                var bullet = new Bullet(0f, 0f, 0f, 0f, null, null, false, buffer1.getInt());
                string name = serializer1.deserializeString(buffer1);
                return new BulletHitEvent(name, buffer1.getDouble(), bullet);
            }

            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (BulletHitEvent) obj1;
                serializer1.serialize(buffer1, access200(event2).getBulletId());
                serializer1.serialize(buffer1, access100(event2));
                serializer1.serialize(buffer1, access300(event2));
            }

            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var event2 = (BulletHitEvent) obj1;
                return ((5 + serializer1.sizeOf(access100(event2))) + 8);
            }

            #endregion
        }

        #endregion
    }
}