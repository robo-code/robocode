using System;
using System.Collections.Generic;
using System.Text;

namespace nrobocode.Events
{
    public class ScannedRobotEvent : Event
    {
        public ScannedRobotEvent(robocode.ScannedRobotEvent src)
            : base(src)
        {
        }
    }
}
