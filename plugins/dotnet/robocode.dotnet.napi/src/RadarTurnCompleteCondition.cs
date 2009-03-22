using System.Runtime.CompilerServices;
using IKVM.Attributes;

namespace robocode
{
    public class RadarTurnCompleteCondition : Condition
    {
        private AdvancedRobot robot;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xb5, 0x68, 0x67})]
        public RadarTurnCompleteCondition(AdvancedRobot robot)
        {
            this.robot = robot;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {3, 0x68, 0x67, 0x67})]
        public RadarTurnCompleteCondition(AdvancedRobot robot, int priority)
        {
            this.robot = robot;
            base.priority = priority;
        }

        public override void cleanup()
        {
            robot = null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x42)]
        public override bool test()
        {
            return (robot.getRadarTurnRemaining() == 0f);
        }
    }
}