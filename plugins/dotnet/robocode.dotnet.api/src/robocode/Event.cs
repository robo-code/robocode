/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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

        private long time;
        private int priority;

        // time is valid only after adding to event manager on proxy side, we do not update it on battle side
        private volatile bool addedToQueue;

        /// <summary>
        /// Compares this event to another event regarding precedence.
        /// <para>
        /// The event precedence is first and foremost determined by the event time,
        /// secondly the event priority, and lastly specific event information.</para>
        /// <para>
        /// This method will first compare the time of each event. If the event time
        /// is the same for both events, then this method compared the priority of
        /// each event. If the event priorities are equals, then this method will
        /// compare the two events based on specific event information.</para>
        /// <para>
        /// This method is called by the game in order to sort the event queue of a
        /// robot to make sure the events are listed in chronological order.</para>
        /// </summary>
        ///
        /// <param name="evnt">the event to compare to this event.</param>
        /// <returns>
        /// Returns a negative value if this event has higher precedence, i.e. must
        /// be listed before the specified event. A positive value if this event
        /// has a lower precedence, i.e. must be listed after the specified event.
        /// 0 means that the precedence of the two events are equal.
        /// </returns>
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
        /// The time when this event occurred.
        /// </summary>
        /// <remarks>
        /// Note that the time is equal to the turn of a battle round.
        /// <para>
        /// This method is intended for letting robot developers create their own event types.
        /// It is not possible to change the time of an event after it has been added to the event
        /// queue of the robot.</para>
        /// </remarks>
        public long Time
        {
            get { return time; }
            set
            {
                if (addedToQueue)
                {
                    LoggerN.WriteLineToRobotsConsole("SYSTEM: The time of an event cannot be changed after it has been added the event queue.");
                }
                else
                {
                    time = value;
                }
            }
        }

        /// <summary>
        /// The priority of this event.
        /// <para>
        /// An event priority is a value from 0 - 99. The higher value, the higher priority.</para>
        /// <para>
        /// The default priority is 80, but varies depending on the type of event.</para>
        /// </summary>
        /// <remarks>
        /// This method is intended for letting robot developers create their own event types.
        /// <para>
        /// It is not possible to change the priority of an event after it has been added to the event
        /// queue of the robot.</para>
        /// </remarks>
        /// <seealso cref="SetEventPriority(string, int)"/>
        public virtual int Priority
        {
            get { return priority; }
            set
            {
                if (addedToQueue)
                {
                    LoggerN.WriteLineToRobotsConsole("SYSTEM: The priority of an event cannot be changed after it has been added the event queue.");
                }
                else
                {
                    SetPriorityHidden(value);
                }
            }
        }

        /// <summary>
        /// Hidden method for setting the exact time when this event occurred.
        /// </summary>
        /// <remarks>
        /// This method is called by the game engine only.
        /// </remarks>
        /// <param name="time">the time when this event occurred.</param>
        // This method must be invisible on Robot API
        private void SetTimeHidden(long time)
        {
            // we do not replace time which is set by robot to the future 
            if (this.time < time)
            {
                this.time = time;
            }
            // when this flag is set, robots are not allowed to change the time or priority of the event anymore
            addedToQueue = true;
        }

        /// <summary>
        /// Hidden method for setting the priority from the game engine without checking for the 'addedToQueue' flag.
        /// </summary>
        /// <remarks>
        /// This method is called by the game engine only.
        /// </remarks>
        /// <param name="newPriority">the new priority of this event.</param>
        // This method must be invisible on Robot API
        private void SetPriorityHidden(int newPriority)
        {
            if (newPriority < 0)
            {
                LoggerN.WriteLineToRobotsConsole("SYSTEM: Priority must be between 0 and 99");
                LoggerN.WriteLineToRobotsConsole("SYSTEM: Priority for " + GetType().Name + " will be 0");
                newPriority = 0;
            }
            else if (newPriority > 99)
            {
                LoggerN.WriteLineToRobotsConsole("SYSTEM: Priority must be between 0 and 99");
                LoggerN.WriteLineToRobotsConsole("SYSTEM: Priority for " + GetType().Name + " will be 99");
                newPriority = 99;
            }
            priority = newPriority;
        }

        /// <summary>
        /// Dispatch this event for a robot, it's statistics, and graphics context.
        /// </summary>
        /// <param name="robot">the robot to dispatch to.</param>
        /// <param name="statics">the robot to statistics to.</param>
        /// <param name="graphics">the robot to graphics to.</param>
        // This method must be invisible on Robot API
        internal virtual void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
        }

        /// <summary>
        /// The default priority of this event class.
        /// </summary>
        // This method must be invisible on Robot API
        internal virtual int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <summary>
        /// Checks if this event must be delivered even after timeout.
        /// </summary>
        /// <returns>
        /// <c>true</c> when this event must be delivered even after timeout;
        /// <c>false</c> otherwise.
        /// </returns>
        // This method must be invisible on Robot API
        internal virtual bool IsCriticalEvent
        {
            get { return false; }
        }

        // This method must be invisible on Robot API
        internal virtual byte SerializationType
        {
            get { throw new System.Exception("Serialization not supported on this event type"); }
        }

		// Needed for .NET version
        // This method must be invisible on Robot API
        internal virtual void UpdateBullets(Dictionary<int, Bullet> bullets)
        {
        }

        /// <summary>
        /// Returns a hidden event helper for accessing hidden methods on this object.
        /// </summary>
        // This method must be invisible on Robot API
        private static IHiddenEventHelper createHiddenHelper()
        {
            return new HiddenEventHelper();
        }

        /// <summary>
        /// Hidden event helper implementation for accessing the internal methods of an event.
        /// </summary>
        /// <remarks>
        /// This class is used internally by the game engine.
        /// </remarks>
        // This method must be invisible on Robot API
        private class HiddenEventHelper : IHiddenEventHelper
        {
            public void SetTime(Event evnt, long newTime)
            {
                evnt.SetTimeHidden(newTime);
            }

            public void SetDefaultPriority(Event evnt)
            {
                evnt.Priority = evnt.DefaultPriority;
            }

            public void SetPriority(Event evnt, int newPriority)
            {
                evnt.SetPriorityHidden(newPriority);
            }

            public bool IsCriticalEvent(Event evnt)
            {
                return evnt.IsCriticalEvent;
            }

            public void Dispatch(Event evnt, IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
            {
                evnt.Dispatch(robot, statics, graphics);
            }

            public byte GetSerializationType(Event evnt)
            {
                return evnt.SerializationType;
            }

			// Needed for .NET version
            public void UpdateBullets(Event evnt, Dictionary<int, Bullet> bullets)
            {
                evnt.UpdateBullets(bullets);
            }
        }
    }
}
//doc
