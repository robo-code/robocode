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
 *     - Updated Javadoc
 *     Nathaniel Troutman
 *     - Added cleanup() method for cleaning up references to internal classes
 *       to prevent circular references causing memory leaks
 *******************************************************************************/
namespace robocode
{
    /// <summary>
    /// A prebuilt condition you can use that indicates your robot has finished turning.
    /// </summary>
    /// <seealso cref="Condition"/>
    public class TurnCompleteCondition : Condition
    {
        private readonly AdvancedRobot robot;

        /// <summary>
        /// Creates a new TurnCompleteCondition with default priority.
        /// The default priority is 80.
        /// </summary>
        /// <param name="robot">your robot, which must be a <see cref="AdvancedRobot"/></param>
        public TurnCompleteCondition(AdvancedRobot robot)
        {
            this.robot = robot;
        }

        /// <summary>
        /// Creates a new TurnCompleteCondition with the specified priority.
        /// A condition priority is a value from 0 - 99. The higher value, the
        /// higher priority. The default priority is 80.
        /// </summary>
        /// <param name="robot">your robot, which must be a <see cref="AdvancedRobot"/></param>
        /// <param name="priority">the priority of this condition</param>
        /// <seealso cref="Condition#setPriority(int)"/>
        public TurnCompleteCondition(AdvancedRobot robot, int priority)
        {
            this.robot = robot;
            this.priority = priority;
        }

        /// <summary>
        /// Tests if the robot has finished turning.
        /// Returns true if the robot has stopped turning; false otherwise
        /// </summary>
        public override bool Test()
        {
            return (robot.TurnRemaining == 0);
        }
    }
}
//doc