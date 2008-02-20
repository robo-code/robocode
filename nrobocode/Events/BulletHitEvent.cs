using System;
using System.Collections.Generic;
using System.Text;

namespace nrobocode.Events
{
    public class BulletHitEvent : Event
    {
        public BulletHitEvent(robocode.BulletHitEvent src)
            : base(src)
        {
        }
    }
}
