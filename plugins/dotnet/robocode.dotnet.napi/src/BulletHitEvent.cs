using System;
using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.awt;
using java.lang;
using java.nio;
using java.util;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;
using Object=java.lang.Object;

namespace robocode
{
    public sealed class BulletHitEvent : Event
    {
        private const int DEFAULT_PRIORITY = 50;
        private const long serialVersionUID = 1L;
        private Bullet bullet;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double energy;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string name;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0, 0x68, 0x67, 0x69, 0x67})]
        public BulletHitEvent(string name, double energy, Bullet bullet)
        {
            this.name = name;
            this.energy = energy;
            this.bullet = bullet;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static string access100(BulletHitEvent event1)
        {
            return event1.name;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static Bullet access200(BulletHitEvent event1)
        {
            return event1.bullet;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access300(BulletHitEvent event1)
        {
            return event1.energy;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x95)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x4a, 0x87, 0x66, 0x87})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics, Graphics2D graphics2D)
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

        [Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}), Obsolete]
        public double getLife()
        {
            return energy;
        }

        public string getName()
        {
            return name;
        }

        [Obsolete, Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"})]
        public double getRobotLife()
        {
            return energy;
        }

        [Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}), Obsolete]
        public string getRobotName()
        {
            return name;
        }

        internal override byte getSerializationType()
        {
            return 0x22;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x57, 0x7f, 2}),
         Signature("(Ljava/util/Hashtable<Ljava/lang/Integer;Lrobocode/Bullet;>;)V")]
        internal override sealed void updateBullets(Hashtable hashtable1)
        {
            bullet = (Bullet) hashtable1.get(Integer.valueOf(bullet.getBulletId()));
        }

        #region Nested type: a1

        [SourceFile("BulletHitEvent.java"), Modifiers(Modifiers.Synthetic | Modifiers.Synchronized),
         EnclosingMethod("robocode.BulletHitEvent", null, null),
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

        [SourceFile("BulletHitEvent.java"), InnerClass(null, Modifiers.Static | Modifiers.Private),
         Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"})]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x98)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x98), Modifiers(Modifiers.Synthetic)]
            internal SerializableHelper(BulletHitEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x77, 0x7f, 4, 0x68, 0x88})]
            public object deserialize(RbSerializer serializer1, ByteBuffer buffer1)
            {
                var bullet = new Bullet(0f, 0f, 0f, 0f, null, null, false, buffer1.getInt());
                string name = serializer1.deserializeString(buffer1);
                return new BulletHitEvent(name, buffer1.getDouble(), bullet);
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x6f, 0x87, 0x72, 0x6d, 0x6d})]
            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (BulletHitEvent) obj1;
                serializer1.serialize(buffer1, access200(event2).getBulletId());
                serializer1.serialize(buffer1, access100(event2));
                serializer1.serialize(buffer1, access300(event2));
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x68, 0x87})]
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