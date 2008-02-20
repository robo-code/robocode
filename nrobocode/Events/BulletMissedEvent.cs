using System;
using System.Collections.Generic;
using System.Text;

namespace nrobocode.Events
{
    public class BulletMissedEvent : Event
    {
        public BulletMissedEvent(robocode.BulletMissedEvent src)
            : base(src)
        {
        }
    }
}
