using System.Runtime.CompilerServices;
using net.sf.robocode.peer;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class MouseWheelMovedEvent : MouseEvent
    {
        private const int DEFAULT_PRIORITY = 0x62;

        private readonly int scrollType;
        private readonly int scrollAmount;
        private readonly int wheelRotation;

        public MouseWheelMovedEvent(int id, long when, int modifiers, int x, int y, int clickCount, int scrollType, int scrollAmount, int wheelRotation)
            : base(id, when, modifiers, x, y, clickCount, 0)
        {
            this.scrollType = scrollType;
            this.scrollAmount = scrollAmount;
            this.wheelRotation = wheelRotation;
        }

        public int ScrollType
        {
            get { return scrollType; }
        }

        public int ScrollAmount
        {
            get { return scrollAmount; }
        }

        public int WheelRotation
        {
            get { return wheelRotation; }
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
                    events.onMouseWheelMoved(this);
                }
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 0x62;
        }

        internal override byte getSerializationType()
        {
            return 0x36;
        }


        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(MouseWheelMovedEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer, ByteBuffer buffer1)
            {
                int clickCount = buffer1.getInt();
                int x = buffer1.getInt();
                int y = buffer1.getInt();
                int scrollType = buffer1.getInt();
                int scrollAmount = buffer1.getInt();
                int wheelRotation = buffer1.getInt();
                int id = buffer1.getInt();
                int modifiers = buffer1.getInt();
                long when = buffer1.getLong();
                return new MouseWheelMovedEvent(id, when,modifiers, x, y, clickCount, scrollType, scrollAmount, wheelRotation);
            }

            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                MouseWheelMovedEvent event3 = (MouseWheelMovedEvent) obj1;
                serializer1.serialize(buffer1, event3.ClickCount);
                serializer1.serialize(buffer1, event3.X);
                serializer1.serialize(buffer1, event3.Y);
                serializer1.serialize(buffer1, event3.ScrollType);
                serializer1.serialize(buffer1, event3.ScrollAmount);
                serializer1.serialize(buffer1, event3.WheelRotation);
                serializer1.serialize(buffer1, event3.Id);
                serializer1.serialize(buffer1, event3.Modifiers);
                serializer1.serialize(buffer1, event3.When);
            }

            public int sizeOf(RbSerializer serializer, object obj)
            {
                return 0x29;
            }

            #endregion
        }

        #endregion
    }
}