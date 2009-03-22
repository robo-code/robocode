using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.awt;
using java.lang;
using java.nio;
using java.util;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class BulletMissedEvent : Event
    {
        private const int DEFAULT_PRIORITY = 60;
        private const long serialVersionUID = 1L;
        private Bullet bullet;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xbc, 0x68, 0x67})]
        public BulletMissedEvent(Bullet bullet)
        {
            this.bullet = bullet;
        }

        [LineNumberTable((ushort) 0x23), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static Bullet access100(BulletMissedEvent event1)
        {
            return event1.bullet;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x61)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x16, 0x87, 0x66, 0x87})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics, Graphics2D graphics2D)
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

        [MethodImpl(MethodImplOptions.NoInlining),
         Signature("(Ljava/util/Hashtable<Ljava/lang/Integer;Lrobocode/Bullet;>;)V"),
         LineNumberTable(new byte[] {0x23, 0x7f, 2})]
        internal override sealed void updateBullets(Hashtable hashtable1)
        {
            bullet = (Bullet) hashtable1.get(Integer.valueOf(bullet.getBulletId()));
        }

        #region Nested type: a1

        [SourceFile("BulletMissedEvent.java"), EnclosingMethod("robocode.BulletMissedEvent", null, null),
         Modifiers(Modifiers.Synthetic | Modifiers.Synchronized),
         InnerClass(null, Modifiers.Synthetic | Modifiers.Static)]
        internal sealed class a1 : Object
        {
            /* private scope */

            private a1()
            {
                throw null;
            }
        }

        #endregion

        #region Nested type: SerializableHelper

        [Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"}), SourceFile("BulletMissedEvent.java"),
         InnerClass(null, Modifiers.Static | Modifiers.Private)]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 100)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), Modifiers(Modifiers.Synthetic), LineNumberTable((ushort) 100)]
            internal SerializableHelper(BulletMissedEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x3e, 0x9f, 4})]
            public object deserialize(RbSerializer serializer, ByteBuffer buffer1)
            {
                return new BulletMissedEvent(new Bullet(0f, 0f, 0f, 0f, null, null, false, buffer1.getInt()));
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x38, 0x87, 0x72})]
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