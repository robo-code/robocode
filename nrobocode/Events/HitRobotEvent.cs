using System;
using System.Collections.Generic;
using System.Text;

namespace nrobocode.Events
{
    public class HitRobotEvent : Event
    {
        public HitRobotEvent(robocode.HitRobotEvent src)
            : base(src)
        {
        }
    }
}
