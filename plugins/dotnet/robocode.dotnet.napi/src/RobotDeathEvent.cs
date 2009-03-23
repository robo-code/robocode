using System;
using System.Runtime.CompilerServices;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class RobotDeathEvent : Event
    {
        private const int DEFAULT_PRIORITY = 70;
        private const long serialVersionUID = 1L;
        private string robotName;

        public RobotDeathEvent(string robotName)
        {
            this.robotName = robotName;
        }

        internal static string access100(RobotDeathEvent event1)
        {
            return event1.robotName;
        }

        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        internal override void dispatch(IBasicRobot robot1, IRobotStatics sta, IGraphics gra)
        {
            IBasicEvents events = robot1.getBasicEventListener();
            if (events != null)
            {
                events.onRobotDeath(this);
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 70;
        }

        public string getName()
        {
            return robotName;
        }

        public string getRobotName()
        {
            return robotName;
        }

        internal override byte getSerializationType()
        {
            return 0x27;
        }

        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(RobotDeathEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer1, ByteBuffer buffer1)
            {
                return new RobotDeathEvent(serializer1.deserializeString(buffer1));
            }

            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (RobotDeathEvent) obj1;
                serializer1.serialize(buffer1, access100(event2));
            }

            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var event2 = (RobotDeathEvent) obj1;
                return (1 + serializer1.sizeOf(access100(event2)));
            }

            #endregion
        }

        #endregion
    }
}