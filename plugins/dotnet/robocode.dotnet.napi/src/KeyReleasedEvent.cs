using System.IO;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class KeyReleasedEvent : KeyEvent
    {
        private const int DEFAULT_PRIORITY = 0x62;
        private const long serialVersionUID = 1L;

        public KeyReleasedEvent(char keyChar, int keyCode, int keyLocation, int id, int modifiers, long when)
            : base(keyChar, keyCode, keyLocation, id, modifiers, when)
        {
        }

        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        internal override void dispatch(IBasicRobot robot1, IRobotStatics statics1, IGraphics kIGraphics)
        {
            if (statics1.isInteractiveRobot())
            {
                IInteractiveEvents events = ((IInteractiveRobot) robot1).getInteractiveEventListener();
                if (events != null)
                {
                    events.onKeyReleased(this);
                }
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 0x62;
        }

        internal override byte getSerializationType()
        {
            return 0x2d;
        }

        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(KeyReleasedEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer, BinaryReader br)
            {
                char keyChar = br.ReadChar();
                int keyCode = br.ReadInt32();
                int keyLocation = br.ReadInt32();
                int id = br.ReadInt32();
                int modifiers = br.ReadInt32();
                long when = br.ReadInt64();
                return new KeyReleasedEvent(keyChar, keyCode, keyLocation, id, modifiers, when);
            }

            public void serialize(RbSerializer serializer, BinaryWriter bw, object obj)
            {
                KeyEvent event3 = ((KeyReleasedEvent) obj);
                serializer.serialize(bw, event3.KeyChar);
                serializer.serialize(bw, event3.KeyCode);
                serializer.serialize(bw, event3.KeyLocation);
                serializer.serialize(bw, event3.Id);
                serializer.serialize(bw, event3.Modifiers);
                serializer.serialize(bw, event3.When);
            }

            public int sizeOf(RbSerializer serializer, object o)
            {
                return 0x1b;
            }

            #endregion
        }

        #endregion
    }
}