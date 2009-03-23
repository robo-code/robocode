using System;
using net.sf.robocode.peer;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class PaintEvent : Event
    {
        private const int DEFAULT_PRIORITY = 5;
        private const long serialVersionUID = 1L;

        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics1, IGraphics graphicsd1)
        {
            if (statics1.isPaintRobot())
            {
                IPaintEvents events = ((IPaintRobot) robot1).getPaintEventListener();
                if (events != null)
                {
                    events.onPaint(graphicsd1);
                }
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 5;
        }

        internal override byte getSerializationType()
        {
            throw new Exception("Serialization of this type is not supported");
        }
    }
}