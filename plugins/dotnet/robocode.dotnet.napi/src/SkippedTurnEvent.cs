using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.awt;
using java.lang;
using java.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class SkippedTurnEvent : Event
    {
        private const int DEFAULT_PRIORITY = 100;
        private const long serialVersionUID = 1L;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x76)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x2a, 0x6b, 140, 0x66, 0xa7})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics1, Graphics2D graphics2D)
        {
            if (statics1.isAdvancedRobot())
            {
                IAdvancedEvents events = ((IAdvancedRobot) robot1).getAdvancedEventListener();
                if (events != null)
                {
                    events.onSkippedTurn(this);
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

        internal override byte getSerializationType()
        {
            return 40;
        }

        internal override sealed bool isCriticalEvent()
        {
            return true;
        }

        #region Nested type: a1

        [InnerClass(null, Modifiers.Synthetic | Modifiers.Static),
         EnclosingMethod("robocode.SkippedTurnEvent", null, null),
         Modifiers(Modifiers.Synthetic | Modifiers.Synchronized), SourceFile("SkippedTurnEvent.java")]
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

        [SourceFile("SkippedTurnEvent.java"), InnerClass(null, Modifiers.Static | Modifiers.Private),
         Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"})]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x79)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), Modifiers(Modifiers.Synthetic), LineNumberTable((ushort) 0x79)]
            internal SerializableHelper(SkippedTurnEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x81)]
            public object deserialize(RbSerializer serializer, ByteBuffer buffer)
            {
                return new SkippedTurnEvent();
            }

            public void serialize(RbSerializer serializer, ByteBuffer buffer, object objec)
            {
            }

            public int sizeOf(RbSerializer serializer, object on)
            {
                return 1;
            }

            #endregion
        }

        #endregion
    }
}