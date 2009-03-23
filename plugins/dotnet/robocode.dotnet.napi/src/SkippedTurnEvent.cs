using System.Runtime.CompilerServices;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class SkippedTurnEvent : Event
    {
        private const int DEFAULT_PRIORITY = 100;
        private const long serialVersionUID = 1L;

        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        internal override void dispatch(IBasicRobot robot1, IRobotStatics statics1, IGraphics graphics2D)
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


        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(SkippedTurnEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

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