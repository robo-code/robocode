using System.IO;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class MouseEnteredEvent : MouseEvent
    {
        private const int DEFAULT_PRIORITY = 0x62;
        private const long serialVersionUID = 1L;

        public MouseEnteredEvent(int id, long when, int modifiers, int x, int y, int clickCount, int button)
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
                    events.onMouseEntered(this);
                }
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 0x62;
        }

        internal override byte getSerializationType()
        {
            return 0x31;
        }

        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(MouseEnteredEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer, BinaryReader br)
            {
                int button = br.ReadInt32();
                int clickCount = br.ReadInt32();
                int x = br.ReadInt32();
                int y = br.ReadInt32();
                int id = br.ReadInt32();
                int modifiers = br.ReadInt32();
                long when = br.ReadInt64();
                return
                    new MouseEnteredEvent(id, when, modifiers, x, y, clickCount, button);
            }

            public void serialize(RbSerializer serializer, BinaryWriter bw, object obj)
            {
                MouseEvent event3 = ((MouseEnteredEvent) obj);
                serializer.serialize(bw, event3.Button);
                serializer.serialize(bw, event3.ClickCount);
                serializer.serialize(bw, event3.X);
                serializer.serialize(bw, event3.Y);
                serializer.serialize(bw, event3.Id);
                serializer.serialize(bw, event3.Modifiers);
                serializer.serialize(bw, event3.When);
            }

            public int sizeOf(RbSerializer serializer, object objectm)
            {
                return 0x21;
            }

            #endregion
        }

        #endregion
    }
}