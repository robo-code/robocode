using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using IKVM.Attributes;
using IKVM.Runtime;
using java.awt;
using java.lang;
using java.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;
using Object=java.lang.Object;

namespace robocode
{
    public sealed class ScannedRobotEvent : Event
    {
        private const int DEFAULT_PRIORITY = 10;
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double bearing;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double distance;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double energy;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double heading;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string name;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double velocity;

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {6, 0x68, 0x67, 0x69, 0x69, 0x6a, 0x6a, 0x6a})]
        public ScannedRobotEvent(string name, double energy, double bearing, double distance, double heading,
                                 double velocity)
        {
            this.name = name;
            this.energy = energy;
            this.bearing = bearing;
            this.distance = distance;
            this.heading = heading;
            this.velocity = velocity;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static string access100(ScannedRobotEvent event1)
        {
            return event1.name;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access200(ScannedRobotEvent event1)
        {
            return event1.energy;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access300(ScannedRobotEvent event1)
        {
            return event1.heading;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x22)]
        internal static double access400(ScannedRobotEvent event1)
        {
            return event1.bearing;
        }

        [LineNumberTable((ushort) 0x22), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static double access500(ScannedRobotEvent event1)
        {
            return event1.distance;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x22)]
        internal static double access600(ScannedRobotEvent event1)
        {
            return event1.velocity;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x81, 0x88, 0x66, 0xc2, 0x6b, 0xb8})
        ]
        public override sealed int compareTo(Event @event)
        {
            int num = base.compareTo(@event);
            if (num != 0)
            {
                return num;
            }
            if (@event is ScannedRobotEvent)
            {
                return ByteCodeHelper.d2i(getDistance() - ((ScannedRobotEvent) @event).getDistance());
            }
            return 0;
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         Modifiers(Modifiers.Synthetic | Modifiers.Public | Modifiers.Volatile),
         EditorBrowsable(EditorBrowsableState.Never), LineNumberTable((ushort) 0x22)]
        public override int compareTo(object x0)
        {
            return compareTo((Event) x0);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x11e)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x9c, 0x87, 0x66, 0x87})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics robotStatics, Graphics2D graphics2D)
        {
            IBasicEvents events = robot1.getBasicEventListener();
            if (events != null)
            {
                events.onScannedRobot(this);
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

        internal override sealed int getDefaultPriority()
        {
            return 10;
        }

        public double getDistance()
        {
            return distance;
        }

        public double getEnergy()
        {
            return energy;
        }

        public double getHeading()
        {
            return ((heading*180.0)/3.1415926535897931);
        }

        public double getHeadingRadians()
        {
            return heading;
        }

        public string getName()
        {
            return name;
        }

        [MethodImpl(MethodImplOptions.NoInlining), Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}),
         LineNumberTable((ushort) 0x91), Obsolete]
        public double getRobotBearing()
        {
            return getBearing();
        }

        [MethodImpl(MethodImplOptions.NoInlining), Obsolete, LineNumberTable((ushort) 0x9a),
         Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"})]
        public double getRobotBearingDegrees()
        {
            return getBearing();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xa3), Obsolete,
         Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"})]
        public double getRobotBearingRadians()
        {
            return getBearingRadians();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xac), Obsolete,
         Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"})]
        public double getRobotDistance()
        {
            return getDistance();
        }

        [MethodImpl(MethodImplOptions.NoInlining), Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}),
         LineNumberTable((ushort) 0xb5), Obsolete]
        public double getRobotHeading()
        {
            return getHeading();
        }

        [MethodImpl(MethodImplOptions.NoInlining), Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}),
         LineNumberTable((ushort) 190), Obsolete]
        public double getRobotHeadingDegrees()
        {
            return getHeading();
        }

        [MethodImpl(MethodImplOptions.NoInlining), Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}), Obsolete,
         LineNumberTable((ushort) 0xc7)]
        public double getRobotHeadingRadians()
        {
            return getHeadingRadians();
        }

        [MethodImpl(MethodImplOptions.NoInlining), Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}), Obsolete,
         LineNumberTable((ushort) 0xd0)]
        public double getRobotLife()
        {
            return getEnergy();
        }

        [MethodImpl(MethodImplOptions.NoInlining), Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}), Obsolete,
         LineNumberTable((ushort) 0xd9)]
        public string getRobotName()
        {
            return getName();
        }

        [MethodImpl(MethodImplOptions.NoInlining), Deprecated(new object[] {0x40, "Ljava/lang/Deprecated;"}),
         LineNumberTable((ushort) 0xe2), Obsolete]
        public double getRobotVelocity()
        {
            return getVelocity();
        }

        internal override byte getSerializationType()
        {
            return 0x29;
        }

        public double getVelocity()
        {
            return velocity;
        }

        #region Nested type: a1

        [InnerClass(null, Modifiers.Synthetic | Modifiers.Static),
         EnclosingMethod("robocode.ScannedRobotEvent", null, null),
         Modifiers(Modifiers.Synthetic | Modifiers.Synchronized), SourceFile("ScannedRobotEvent.java")]
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

        [SourceFile("ScannedRobotEvent.java"), Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"}),
         InnerClass(null, Modifiers.Static | Modifiers.Private)]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x121)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x121), Modifiers(Modifiers.Synthetic)]
            internal SerializableHelper(ScannedRobotEvent a1)
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining),
             LineNumberTable(new byte[] {160, 0xc2, 0x68, 0x68, 0x68, 0x68, 0x69, 0x89})]
            public object deserialize(RbSerializer serializer1, ByteBuffer buffer1)
            {
                string name = serializer1.deserializeString(buffer1);
                double energy = buffer1.getDouble();
                double heading = buffer1.getDouble();
                double bearing = buffer1.getDouble();
                double distance = buffer1.getDouble();
                return new ScannedRobotEvent(name, energy, bearing, distance, heading, buffer1.getDouble());
            }

            [MethodImpl(MethodImplOptions.NoInlining),
             LineNumberTable(new byte[] {160, 0xb7, 0x87, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d})]
            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (ScannedRobotEvent) obj1;
                serializer1.serialize(buffer1, access100(event2));
                serializer1.serialize(buffer1, access200(event2));
                serializer1.serialize(buffer1, access300(event2));
                serializer1.serialize(buffer1, access400(event2));
                serializer1.serialize(buffer1, access500(event2));
                serializer1.serialize(buffer1, access600(event2));
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0xb1, 0x87})]
            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var event2 = (ScannedRobotEvent) obj1;
                return ((1 + serializer1.sizeOf(access100(event2))) + 40);
            }

            #endregion
        }

        #endregion
    }
}