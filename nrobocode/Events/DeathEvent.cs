using System;
using System.Collections.Generic;
using System.Text;

namespace nrobocode.Events
{
    public class DeathEvent : Event
    {
        public DeathEvent(robocode.DeathEvent src)
            : base(src)
        {
        }
    }
}
