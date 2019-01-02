/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System.Collections.Generic;
using net.sf.robocode.security;
using Robocode;

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

                if (!HiddenAccessN.IsCriticalEvent(e))
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

                if ((e.Time <= clearTime) && !HiddenAccessN.IsCriticalEvent(e))
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
