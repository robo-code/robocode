using System.Runtime.CompilerServices;
using net.sf.robocode.peer;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class MouseExitedEvent : MouseEvent
    {
        private const int DEFAULT_PRIORITY = 0x62;
        private const long serialVersionUID = 1L;

        public MouseExitedEvent(int id, long when, int modifiers, int x, int y, int clickCount, int button)
            : base(id, when, modifiers, x, y, clickCount, button)
        {
        }

        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics1, IGraphics graphics2D)
        {
            if (statics1.isInteractiveRobot())
            {
                IInteractiveEvents events = ((IInteractiveRobot) robot1).getInteractiveEventListener();
                if (events != null)
                {
                    events.onMouseExited(this);
                }
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 0x62;
        }

        internal override byte getSerializationType()
        {
            return 50;
        }

        

        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(MouseExitedEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer, ByteBuffer buffer1)
            {
                int button = buffer1.getInt();
                int clickCount = buffer1.getInt();
                int x = buffer1.getInt();
                int y = buffer1.getInt();
                int id = buffer1.getInt();
                int modifiers = buffer1.getInt();
                long when = buffer1.getLong();
                return
                    new MouseExitedEvent(id, when, modifiers, x, y, clickCount, button);
            }

            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                MouseEvent event3 = ((MouseExitedEvent) obj1);
                serializer1.serialize(buffer1, event3.Button);
                serializer1.serialize(buffer1, event3.ClickCount);
                serializer1.serialize(buffer1, event3.X);
                serializer1.serialize(buffer1, event3.Y);
                serializer1.serialize(buffer1, event3.Id);
                serializer1.serialize(buffer1, event3.Modifiers);
                serializer1.serialize(buffer1, event3.When);
            }

            public int sizeOf(RbSerializer serializer, object obj)
            {
                return 0x21;
            }

            #endregion
        }

        #endregion
    }
}