using System;
using System.ComponentModel;
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
    public sealed class HitRobotEvent : Event
    {
        private const int DEFAULT_PRIORITY = 40;
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private bool atFault;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double bearing;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double energy;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string robotName;

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0x9f, 0x81, 0x43, 0x68, 0x67, 0x69, 0x69, 0x67})]
        public HitRobotEvent(string name, double bearing, double energy, bool atFault)
        {
            robotName = name;
            this.bearing = bearing;
            this.energy = energy;
            this.atFault = atFault;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x22)]
        internal static string access100(HitRobotEvent event1)
        {
            return event1.robotName;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access200(HitRobotEvent event1)
        {
            return event1.bearing;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access300(HitRobotEvent event1)
        {
            return event1.energy;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static bool access400(HitRobotEvent event1)
        {
            return event1.atFault;
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0x57, 0x88, 0x66, 0xe2, 0x45, 0x6b, 0x73, 0x98, 0xc4})]
        public override sealed int compareTo(Event @event)
        {
            int num = base.compareTo(@event);
            if (num != 0)
            {
                return num;
            }
            if (@event is HitRobotEvent)
            {
                int num2 = !isMyFault() ? 0 : -1;
                int num3 = !((HitRobotEvent) @event).isMyFault() ? 0 : -1;
                return (num2 - num3);
            }
            return 0;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x22),
         Modifiers(Modifiers.Synthetic | Modifiers.Public | Modifiers.Volatile),
         EditorBrowsable(EditorBrowsableState.Never)]
        public override int compareTo(object x0)
        {
            return compareTo((Event) x0);
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
                events.onHitRobot(this);
            }
        }

        public double getBearing()
        {
            return ((bearing*180.0)/3.1415926535897931);
        }

        [MethodImpl(MethodImplOptions.NoInlining), Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}), Obsolete,
         LineNumberTable((ushort) 0x4b)]
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
            return 40;
        }

        public double getEnergy()
        {
            return energy;
        }

        public string getName()
        {
            return robotName;
        }

        [Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}), Obsolete]
        public string getRobotName()
        {
            return robotName;
        }

        internal override byte getSerializationType()
        {
            return 0x2b;
        }

        public bool isMyFault()
        {
            return atFault;
        }

        #region Nested type: a1

        [InnerClass(null, Modifiers.Synthetic | Modifiers.Static), EnclosingMethod("robocode.HitRobotEvent", null, null)
        , SourceFile("HitRobotEvent.java"), Modifiers(Modifiers.Synthetic | Modifiers.Synchronized)]
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

        [InnerClass(null, Modifiers.Static | Modifiers.Private), SourceFile("HitRobotEvent.java"),
         Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"})]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xbc)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), Modifiers(Modifiers.Synthetic), LineNumberTable((ushort) 0xbc)]
            internal SerializableHelper(HitRobotEvent a1)
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x5c, 0x68, 0x68, 0x68, 0x88})]
            public object deserialize(RbSerializer serializer1, ByteBuffer buffer1)
            {
                string name = serializer1.deserializeString(buffer1);
                double bearing = buffer1.getDouble();
                double energy = buffer1.getDouble();
                return new HitRobotEvent(name, bearing, energy, serializer1.deserializeBoolean(buffer1));
            }

            [MethodImpl(MethodImplOptions.NoInlining),
             LineNumberTable(new byte[] {160, 0x53, 0x87, 0x6d, 0x6d, 0x6d, 0x6d})]
            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (HitRobotEvent) obj1;
                serializer1.serialize(buffer1, access100(event2));
                serializer1.serialize(buffer1, access200(event2));
                serializer1.serialize(buffer1, access300(event2));
                serializer1.serialize(buffer1, access400(event2));
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x4c, 0x87})]
            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var event2 = (HitRobotEvent) obj1;
                return (((1 + serializer1.sizeOf(access100(event2))) + 0x10) + 1);
            }

            #endregion
        }

        #endregion
    }
}