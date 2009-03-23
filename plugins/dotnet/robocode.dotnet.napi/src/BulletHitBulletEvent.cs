using System.Collections.Generic;
using System.IO;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class BulletHitBulletEvent : Event
    {
        private const int DEFAULT_PRIORITY = 0x37;
        private const long serialVersionUID = 1L;
        private readonly Bullet hitBullet;
        private Bullet bullet;

        public BulletHitBulletEvent(Bullet bullet, Bullet hitBullet)
        {
            this.bullet = bullet;
            this.hitBullet = hitBullet;
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

            public object deserialize(RbSerializer serializer, BinaryReader br)
            {
                var bullet = new Bullet(0f, 0f, 0f, 0f, null, null, false, br.ReadInt32());
                return new BulletHitBulletEvent(bullet, (Bullet) serializer.deserializeAny(br));
            }

            public void serialize(RbSerializer serializer, BinaryWriter bw, object obj)
            {
                var res = (BulletHitBulletEvent) obj;
                serializer.serialize(bw, res.bullet.getBulletId());
                serializer.serialize(bw, 9, res.hitBullet);
            }

            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var event2 = (BulletHitBulletEvent) obj1;
                return (5 + serializer1.sizeOf(9, event2.hitBullet));
            }

            #endregion
        }

        #endregion
    }
}