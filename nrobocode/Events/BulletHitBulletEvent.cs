using System;
using System.Collections.Generic;
using System.Text;

namespace nrobocode.Events
{
    public class BulletHitBulletEvent : Event
    {
        public BulletHitBulletEvent(robocode.BulletHitBulletEvent src)
            : base(src)
        {
        }
    }
}
