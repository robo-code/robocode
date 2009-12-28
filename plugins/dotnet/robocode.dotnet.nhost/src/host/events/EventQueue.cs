using System.Collections.Generic;
using net.sf.robocode.security;
using robocode;

namespace net.sf.robocode.dotnet.host.events
{
    internal class EventQueue : List<Event>
    {
        public void clear(bool includingSystemEvents)
        {
            if (includingSystemEvents)
            {
                Clear();
                return;
            }

            for (int i = 0; i < Count; i++)
            {
                Event e = this[i];

                if (!HiddenAccessN.isCriticalEvent(e))
                {
                    RemoveAt(i--);
                }
            }
        }

        public void clear(long clearTime)
        {
            for (int i = 0; i < Count; i++)
            {
                Event e = this[i];

                if ((e.Time <= clearTime) && !HiddenAccessN.isCriticalEvent(e))
                {
                    RemoveAt(i--);
                }
            }
        }

        public void sort()
        {
            Sort();
        }
    }
}
