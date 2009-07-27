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

                if (!HiddenAccess.isCriticalEvent(e))
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

                if ((e.getTime() <= clearTime) && !HiddenAccess.isCriticalEvent(e))
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
