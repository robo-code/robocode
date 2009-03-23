using System.Runtime.CompilerServices;

namespace robocode
{
    public class MoveCompleteCondition : Condition
    {
        private AdvancedRobot robot;

        public MoveCompleteCondition(AdvancedRobot robot)
        {
            this.robot = robot;
        }

        public MoveCompleteCondition(AdvancedRobot robot, int priority)
        {
            this.robot = robot;
            base.priority = priority;
        }

        public override void cleanup()
        {
            robot = null;
        }

        public override bool test()
        {
            return (robot.getDistanceRemaining() == 0f);
        }
    }
}