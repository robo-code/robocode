using System.IO;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class MouseWheelMovedEvent : MouseEvent
    {
        private const int DEFAULT_PRIORITY = 0x62;

        private readonly int scrollAmount;
        private readonly int scrollType;
        private readonly int wheelRotation;

        public MouseWheelMovedEvent(int id, long when, int modifiers, int x, int y, int clickCount, int scrollType,
                                    int scrollAmount, int wheelRotation)
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

            public object deserialize(RbSerializer serializer, BinaryReader br)
            {
                int clickCount = br.ReadInt32();
                int x = br.ReadInt32();
                int y = br.ReadInt32();
                int scrollType = br.ReadInt32();
                int scrollAmount = br.ReadInt32();
                int wheelRotation = br.ReadInt32();
                int id = br.ReadInt32();
                int modifiers = br.ReadInt32();
                long when = br.ReadInt64();
                return new MouseWheelMovedEvent(id, when, modifiers, x, y, clickCount, scrollType, scrollAmount,
                                                wheelRotation);
            }

            public void serialize(RbSerializer serializer, BinaryWriter bw, object obj)
            {
                var event3 = (MouseWheelMovedEvent) obj;
                serializer.serialize(bw, event3.ClickCount);
                serializer.serialize(bw, event3.X);
                serializer.serialize(bw, event3.Y);
                serializer.serialize(bw, event3.ScrollType);
                serializer.serialize(bw, event3.ScrollAmount);
                serializer.serialize(bw, event3.WheelRotation);
                serializer.serialize(bw, event3.Id);
                serializer.serialize(bw, event3.Modifiers);
                serializer.serialize(bw, event3.When);
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