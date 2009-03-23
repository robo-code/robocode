using System.Runtime.CompilerServices;

namespace robocode
{
    public class RadarTurnCompleteCondition : Condition
    {
        private AdvancedRobot robot;

        public RadarTurnCompleteCondition(AdvancedRobot robot)
        {
            this.robot = robot;
        }

        public RadarTurnCompleteCondition(AdvancedRobot robot, int priority)
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
            return (robot.getRadarTurnRemaining() == 0f);
        }
    }
}