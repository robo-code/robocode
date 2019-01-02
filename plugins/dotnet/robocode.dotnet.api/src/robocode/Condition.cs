/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using net.sf.robocode.io;

namespace Robocode
{
    /// <summary>
    /// Condition is used to define custom  <see cref="AdvancedRobot.WaitFor(Condition)"/> 
    /// and custom events for an AdvancedRobot. The code below is taken from the sample robot
    /// named samplecs.Target. See the samplecs/Target.cs for details.
    /// <example>
    ///   <code>
    ///   AddCustomEvent(
    ///       new Condition("triggerhit", (c) =>
    ///       {
    ///           return Energy &lt;= trigger;
    ///       }));
    ///   </code>
    /// </example>
    /// <see cref="AdvancedRobot.WaitFor(Condition)"/>
    /// <see cref="AdvancedRobot.AddCustomEvent(Condition)"/>
    /// <see cref="AdvancedRobot.RemoveCustomEvent(Condition)"/>
    /// <see cref="AdvancedRobot.OnCustomEvent(CustomEvent)"/>
    /// </summary>
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

        /// <summary>
        /// Convinience constructor, allows to pass delegate to method, instead of overriding Test() method.
        /// </summary>
        public Condition(string name, int priority, ConditionTest test)
            : this(name, priority)
        {
            this.test = test;
        }

        /// <summary>
        /// Convinience constructor, allows to pass delegate to method, instead of overriding Test() method.
        /// </summary>
        public Condition(string name, ConditionTest test)
            : this(name)
        {
            this.test = test;
        }

        /// <summary>
        /// Convinience constructor, allows to pass delegate to method, instead of overriding Test() method.
        /// </summary>
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
        /// Creates a new Condition with the specified name, and default priority, which is 80.
        /// </summary>
        /// <param name="name">The name for the new Condition</param>
        protected Condition(string name)
        {
            this.name = name;
        }

        /// <summary>
        /// Creates a new Condition with the specified name and priority.
        /// A condition priority is a value from 0 - 99. The higher value, the
        /// higher priority. The default priority is 80.
        /// </summary>
        /// <param name="name">The name for the new condition</param>
        /// <param name="priority">The priority of the new condition</param>
        protected Condition(string name, int priority)
        {
            this.name = name;
            if (priority < 0)
            {
                LoggerN.WriteLineToRobotsConsole("SYSTEM: Priority must be between 0 and 99.");
                LoggerN.WriteLineToRobotsConsole("SYSTEM: Priority for condition " + name + " will be 0.");
                priority = 0;
            }
            else if (priority > 99)
            {
                LoggerN.WriteLineToRobotsConsole("SYSTEM: Priority must be between 0 and 99.");
                LoggerN.WriteLineToRobotsConsole("SYSTEM: Priority for condition " + name + " will be 99.");
                priority = 99;
            }
            this.priority = priority;
        }

        /// <summary>
        /// Returns the name of this condition.
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
        /// </summary>
        public int Priority
        {
            get { return priority; }
            set { priority=value; }
        }

        /// <summary>
        /// Overriding the Test() method is the point of a Condition.
        /// The game will call your Test() function, and take action if it returns true.
        /// This is valid for both <see cref="AdvancedRobot.WaitFor"/> and
        /// <see cref="AdvancedRobot.AddCustomEvent(Condition)"/>
        /// <p/>
        /// You may not take any actions inside of Test().
        /// </summary>
        public virtual bool Test()
        {
            if (test != null)
            {
                return test(this);
            }
            throw new NotImplementedException("You should inherit from Condition class and override Test() method or pass delegate into constructor");
        }
    }

    /// <summary>
    /// Method signature for Test method of <see cref="Condition"/>
    /// </summary>
    public delegate bool ConditionTest(Condition condition);
}
//doc