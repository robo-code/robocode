using System;
using System.Collections.Generic;
using System.Text;

namespace nrobocode.Events
{
    public class RobotDeathEvent : Event
    {
        public RobotDeathEvent(robocode.RobotDeathEvent src)
            : base(src)
        {
        }
    }
}
