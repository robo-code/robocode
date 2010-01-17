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
 *     - Code cleanup
 *     - Updated Javadocs
 *     Nathaniel Troutman
 *     - Added cleanup() method for cleaning up references to internal classes
 *       to prevent circular references causing memory leaks
 *******************************************************************************/
using System;
using net.sf.robocode.io;

namespace robocode
{
    public class Condition
    {
        private readonly ConditionTest test;

        /// <summary>
        /// The priority of this condition. Defaults to 80.
        /// </summary>
        public int priority = 80;

        /// <summary>
        /// The name of this condition.
        /// </summary>
        public string name;

        public Condition(string name, int priority, ConditionTest test)
            : this(name, priority)
        {
            this.test = test;
        }

        public Condition(string name, ConditionTest test)
            : this(name)
        {
            this.test = test;
        }

        public Condition(ConditionTest test)
        {
            this.test = test;
        }

        /// <summary>
        /// Creates a new, unnamed Condition with the default priority, which is 80.
        /// </summary>
        protected Condition()
        {
        }

        /// <summary>
        /// Creates a new Condition with the specified name, and default priority,
        /// which is 80.
        ///
        /// @param name the name for the new Condition
        /// </summary>
        protected Condition(string name)
        {
            this.name = name;
        }

        /// <summary>
        /// Creates a new Condition with the specified name and priority.
        /// A condition priority is a value from 0 - 99. The higher value, the
        /// higher priority. The default priority is 80.
        ///
        /// @param name	 the name for the new condition
        /// @param priority the priority of the new condition
        /// </summary>
        protected Condition(string name, int priority)
        {
            this.name = name;
            if (priority < 0)
            {
                LoggerN.printlnToRobotsConsole("SYSTEM: Priority must be between 0 and 99.");
                LoggerN.printlnToRobotsConsole("SYSTEM: Priority for condition " + name + " will be 0.");
                priority = 0;
            }
            else if (priority > 99)
            {
                LoggerN.printlnToRobotsConsole("SYSTEM: Priority must be between 0 and 99.");
                LoggerN.printlnToRobotsConsole("SYSTEM: Priority for condition " + name + " will be 99.");
                priority = 99;
            }
            this.priority = priority;
        }

        /// <summary>
        /// Returns the name of this condition.
        ///
        /// @return the name of this condition
        /// </summary>
        public string Name
        {
            get { return name ?? GetType().Name; }
            set { name = value; }
        }

        /// <summary>
        /// Returns the priority of this condition.
        /// A condition priority is a value from 0 - 99. The higher value, the
        /// higher priority. The default priority is 80.
        ///
        /// @return the priority of this condition
        /// </summary>
        public int Priority
        {
            get { return priority; }
            set { priority=value; }
        }

        /// <summary>
        /// Overriding the Test() method is the point of a Condition.
        /// The game will call your Test() function, and take action if it returns
        /// {@code true}. This is valid for both {@link AdvancedRobot#WaitFor} and
        /// {@link AdvancedRobot#AddCustomEvent}.
        /// <p/>
        /// You may not take any actions inside of Test().
        ///
        /// @return {@code true} if the condition has been met, {@code false}
        ///         otherwise.
        /// </summary>
        public virtual bool Test()
        {
            if (test!=null)
            {
                return test(this);
            }
            throw new NotImplementedException("You should inherit from Condition class and override Test() method or pass delegate into constructor");
        }
    }

    public delegate bool ConditionTest(Condition condition);
}
//happy