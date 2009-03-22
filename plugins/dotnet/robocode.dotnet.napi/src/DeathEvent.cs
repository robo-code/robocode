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
    public sealed class DeathEvent : Event
    {
        private const int DEFAULT_PRIORITY = -1;
        private const long serialVersionUID = 1L;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x59)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {15, 0x87, 0x66, 0x87})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics, Graphics2D graphics2D)
        {
            IBasicEvents events = robot1.getBasicEventListener();
            if (events != null)
            {
                events.onDeath(this);
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return -1;
        }

        public override sealed int getPriority()
        {
            return -1;
        }

        internal override byte getSerializationType()
        {
            return 0x24;
        }

        internal override sealed bool isCriticalEvent()
        {
            return true;
        }

        #region Nested type: a1

        [EnclosingMethod("robocode.DeathEvent", null, null), SourceFile("DeathEvent.java"),
         Modifiers(Modifiers.Synthetic | Modifiers.Synchronized),
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

        [Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"}),
         InnerClass(null, Modifiers.Static | Modifiers.Private), SourceFile("DeathEvent.java")]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x5c)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x5c), Modifiers(Modifiers.Synthetic)]
            internal SerializableHelper(DeathEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 100)]
            public object deserialize(RbSerializer serializer, ByteBuffer buffer)
            {
                return new DeathEvent();
            }

            public void serialize(RbSerializer serializer, ByteBuffer buffer, object o)
            {
            }

            public int sizeOf(RbSerializer serializer, object o)
            {
                return 1;
            }

            #endregion
        }

        #endregion
    }
}