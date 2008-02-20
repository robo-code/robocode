using System;
using System.Collections.Generic;
using System.Text;

namespace nrobocode.Events
{
    public class HitByBulletEvent : Event
    {
        public HitByBulletEvent(robocode.HitByBulletEvent src)
            : base(src)
        {
        }
    }
}
