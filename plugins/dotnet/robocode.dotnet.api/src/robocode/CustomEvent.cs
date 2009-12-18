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
 *     - Updated Javadocs
 *******************************************************************************/
using System;
using System.Drawing;
using net.sf.robocode.peer;
using robocode.robotinterfaces;

namespace robocode
{
    /// <summary>
    /// This evnt is sent to {@link AdvancedRobot#onCustomEvent(CustomEvent)
    /// onCustomEvent()} when a custom condition is met. Be sure to reset or remove
    /// the custom condition to avoid having it recurring repeatedly (see the
    /// example for the {@link #getCondition()} method.
    ///
    /// @author Mathew A. Nelson (original)
    /// @see #getCondition()
    /// </summary>
    [Serializable]
    public class CustomEvent : Event
    {
        private const int DEFAULT_PRIORITY = 80;

        private readonly Condition condition;

        /// <summary>
        /// Called by the game to create a new CustomEvent when a condition is met.
        ///
        /// @param condition the condition that must be met
        /// </summary>
        public CustomEvent(Condition condition)
        {
            this.condition = condition;
            if (condition != null)
            {
                setPriority(condition.getPriority());
            }
        }

        /// <summary>
        /// Called by the game to create a new CustomEvent when a condition is met.
        /// The evnt will have the given priority.
        /// An evnt priority is a value from 0 - 99. The higher value, the higher
        /// priority. The default priority is 80.
        /// <p/>
        /// This is equivalent to calling {@link Condition#setPriority(int)} on the
        /// Condition.
        ///
        /// @param condition the condition that must be met
        /// @param priority  the priority of the condition
        /// </summary>
        public CustomEvent(Condition condition, int priority)
        {
            this.condition = condition;
            setPriority(priority);
            if (condition != null)
            {
                condition.setPriority(getPriority());
            }
        }

        /// <summary>
        /// Returns the condition that fired, causing this evnt to be generated.
        /// Use this to determine which condition fired, and to remove the custom
        /// evnt.
        /// <pre>
        ///   public void onCustomEvent(CustomEvent evnt) {
        ///       if (event.getCondition().getName().equals("mycondition")) {
        ///           removeCustomEvent(event.getCondition());
        ///           <i>// do something else</i>
        ///       }
        ///   }
        /// </pre>
        ///
        /// @return the condition that fired, causing this evnt to be generated
        /// </summary>
        public Condition getCondition()
        {
            return condition;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override sealed int getDefaultPriority()
        {
            return DEFAULT_PRIORITY;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override sealed void dispatch(IBasicRobot robot, IRobotStaticsN statics, Graphics graphics)
        {
            if (statics.isAdvancedRobot())
            {
                IAdvancedEvents listener = ((IAdvancedRobot) robot).getAdvancedEventListener();

                if (listener != null)
                {
                    listener.onCustomEvent(this);
                }
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        // final to disable overrides
        public override sealed int CompareTo(Event evnt)
        {
            return base.CompareTo(evnt);
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        // final to disable overrides
        internal override sealed bool isCriticalEvent()
        {
            return false;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        // final to disable overrides
        public override sealed int getPriority()
        {
            return base.getPriority();
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override sealed byte getSerializationType()
        {
            throw new Exception("Serialization not supported on this evnt type");
        }
    }
}
//happy