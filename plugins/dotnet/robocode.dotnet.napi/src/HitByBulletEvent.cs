using System;
using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.awt;
using java.lang;
using java.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;
using Object=java.lang.Object;

namespace robocode
{
    public sealed class HitByBulletEvent : Event
    {
        private const int DEFAULT_PRIORITY = 20;
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double bearing;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private Bullet bullet;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 190, 0x68, 0x69, 0x67})]
        public HitByBulletEvent(double bearing, Bullet bullet)
        {
            this.bearing = bearing;
            this.bullet = bullet;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static Bullet access100(HitByBulletEvent event1)
        {
            return event1.bullet;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access200(HitByBulletEvent event1)
        {
            return event1.bearing;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xb9)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x77, 0x87, 0x66, 0x87})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics, Graphics2D graphics2D)
        {
            IBasicEvents events = robot1.getBasicEventListener();
            if (events != null)
            {
                events.onHitByBullet(this);
            }
        }

        public double getBearing()
        {
            return ((bearing*180.0)/3.1415926535897931);
        }

        public double getBearingRadians()
        {
            return bearing;
        }

        public Bullet getBullet()
        {
            return bullet;
        }

        internal override sealed int getDefaultPriority()
        {
            return 20;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x65)]
        public double getHeading()
        {
            return bullet.getHeading();
        }

        [MethodImpl(MethodImplOptions.NoInlining), Obsolete, Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}),
         LineNumberTable((ushort) 110)]
        public double getHeadingDegrees()
        {
            return getHeading();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x7c)]
        public double getHeadingRadians()
        {
            return bullet.getHeadingRadians();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x85)]
        public string getName()
        {
            return bullet.getName();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x90)]
        public double getPower()
        {
            return bullet.getPower();
        }

        internal override byte getSerializationType()
        {
            return 0x2a;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x99)]
        public double getVelocity()
        {
            return bullet.getVelocity();
        }

        #region Nested type: a1

        [InnerClass(null, Modifiers.Synthetic | Modifiers.Static), SourceFile("HitByBulletEvent.java"),
         EnclosingMethod("robocode.HitByBulletEvent", null, null),
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

        [Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"}), SourceFile("HitByBulletEvent.java"),
         InnerClass(null, Modifiers.Static | Modifiers.Private)]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xbc)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xbc), Modifiers(Modifiers.Synthetic)]
            internal SerializableHelper(HitByBulletEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 90, 0x6d, 0x88})]
            public object deserialize(RbSerializer serializer1, ByteBuffer buffer1)
            {
                var bullet = (Bullet) serializer1.deserializeAny(buffer1);
                return new HitByBulletEvent(buffer1.getDouble(), bullet);
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x53, 0x87, 0x6f, 0x6d})]
            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (HitByBulletEvent) obj1;
                serializer1.serialize(buffer1, 9, access100(event2));
                serializer1.serialize(buffer1, access200(event2));
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x4c, 0x87})]
            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var event2 = (HitByBulletEvent) obj1;
                return ((1 + serializer1.sizeOf(9, access100(event2))) + 8);
            }

            #endregion
        }

        #endregion
    }
}