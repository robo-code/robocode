using System.Runtime.CompilerServices;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class DeathEvent : Event
    {
        private const int DEFAULT_PRIORITY = -1;
        private const long serialVersionUID = 1L;

        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics, IGraphics graphics2D)
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

        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(DeathEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

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