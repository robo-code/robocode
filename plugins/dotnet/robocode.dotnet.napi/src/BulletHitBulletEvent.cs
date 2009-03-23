using System.Collections;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class BulletHitBulletEvent : Event
    {
        private const int DEFAULT_PRIORITY = 0x37;
        private const long serialVersionUID = 1L;
        private Bullet bullet;
        private Bullet hitBullet;

        public BulletHitBulletEvent(Bullet bullet, Bullet hitBullet)
        {
            this.bullet = bullet;
            this.hitBullet = hitBullet;
        }

        internal static Bullet access100(BulletHitBulletEvent event1)
        {
            return event1.hitBullet;
        }

        internal static Bullet access200(BulletHitBulletEvent event1)
        {
            return event1.bullet;
        }

        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        internal override void dispatch(IBasicRobot robot1, IRobotStatics statics, IGraphics graphics2D)
        {
            IBasicEvents events = robot1.getBasicEventListener();
            if (events != null)
            {
                events.onBulletHitBullet(this);
            }
        }

        public Bullet getBullet()
        {
            return bullet;
        }

        internal override int getDefaultPriority()
        {
            return 0x37;
        }

        public Bullet getHitBullet()
        {
            return hitBullet;
        }

        internal override byte getSerializationType()
        {
            return 0x21;
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

            internal SerializableHelper(BulletHitBulletEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer1, ByteBuffer buffer1)
            {
                var bullet = new Bullet(0f, 0f, 0f, 0f, null, null, false, buffer1.getInt());
                return new BulletHitBulletEvent(bullet, (Bullet) serializer1.deserializeAny(buffer1));
            }

            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (BulletHitBulletEvent) obj1;
                serializer1.serialize(buffer1, access200(event2).getBulletId());
                serializer1.serialize(buffer1, 9, access100(event2));
            }

            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var event2 = (BulletHitBulletEvent) obj1;
                return (5 + serializer1.sizeOf(9, access100(event2)));
            }

            #endregion
        }

        #endregion
    }
}