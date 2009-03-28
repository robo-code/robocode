namespace robocode
{
    public class TurnCompleteCondition : Condition
    {
        private AdvancedRobot robot;

        public TurnCompleteCondition(AdvancedRobot robot)
        {
            this.robot = robot;
        }

        public TurnCompleteCondition(AdvancedRobot robot, int priority)
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
            return (robot.getTurnRemaining() == 0f);
        }
    }
}