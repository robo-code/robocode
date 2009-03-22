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
    public sealed class BulletHitBulletEvent : Event
    {
        private const int DEFAULT_PRIORITY = 0x37;
        private const long serialVersionUID = 1L;
        private Bullet bullet;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private Bullet hitBullet;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 190, 0x68, 0x67, 0x67})]
        public BulletHitBulletEvent(Bullet bullet, Bullet hitBullet)
        {
            this.bullet = bullet;
            this.hitBullet = hitBullet;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static Bullet access100(BulletHitBulletEvent event1)
        {
            return event1.hitBullet;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static Bullet access200(BulletHitBulletEvent event1)
        {
            return event1.bullet;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x6d)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x22, 0x87, 0x66, 0x87})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics, Graphics2D graphics2D)
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

        internal override sealed int getDefaultPriority()
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x2f, 0x7f, 2}),
         Signature("(Ljava/util/Hashtable<Ljava/lang/Integer;Lrobocode/Bullet;>;)V")]
        internal override sealed void updateBullets(Hashtable hashtable1)
        {
            bullet = (Bullet) hashtable1.get(Integer.valueOf(bullet.getBulletId()));
        }

        #region Nested type: a1

        [InnerClass(null, Modifiers.Synthetic | Modifiers.Static), SourceFile("BulletHitBulletEvent.java"),
         EnclosingMethod("robocode.BulletHitBulletEvent", null, null),
         Modifiers(Modifiers.Synthetic | Modifiers.Synchronized)]
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

        [InnerClass(null, Modifiers.Static | Modifiers.Private), SourceFile("BulletHitBulletEvent.java"),
         Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"})]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x70)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), Modifiers(Modifiers.Synthetic), LineNumberTable((ushort) 0x70)]
            internal SerializableHelper(BulletHitBulletEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x4f, 0x7f, 4, 0x8d})]
            public object deserialize(RbSerializer serializer1, ByteBuffer buffer1)
            {
                var bullet = new Bullet(0f, 0f, 0f, 0f, null, null, false, buffer1.getInt());
                return new BulletHitBulletEvent(bullet, (Bullet) serializer1.deserializeAny(buffer1));
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x47, 0xa7, 0x72, 0x6f})]
            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (BulletHitBulletEvent) obj1;
                serializer1.serialize(buffer1, access200(event2).getBulletId());
                serializer1.serialize(buffer1, 9, access100(event2));
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x40, 0x87})]
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