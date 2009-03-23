using System;
using System.Runtime.CompilerServices;
using net.sf.robocode.peer;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class StatusEvent : Event
    {
        private const int DEFAULT_PRIORITY = 0x63;
        private const long serialVersionUID = 1L;
        private RobotStatus status;

        public StatusEvent(RobotStatus status)
        {
            this.status = status;
        }

        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics, IGraphics graphics2D)
        {
            IBasicEvents events = robot1.getBasicEventListener();
            if (events != null)
            {
                events.onStatus(this);
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 0x63;
        }

        internal override byte getSerializationType()
        {
            throw new Exception("Serialization of this type is not supported");
        }

        public RobotStatus getStatus()
        {
            return status;
        }
    }
}