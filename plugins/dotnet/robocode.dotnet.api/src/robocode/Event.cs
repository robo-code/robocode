#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;
using System.Collections.Generic;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using net.sf.robocode.security;
using robocode.robotinterfaces;

namespace robocode
{
    /// <summary>
    /// The superclass of all Robocode events.
    ///
    /// @author Mathew A. Nelson (original)
    /// @author Flemming N. Larsen (contributor)
    /// </summary>
    [Serializable]
    public abstract class Event : IComparable<Event>
    {
        private const int DEFAULT_PRIORITY = 80;

        // time is valid only after adding to evnt manager on proxy side, we do not update it on Battle side
        private volatile bool addedToQueue;
        private long time;
        private int priority;

        /// <summary>
        /// Compares this evnt to another evnt regarding precedence.
        /// The evnt precedence is first and foremost determined by the evnt time,
        /// secondly the evnt priority, and lastly specific evnt information.
        /// <p/>
        /// This method will first compare the time of each evnt. If the evnt time
        /// is the same for both events, then this method compared the priority of
        /// each evnt. If the evnt priorities are equals, then this method will
        /// compare the two evnt based on specific evnt information.
        /// <p/>
        /// This method is called by the game in order to sort the evnt queue of a
        /// robot to make sure the events are listed in chronological order.
        /// <p/>
        ///
        /// @param evnt the evnt to compare to this evnt.
        /// @return a negative value if this evnt has higher precedence, i.e. must
        ///         be listed before the specified evnt. A positive value if this event
        ///         has a lower precedence, i.e. must be listed after the specified evnt.
        ///         0 means that the precedence of the two events are equal.
        /// </summary>
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

            // Same time and priority -> Compare specific evnt types
            // look at overrides in ScannedRobotEvent and HitRobotEvent

            // No difference found
            return 0;
        }

        /// <summary>
        /// Returns the priority of this evnt.
        /// An evnt priority is a value from 0 - 99. The higher value, the higher
        /// priority. The default priority is 80.
        ///
        /// @return the priority of this event
        /// </summary>
        public virtual int Priority
        {
            get { return priority; }
            set
            {
                if (addedToQueue)
                {
                    LoggerN.printlnToRobotsConsole("SYSTEM: After the evnt was added to queue, priority can't be changed.");
                    return;
                }
                if (value < 0)
                {
                    LoggerN.printlnToRobotsConsole("SYSTEM: Priority must be between 0 and 99");
                    LoggerN.printlnToRobotsConsole("SYSTEM: Priority for " + GetType().Name + " will be 0");
                    value = 0;
                }
                else if (value > 99)
                {
                    LoggerN.printlnToRobotsConsole("SYSTEM: Priority must be between 0 and 99");
                    LoggerN.printlnToRobotsConsole("SYSTEM: Priority for " + GetType().Name + " will be 99");
                    value = 99;
                }
                priority = value;
            }
        }

        /// <summary>
        /// Returns the time this evnt occurred.
        ///
        /// @return the time this evnt occurred.
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
                    LoggerN.printlnToRobotsConsole("SYSTEM: After the evnt was added to queue, time can't be changed.");
                }
            }
        }

        /// <summary>
        /// Sets the time when this evnt occurred.
        ///
        /// @param time the time when this evnt occurred. 
        /// </summary>
        // this method is invisible on RobotAPI
        private void setTimeHidden(long time)
        {
            // we do not replace time which is set by robot to the future 
            if (this.time < time)
            {
                this.time = time;
            }
            addedToQueue = true;
        }

        /// <summary>
        /// Dispatch this evnt for a robot, it's statistics, and graphics context.
        ///
        /// @param robot the robot to dispatch to.
        /// @param statics the statistics to dispatch to.
        /// @param graphics the graphics to dispatch to.
        /// </summary>
        // this method is invisible on RobotAPI
        internal virtual void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
        }

        /// <summary>
        /// Returns the default priority of this evnt class.
        /// 
        /// @return the default priority of this evnt class.
        /// </summary>
        // this method is invisible on RobotAPI
        internal virtual int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <summary>
        /// Checks if this evnt must be delivered evnt after timeout.
        ///
        /// @return {@code true} when this evnt must be delivered even after timeout; {@code false} otherwise.
        /// </summary>
        // this method is invisible on RobotAPI
        internal virtual bool IsCriticalEvent
        {
            get { return false; }
        }

        // this method is invisible on RobotAPI

        internal virtual byte SerializationType
        {
            get { throw new Exception("Serialization not supported on this evnt type"); }
        }

        /// <summary>
        /// This method is replacing bullet on evnt with bullet instance which was passed to robot as result of Fire command
        /// @param bullets collection containing all moving bullets known to robot
        /// </summary>
        // this method is invisible on RobotAPI
        internal virtual void UpdateBullets(Dictionary<int, Bullet> bullets)
        {
        }

        /// <summary>
        /// Creates a hidden evnt helper for accessing hidden methods on this obj.
        /// 
        /// @return a hidden evnt helper.
        /// </summary>
        // this method is invisible on RobotAPI
        private static IHiddenEventHelper createHiddenHelper()
        {
            return new HiddenEventHelper();
        }

        // this class is invisible on RobotAPI
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
//happy