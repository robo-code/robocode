using System.Runtime.CompilerServices;
using ikvm.@internal;
using IKVM.Attributes;
using java.awt;
using java.lang;
using java.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class BattleEndedEvent : Event
    {
        private const int DEFAULT_PRIORITY = 100;
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private bool aborted;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private BattleResults results;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0x81, 0x43, 0x68, 0x67, 0x67})]
        public BattleEndedEvent(bool aborted, BattleResults results)
        {
            this.aborted = aborted;
            this.results = results;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x27)]
        internal static BattleResults access100(BattleEndedEvent event1)
        {
            return event1.results;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x27)]
        internal static bool access200(BattleEndedEvent event1)
        {
            return event1.aborted;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x7a)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x2e, 0x66, 0x87, 0x7b, 0xac})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics, Graphics2D graphics2D)
        {
            if (robot1 != null)
            {
                IBasicEvents @this = robot1.getBasicEventListener();
                if ((@this != null) &&
                    ClassLiteral<IBasicEvents2>.Value.isAssignableFrom(instancehelper_getClass(@this)))
                {
                    ((IBasicEvents2) @this).onBattleEnded(this);
                }
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 100;
        }

        public override sealed int getPriority()
        {
            return 100;
        }

        public BattleResults getResults()
        {
            return results;
        }

        internal override byte getSerializationType()
        {
            return 0x20;
        }

        public bool isAborted()
        {
            return aborted;
        }

        internal override sealed bool isCriticalEvent()
        {
            return true;
        }

        #region Nested type: a1

        [Modifiers(Modifiers.Synthetic | Modifiers.Synchronized),
         InnerClass(null, Modifiers.Synthetic | Modifiers.Static), SourceFile("BattleEndedEvent.java"),
         EnclosingMethod("robocode.BattleEndedEvent", null, null)]
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

        [InnerClass(null, Modifiers.Static | Modifiers.Private),
         Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"}), SourceFile("BattleEndedEvent.java")]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x7d)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x7d), Modifiers(Modifiers.Synthetic)]
            internal SerializableHelper(BattleEndedEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x5b, 0x68, 0x8d})]
            public object deserialize(RbSerializer serializer1, ByteBuffer buffer1)
            {
                return new BattleEndedEvent(serializer1.deserializeBoolean(buffer1),
                                            (BattleResults) serializer1.deserializeAny(buffer1));
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x54, 0x87, 0x6d, 110})]
            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (BattleEndedEvent) obj1;
                serializer1.serialize(buffer1, access200(event2));
                serializer1.serialize(buffer1, 8, access100(event2));
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x4d, 0x87})]
            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var event2 = (BattleEndedEvent) obj1;
                return (2 + serializer1.sizeOf(8, access100(event2)));
            }

            #endregion
        }

        #endregion
    }
}