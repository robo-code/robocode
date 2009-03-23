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
            object serializable2;
            object serializable = message;
            this.sender = sender;
            /*object obj2 = serializable.__<ref>;
            object obj3 = obj2;
            serializable2.__<ref> = obj3;
            this.message = serializable2;*/
        }

        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics1, IGraphics graphics2D)
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

        internal override sealed int getDefaultPriority()
        {
            return 0x4b;
        }

        /*public Serializable getMessage()
        {
            Serializable serializable2;
            serializable2.__<ref> = this.message.__<ref>;
            return serializable2;
        }*/

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