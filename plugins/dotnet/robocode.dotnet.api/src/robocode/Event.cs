#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using System.Collections.Generic;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using net.sf.robocode.security;
using Robocode.RobotInterfaces;

namespace Robocode
{
    /// <summary>
    /// The superclass of all Robocode events.
    /// </summary>
    [Serializable]
    public abstract class Event : IComparable<Event>
    {
        private const int DEFAULT_PRIORITY = 80;

        // time is valid only after adding to event manager on proxy side, we do not update it on battle side
        private volatile bool addedToQueue;
        private long time;
        private int priority;

        /// <summary>
        /// Compares this event to another event regarding precedence.
        /// The event precedence is first and foremost determined by the event time,
        /// secondly the event priority, and lastly specific event information.
        /// <p/>
        /// This method will first compare the time of each event. If the event time
        /// is the same for both events, then this method compared the priority of
        /// each event. If the event priorities are equals, then this method will
        /// compare the two events based on specific event information.
        /// <p/>
        /// This method is called by the game in order to sort the event queue of a
        /// robot to make sure the events are listed in chronological order.
        /// <p/>
        /// Returns a negative value if this event has higher precedence, i.e. must
        /// be listed before the specified event. A positive value if this event
        /// has a lower precedence, i.e. must be listed after the specified event.
        /// 0 means that the precedence of the two events are equal.
        /// </summary>
        /// <param name="evnt">the event to compare to this event.</param>
        public virtual int CompareTo(Event evnt)
        {
            // Compare the time difference which has precedence over priority.
            var timeDiff = (int) (time - evnt.time);

            if (timeDiff != 0)
            {
                return timeDiff; // Time differ
            }

            // Same time -> Compare the difference in priority
            int priorityDiff = evnt.Priority - Priority;

            if (priorityDiff != 0)
            {
                return priorityDiff; // Priority differ
            }

            // Same time and priority -> Compare specific event types
            // look at overrides in ScannedRobotEvent and HitRobotEvent

            // No difference found
            return 0;
        }

        /// <summary>
        /// Returns the priority of this event.
        /// An event priority is a value from 0 - 99. The higher value, the higher
        /// priority. The default priority is 80.
        /// </summary>
        public virtual int Priority
        {
            get { return priority; }
            set
            {
                if (addedToQueue)
                {
                    LoggerN.WriteLineToRobotsConsole("SYSTEM: After the event was added to queue, priority can't be changed.");
                    return;
                }
                if (value < 0)
                {
                    LoggerN.WriteLineToRobotsConsole("SYSTEM: Priority must be between 0 and 99");
                    LoggerN.WriteLineToRobotsConsole("SYSTEM: Priority for " + GetType().Name + " will be 0");
                    value = 0;
                }
                else if (value > 99)
                {
                    LoggerN.WriteLineToRobotsConsole("SYSTEM: Priority must be between 0 and 99");
                    LoggerN.WriteLineToRobotsConsole("SYSTEM: Priority for " + GetType().Name + " will be 99");
                    value = 99;
                }
                priority = value;
            }
        }

        /// <summary>
        /// Returns the time this event occurred.
        /// </summary>
        public long Time
        {
            get { return time; }
            set
            {
                if (!addedToQueue)
                {
                    time = value;
                }
                else
                {
                    LoggerN.WriteLineToRobotsConsole("SYSTEM: After the event was added to queue, time can't be changed.");
                }
            }
        }

        private void setTimeHidden(long time)
        {
            // we do not replace time which is set by robot to the future 
            if (this.time < time)
            {
                this.time = time;
            }
            addedToQueue = true;
        }

        // This is how event gets dispatched to robot
        internal virtual void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
        }

        internal virtual int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        internal virtual bool IsCriticalEvent
        {
            get { return false; }
        }

        internal virtual byte SerializationType
        {
            get { throw new System.Exception("Serialization not supported on this event type"); }
        }

        internal virtual void UpdateBullets(Dictionary<int, Bullet> bullets)
        {
        }

        private static IHiddenEventHelper createHiddenHelper()
        {
            return new HiddenEventHelper();
        }

        private class HiddenEventHelper : IHiddenEventHelper
        {
            public void setTime(Event evnt, long newTime)
            {
                evnt.setTimeHidden(newTime);
            }

            public void setDefaultPriority(Event evnt)
            {
                evnt.Priority = evnt.DefaultPriority;
            }

            public void setPriority(Event evnt, int newPriority)
            {
                evnt.Priority = newPriority;
            }

            public bool isCriticalEvent(Event evnt)
            {
                return evnt.IsCriticalEvent;
            }

            public void dispatch(Event evnt, IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
            {
                evnt.Dispatch(robot, statics, graphics);
            }

            public byte getSerializationType(Event evnt)
            {
                return evnt.SerializationType;
            }

            public void updateBullets(Event evnt, Dictionary<int, Bullet> bullets)
            {
                evnt.UpdateBullets(bullets);
            }
        }
    }
}
//doc