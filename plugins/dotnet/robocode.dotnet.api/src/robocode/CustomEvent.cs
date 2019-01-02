/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using net.sf.robocode.peer;
using Robocode.RobotInterfaces;

namespace Robocode
{
    /// <summary>
    /// This event is sent to <see cref="AdvancedRobot.OnCustomEvent(CustomEvent)"/>
    /// when a custom condition is met. Be sure to reset or remove the custom condition to avoid
    /// having it recurring repeatedly (see the example for the <see cref="Condition"/> method.
    /// <seealso cref="Condition"/>
    /// </summary>
    [Serializable]
    public class CustomEvent : Event
    {
        private const int DEFAULT_PRIORITY = 80;

        private readonly Condition condition;

        /// <summary>
        /// Called by the game to create a new CustomEvent when a condition is met.
        /// </summary>
        public CustomEvent(Condition condition)
        {
            this.condition = condition;
            if (condition != null)
            {
                Priority = condition.Priority;
            }
        }

        /// <summary>
        /// Called by the game to create a new CustomEvent when a condition is met.
        /// The event will have the given priority.
        /// An event priority is a value from 0 - 99. The higher value, the higher
        /// priority. The default priority is 80.
        /// <p/>
        /// This is equivalent to calling <see cref="Robocode.Condition.Priority"/> on the
        /// Condition.
        /// </summary>
        /// <param name="condition">The condition that must be met</param>
        /// <param name="priority">The priority of the condition</param>
        public CustomEvent(Condition condition, int priority)
        {
            this.condition = condition;
            Priority = priority;
            if (condition != null)
            {
                condition.Priority = priority;
            }
        }

        /// <summary>
        /// Returns the condition that fired, causing this event to be generated.
        /// Use this to determine which condition fired, and to remove the custom event.
        /// <example>
        ///   <code>
        ///   public void OnCustomEvent(CustomEvent evnt)
        ///   {
        ///       if (event.Condition.Name == "mycondition")
        ///       {
        ///           RemoveCustomEvent(event.Condition);
        ///           // do something else
        ///       }
        ///   }
        ///   </code>
        /// </example>
        /// </summary>
        public Condition Condition
        {
            get { return condition; }
        }

        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        internal override sealed void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            if (statics.IsAdvancedRobot())
            {
                IAdvancedEvents listener = ((IAdvancedRobot) robot).GetAdvancedEventListener();

                if (listener != null)
                {
                    listener.OnCustomEvent(this);
                }
            }
        }

        // sealed to disable overrides
        public override sealed int CompareTo(Event evnt)
        {
            return base.CompareTo(evnt);
        }

        // sealed to disable overrides
        internal override bool IsCriticalEvent
        {
            get { return false; }
        }

        // sealed to disable overrides
        public sealed override int Priority
        {
            get { return base.Priority; }
        }

        internal override byte SerializationType
        {
            get { throw new System.Exception("Serialization not supported on this event type"); }
        }
    }
}
//doc