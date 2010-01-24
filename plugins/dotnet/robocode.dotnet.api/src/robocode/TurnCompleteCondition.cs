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
    /// A prebuilt condition you can use that indicates your robot has finished
    /// turning.
    ///
    /// @author Mathew A. Nelson (original)
    /// @author Flemming N. Larsen (contributor)
    /// @author Nathaniel Troutman (contributor)
    /// @see Condition
    /// </summary>
    public class TurnCompleteCondition : Condition
    {
        private readonly AdvancedRobot robot;

        /// <summary>
        /// Creates a new TurnCompleteCondition with default priority.
        /// The default priority is 80.
        ///
        /// @param robot your robot, which must be a {@link AdvancedRobot}
        /// </summary>
        public TurnCompleteCondition(AdvancedRobot robot)
        {
            this.robot = robot;
        }

        /// <summary>
        /// Creates a new TurnCompleteCondition with the specified priority.
        /// A condition priority is a value from 0 - 99. The higher value, the
        /// higher priority. The default priority is 80.
        ///
        /// @param robot	your robot, which must be a {@link AdvancedRobot}
        /// @param priority the priority of this condition
        /// @see Condition#setPriority(int)
        /// </summary>
        public TurnCompleteCondition(AdvancedRobot robot, int priority)
        {
            this.robot = robot;
            this.priority = priority;
        }

        /// <summary>
        /// Tests if the robot has finished turning.
        ///
        /// @return {@code true} if the robot has stopped turning; {@code false}
        ///         otherwise
        /// </summary>
        public override bool Test()
        {
            return (robot.TurnRemaining == 0);
        }
    }
}
//happy