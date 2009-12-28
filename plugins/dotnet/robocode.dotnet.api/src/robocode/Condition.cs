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
using net.sf.robocode.io;

namespace robocode
{
    /// <summary>
    /// Condition is used to define custom  {@link AdvancedRobot#WaitFor(Condition)
    /// WaitFor(Condition)} and custom events for an {@link AdvancedRobot}. The code
    /// below is taken from the sample robot named {@code sample.Target}. See the
    /// {@code sample/Target.java} for details.
    /// <pre>
    ///   AddCustomEvent(
    ///       new Condition("triggerhit") {
    ///           public bool Test() {
    ///               return (getEnergy() <= trigger);
    ///           };
    ///       }
    ///   );
    /// </pre>
    /// You should note that by extending Condition this way, you are actually
    /// creating an inner class -- so if you distribute your robot, there will be
    /// multiple class files. (i.e. {@code Target$1.class})
    ///
    /// @author Mathew A. Nelson (original)
    /// @author Flemming N. Larsen (contributor)
    /// @author Nathaniel Troutman (contributor)
    /// @see AdvancedRobot#WaitFor(Condition)
    /// @see AdvancedRobot#AddCustomEvent(Condition)
    /// @see AdvancedRobot#RemoveCustomEvent(Condition)
    /// @see AdvancedRobot#OnCustomEvent(CustomEvent)
    /// </summary>
    public abstract class Condition
    {
        /// <summary>
        /// The priority of this condition. Defaults to 80.
        /// </summary>
        public int priority = 80;

        /// <summary>
        /// The name of this condition.
        /// </summary>
        public string name;

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
        public abstract bool Test();
    }
}
//happy