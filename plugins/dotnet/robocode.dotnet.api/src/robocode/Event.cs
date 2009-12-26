/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Optimized for Java 5
 *     - Updated Javadocs
 *     - Removed try-catch(ClassCastException) from compareTo()
 *     - Changed compareTo() to first and foremost compare the events based on
 *       their evnt times, and secondly to compare the priorities if the event
 *       times are equals. Previously, the priorities were compared first, and
 *       secondly the evnt times if the priorities were equal.
 *       This change was made to sort the evnt queues of the robots in
 *       chronological so that the older events are listed before newer events
 *******************************************************************************/
using System;
using System.Collections.Generic;
using System.Drawing;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using net.sf.robocode.security;
using robocode.robocode;
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
        /// Called by the game to create a new Event.
        /// </summary>
        public Event()
        {
        }

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
            int priorityDiff = evnt.getPriority() - getPriority();

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
        public virtual int getPriority()
        {
            return priority;
        }

        /// <summary>
        /// Returns the time this evnt occurred.
        ///
        /// @return the time this evnt occurred.
        /// </summary>
        public long getTime()
        {
            return time;
        }

        /// <summary>
        /// Called by the game to set the priority of an evnt to the priority your
        /// robot specified for this type of evnt (or the default priority).
        /// <p/>
        /// An evnt priority is a value from 0 - 99. The higher value, the higher
        /// priority. The default priority is 80.
        ///
        /// Could be called by robot on events which are not managed by game.
        /// If the evnt is added into EventQueue, the time will be overridden.
        /// 
        /// @param newPriority the new priority of this event
        /// @see AdvancedRobot#setEventPriority(string, int)
        /// </summary>
        public void setPriority(int newPriority)
        {
            if (addedToQueue)
            {
                LoggerN.printlnToRobotsConsole("SYSTEM: After the evnt was added to queue, priority can't be changed.");
                return;
            }
            if (newPriority < 0)
            {
                LoggerN.printlnToRobotsConsole("SYSTEM: Priority must be between 0 and 99");
                LoggerN.printlnToRobotsConsole("SYSTEM: Priority for " + GetType().Name + " will be 0");
                newPriority = 0;
            }
            else if (newPriority > 99)
            {
                LoggerN.printlnToRobotsConsole("SYSTEM: Priority must be between 0 and 99");
                LoggerN.printlnToRobotsConsole("SYSTEM: Priority for " + GetType().Name + " will be 99");
                newPriority = 99;
            }
            priority = newPriority;
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
        /// Could be caled by robot to assign the time to events which are not managed by game.
        /// If the evnt is added into EventQueue, the time could be overriden
        ///
        /// @param newTime the time this evnt occurred
        /// </summary>
        public void setTime(long newTime)
        {
            if (!addedToQueue)
            {
                time = newTime;
            }
            else
            {
                LoggerN.printlnToRobotsConsole("SYSTEM: After the evnt was added to queue, time can't be changed.");
            }
        }

        /// <summary>
        /// Dispatch this evnt for a robot, it's statistics, and graphics context.
        ///
        /// @param robot the robot to dispatch to.
        /// @param statics the statistics to dispatch to.
        /// @param graphics the graphics to dispatch to.
        /// </summary>
        // this method is invisible on RobotAPI
        internal virtual void dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
        }

        /// <summary>
        /// Returns the default priority of this evnt class.
        /// 
        /// @return the default priority of this evnt class.
        /// </summary>
        // this method is invisible on RobotAPI
        internal virtual int getDefaultPriority()
        {
            return DEFAULT_PRIORITY;
        }

        /// <summary>
        /// Checks if this evnt must be delivered evnt after timeout.
        ///
        /// @return {@code true} when this evnt must be delivered even after timeout; {@code false} otherwise.
        /// </summary>
        // this method is invisible on RobotAPI
        internal virtual bool isCriticalEvent()
        {
            return false;
        }

        // this method is invisible on RobotAPI
        internal virtual byte getSerializationType()
        {
            throw new Exception("Serialization not supported on this evnt type");
        }

        /// <summary>
        /// This method is replacing bullet on evnt with bullet instance which was passed to robot as result of fire command
        /// @param bullets collection containing all moving bullets known to robot
        /// </summary>
        // this method is invisible on RobotAPI
        internal virtual void updateBullets(Dictionary<int, Bullet> bullets)
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
                evnt.setPriority(evnt.getDefaultPriority());
            }

            public void setPriority(Event evnt, int newPriority)
            {
                evnt.setPriority(newPriority);
            }

            public bool isCriticalEvent(Event evnt)
            {
                return evnt.isCriticalEvent();
            }

            public void dispatch(Event evnt, IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
            {
                evnt.dispatch(robot, statics, graphics);
            }

            public byte getSerializationType(Event evnt)
            {
                return evnt.getSerializationType();
            }

            public void updateBullets(Event evnt, Dictionary<int, Bullet> bullets)
            {
                evnt.updateBullets(bullets);
            }
        }
    }
}
//happy