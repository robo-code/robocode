using System.Collections;
using System.Runtime.CompilerServices;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class BulletMissedEvent : Event
    {
        private const int DEFAULT_PRIORITY = 60;
        private const long serialVersionUID = 1L;
        private Bullet bullet;

        public BulletMissedEvent(Bullet bullet)
        {
            this.bullet = bullet;
        }

        internal static Bullet access100(BulletMissedEvent event1)
        {
            return event1.bullet;
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
                events.onBulletMissed(this);
            }
        }

        public Bullet getBullet()
        {
            return bullet;
        }

        internal override sealed int getDefaultPriority()
        {
            return 60;
        }

        internal override byte getSerializationType()
        {
            return 0x23;
        }

        internal void updateBullets(Hashtable hashtable1)
        {
            bullet = (Bullet) hashtable1[bullet.getBulletId()];
        }


        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(BulletMissedEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer, ByteBuffer buffer1)
            {
                return new BulletMissedEvent(new Bullet(0f, 0f, 0f, 0f, null, null, false, buffer1.getInt()));
            }

            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (BulletMissedEvent) obj1;
                serializer1.serialize(buffer1, access100(event2).getBulletId());
            }

            public int sizeOf(RbSerializer serializer, object obj)
            {
                return 5;
            }

            #endregion
        }

        #endregion
    }
}