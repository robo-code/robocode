using System;
using net.sf.robocode.peer;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class MessageEvent : Event
    {
        private const int DEFAULT_PRIORITY = 0x4b;
        private const long serialVersionUID = 1L;
        private readonly string sender;
        private object message;

        public MessageEvent(string sender, object message)
        {
            this.sender = sender;
            this.message = message;
        }

        internal override void dispatch(IBasicRobot robot1, IRobotStatics statics1, IGraphics graphics2D)
        {
            if (statics1.isTeamRobot())
            {
                ITeamEvents events = ((ITeamRobot) robot1).getTeamEventListener();
                if (events != null)
                {
                    events.onMessageReceived(this);
                }
            }
        }

        internal override int getDefaultPriority()
        {
            return 0x4b;
        }

        public object getMessage()
        {
            return message;
        }

        public string getSender()
        {
            return sender;
        }

        internal override byte getSerializationType()
        {
            throw new Exception("Serialization of event type not supported");
        }
    }
}