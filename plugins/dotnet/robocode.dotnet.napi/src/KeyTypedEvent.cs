using System.Runtime.CompilerServices;
using net.sf.robocode.peer;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class KeyTypedEvent : KeyEvent
    {
        private const int DEFAULT_PRIORITY = 0x62;
        private const long serialVersionUID = 1L;

        public KeyTypedEvent(char keyChar, int keyCode, int keyLocation, int id, int modifiers, long when)
            : base(keyChar, keyCode, keyLocation, id, modifiers, when)
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
                    events.onKeyTyped(this);
                }
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 0x62;
        }

        internal override byte getSerializationType()
        {
            return 0x2e;
        }

 
        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(KeyTypedEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer, ByteBuffer buffer1)
            {
                char keyChar = buffer1.getChar();
                int keyCode = buffer1.getInt();
                int keyLocation = buffer1.getInt();
                int id = buffer1.getInt();
                int modifiers = buffer1.getInt();
                long when = buffer1.getLong();
                return new KeyTypedEvent(keyChar, keyCode, keyLocation, id, modifiers, when);
            }

            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                KeyEvent event3 = ((KeyTypedEvent) obj1);
                serializer1.serialize(buffer1, event3.KeyChar);
                serializer1.serialize(buffer1, event3.KeyCode);
                serializer1.serialize(buffer1, event3.KeyLocation);
                serializer1.serialize(buffer1, event3.Id);
                serializer1.serialize(buffer1, event3.Modifiers);
                serializer1.serialize(buffer1, event3.When);
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