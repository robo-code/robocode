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
    public sealed class RobotDeathEvent : Event
    {
        private const int DEFAULT_PRIORITY = 70;
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string robotName;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xbb, 0x68, 0x67})]
        public RobotDeathEvent(string robotName)
        {
            this.robotName = robotName;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x21)]
        internal static string access100(RobotDeathEvent event1)
        {
            return event1.robotName;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x60)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {30, 0x87, 0x66, 0x87})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics sta, Graphics2D gra)
        {
            IBasicEvents events = robot1.getBasicEventListener();
            if (events != null)
            {
                events.onRobotDeath(this);
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 70;
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
            return 0x27;
        }

        #region Nested type: a1

        [EnclosingMethod("robocode.RobotDeathEvent", null, null),
         InnerClass(null, Modifiers.Synthetic | Modifiers.Static),
         Modifiers(Modifiers.Synthetic | Modifiers.Synchronized), SourceFile("RobotDeathEvent.java")]
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

        [InnerClass(null, Modifiers.Static | Modifiers.Private), SourceFile("RobotDeathEvent.java"),
         Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"})]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x63)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x63), Modifiers(Modifiers.Synthetic)]
            internal SerializableHelper(RobotDeathEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x3f, 0x88})]
            public object deserialize(RbSerializer serializer1, ByteBuffer buffer1)
            {
                return new RobotDeathEvent(serializer1.deserializeString(buffer1));
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x39, 0x87, 0x6d})]
            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (RobotDeathEvent) obj1;
                serializer1.serialize(buffer1, access100(event2));
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x33, 0x87})]
            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var event2 = (RobotDeathEvent) obj1;
                return (1 + serializer1.sizeOf(access100(event2)));
            }

            #endregion
        }

        #endregion
    }
}