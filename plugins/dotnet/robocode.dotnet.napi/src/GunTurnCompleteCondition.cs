using System.Runtime.CompilerServices;

namespace robocode
{
    public class GunTurnCompleteCondition : Condition
    {
        private AdvancedRobot robot;

        public GunTurnCompleteCondition(AdvancedRobot robot)
        {
            this.robot = null;
            this.robot = robot;
        }

        public GunTurnCompleteCondition(AdvancedRobot robot, int priority)
        {
            this.robot = null;
            this.robot = robot;
            base.priority = priority;
        }

        public override sealed void cleanup()
        {
            robot = null;
        }

        public override bool test()
        {
            return (robot.getGunTurnRemaining() == 0f);
        }
    }
}