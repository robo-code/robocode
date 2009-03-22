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
    public sealed class WinEvent : Event
    {
        private const int DEFAULT_PRIORITY = 100;
        private const long serialVersionUID = 1L;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x59)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {15, 0x87, 0x66, 0x87})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics st, Graphics2D graphics2D)
        {
            IBasicEvents events = robot1.getBasicEventListener();
            if (events != null)
            {
                events.onWin(this);
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
            return 0x25;
        }

        internal override sealed bool isCriticalEvent()
        {
            return true;
        }

        #region Nested type: a1

        [InnerClass(null, Modifiers.Synthetic | Modifiers.Static),
         Modifiers(Modifiers.Synthetic | Modifiers.Synchronized), EnclosingMethod("robocode.WinEvent", null, null),
         SourceFile("WinEvent.java")]
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

        [Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"}), SourceFile("WinEvent.java"),
         InnerClass(null, Modifiers.Static | Modifiers.Private)]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x5c)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), Modifiers(Modifiers.Synthetic), LineNumberTable((ushort) 0x5c)]
            internal SerializableHelper(WinEvent a1)
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 100)]
            public object deserialize(RbSerializer serializer, ByteBuffer buffer)
            {
                return new WinEvent();
            }

            public void serialize(RbSerializer serializer, ByteBuffer buffer, object @object)
            {
            }

            public int sizeOf(RbSerializer serializer, object @object)
            {
                return 1;
            }

            #endregion
        }

        #endregion
    }
}