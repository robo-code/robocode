/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
namespace Robocode
{
    /// <summary>
    /// A prebuilt condition you can use that indicates your robot has finished moving.
    /// <seealso cref="Condition"/>
    /// </summary>
    public class MoveCompleteCondition : Condition
    {
        private readonly AdvancedRobot robot;

        /// <summary>
        /// Creates a new MoveCompleteCondition with default priority.
        /// The default priority is 80.
        /// </summary>
        /// <param name="robot">Your robot, which must be a <see cref="AdvancedRobot"/></param>
        public MoveCompleteCondition(AdvancedRobot robot)
        {
            this.robot = robot;
        }

        /// <summary>
        /// Creates a new MoveCompleteCondition with the specified priority.
        /// A condition priority is a value from 0 - 99. The higher value, the
        /// higher priority. The default priority is 80.
        /// <seealso cref="Condition.Priority"/>
        /// </summary>
        /// <param name="robot">Your robot, which must be a <see cref="AdvancedRobot"/></param>
        /// <param name="priority">The priority of this condition</param>
        public MoveCompleteCondition(AdvancedRobot robot, int priority)
        {
            this.robot = robot;
            this.priority = priority;
        }

        /// <summary>
        /// Tests if the robot has stopped moving.
        /// Returns true if the robot has stopped moving
        /// </summary>
        public override bool Test()
        {
            return (robot.DistanceRemaining == 0);
        }
    }
}
//doc