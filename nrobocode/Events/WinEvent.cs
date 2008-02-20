using System;
using System.Collections.Generic;
using System.Text;

namespace nrobocode.Events
{
    public class WinEvent : Event
    {
        public WinEvent(robocode.WinEvent src)
            : base(src)
        {
        }
    }
}
