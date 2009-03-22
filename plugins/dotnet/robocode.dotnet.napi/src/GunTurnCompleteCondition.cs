using System.Runtime.CompilerServices;
using IKVM.Attributes;

namespace robocode
{
    public class GunTurnCompleteCondition : Condition
    {
        private AdvancedRobot robot;

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0x9f, 0xb5, 0xe8, 0x37, 0xe7, 0x4a, 0x67})]
        public GunTurnCompleteCondition(AdvancedRobot robot)
        {
            this.robot = null;
            this.robot = robot;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {3, 0xe8, 0x29, 0xe7, 0x58, 0x67, 0x67})]
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x42)]
        public override bool test()
        {
            return (robot.getGunTurnRemaining() == 0f);
        }
    }
}