using System;
using System.Collections.Generic;
using System.Text;

namespace nrobocode.Events
{
    public class StatusEvent : Event
    {
        public StatusEvent(robocode.StatusEvent src)
            : base(src)
        {
        }
    }
}
