using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Snapshot
{
    /**
     * Defines a robot state, where the robot can be: active on the battlefield, hitting a wall or robot this second,
     * or dead.
     *
     * @author Flemming N. Larsen (original)
     * @since 1.6.2
     */
    public enum RobotState
    {
        /** The robot is active on the battlefield and has not hit the wall or a robot at this time. */
        Active = 0,

        /** The robot has hit a wall, i.e. one of the four borders, at this time. This state only last one turn. */
        HitWall = 1,

        /** The robot has hit another robot at this time. This state only last one turn. */
        HitRobot = 2,

        /** The robot is dead. */
        Dead = 3
    }
}
