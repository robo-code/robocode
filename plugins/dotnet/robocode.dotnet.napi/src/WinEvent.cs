using System.Drawing;
using System.Runtime.CompilerServices;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class WinEvent : Event
    {
        private const int DEFAULT_PRIORITY = 100;
        private const long serialVersionUID = 1L;

        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        internal override void dispatch(IBasicRobot robot1, IRobotStatics st, IGraphics graphics2D)
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


        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(WinEvent a1)
            {
            }

            #region ISerializableHelper Members

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