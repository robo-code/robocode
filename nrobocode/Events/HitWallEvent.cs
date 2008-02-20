using System;
using System.Collections.Generic;
using System.Text;

namespace nrobocode.Events
{
    public class HitWallEvent : Event
    {
        public HitWallEvent(robocode.HitWallEvent src)
            : base(src)
        {
        }
    }
}
