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
 *     Nathaniel Troutman
 *     - Added cleanup() method for cleaning up references to internal classes
 *       to prevent circular references causing memory leaks
 *******************************************************************************/
namespace robocode
{
    /// <summary>
    /// A prebuilt condition you can use that indicates your gun has finished
    /// turning.
    /// <seealso cref="Condition"/>
    /// </summary>
    public class GunTurnCompleteCondition : Condition
    {
        private readonly AdvancedRobot robot;

        /// <summary>
        /// Creates a new GunTurnCompleteCondition with default priority.
        /// The default priority is 80.
        /// </summary>
        /// <param name="robot">your robot, which must be a <see cref="AdvancedRobot"/></param>
        public GunTurnCompleteCondition(AdvancedRobot robot)
        {
            this.robot = robot;
        }

        /// <summary>
        /// Creates a new GunTurnCompleteCondition with a specific priority.
        /// A condition priority is a value from 0 - 99. The higher value, the
        /// higher priority. The default priority is 80.
        /// <seealso cref="Condition.Priority"/>
        /// </summary>
        /// <param name="robot">your robot, which must be a <see cref="AdvancedRobot"/></param>
        /// <param name="priority">the priority of this condition</param>
        public GunTurnCompleteCondition(AdvancedRobot robot, int priority)
        {
            this.robot = robot;
            this.priority = priority;
        }

        /// <summary>
        /// Tests if the gun has stopped turning.
        /// Returns true if the gun has stopped turning
        /// </summary>
        public override bool Test()
        {
            return (robot.GunTurnRemaining == 0);
        }
    }
}
//doc