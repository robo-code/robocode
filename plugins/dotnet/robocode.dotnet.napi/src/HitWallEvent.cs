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
    public sealed class HitWallEvent : Event
    {
        private const int DEFAULT_PRIORITY = 30;
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double bearing;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xbb, 0x68, 0x69})]
        public HitWallEvent(double bearing)
        {
            this.bearing = bearing;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x22)]
        internal static double access100(HitWallEvent event1)
        {
            return event1.bearing;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x6b)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x29, 0x87, 0x66, 0x87})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics, Graphics2D graphics2D)
        {
            IBasicEvents events = robot1.getBasicEventListener();
            if (events != null)
            {
                events.onHitWall(this);
            }
        }

        public double getBearing()
        {
            return ((bearing*180.0)/3.1415926535897931);
        }

        [MethodImpl(MethodImplOptions.NoInlining), Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}), Obsolete,
         LineNumberTable((ushort) 0x41)]
        public double getBearingDegrees()
        {
            return getBearing();
        }

        public double getBearingRadians()
        {
            return bearing;
        }

        internal override sealed int getDefaultPriority()
        {
            return 30;
        }

        internal override byte getSerializationType()
        {
            return 0x26;
        }

        #region Nested type: a1

        [EnclosingMethod("robocode.HitWallEvent", null, null), InnerClass(null, Modifiers.Synthetic | Modifiers.Static),
         Modifiers(Modifiers.Synthetic | Modifiers.Synchronized), SourceFile("HitWallEvent.java")]
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

        [Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"}),
         InnerClass(null, Modifiers.Static | Modifiers.Private), SourceFile("HitWallEvent.java")]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 110)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), Modifiers(Modifiers.Synthetic), LineNumberTable((ushort) 110)]
            internal SerializableHelper(HitWallEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x48, 0x88})]
            public object deserialize(RbSerializer serializer, ByteBuffer buffer1)
            {
                return new HitWallEvent(buffer1.getDouble());
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x42, 0x87, 0x6d})]
            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (HitWallEvent) obj1;
                serializer1.serialize(buffer1, access100(event2));
            }

            public int sizeOf(RbSerializer serializer, object obj)
            {
                return 9;
            }

            #endregion
        }

        #endregion
    }
}