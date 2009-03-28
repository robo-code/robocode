using System.Runtime.CompilerServices;
using IKVM.Attributes;
using IKVM.Runtime;
using java.io;
using java.lang;

namespace robocode.control
{
    [Implements(new[] {"java.io.Serializable"})]
    public class BattleSpecification : Object, Serializable.__Interface
    {
        private const long serialVersionUID = 1L;
        private readonly int battlefieldHeight;
        private readonly int battlefieldWidth;
        private readonly double gunCoolingRate;
        private readonly long inactivityTime;
        private readonly int numRounds;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private RobotSpecification[] robots;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xbf, 120})]
        public BattleSpecification(int numRounds, BattlefieldSpecification battlefieldSize, RobotSpecification[] robots)
            : this(numRounds, 450L, 0.1, battlefieldSize, robots)
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(
             new byte[] {12, 0xe8, 0x22, 0x6b, 0x6b, 0x68, 0x70, 0xec, 0x5b, 0x67, 0x67, 0x69, 0x6d, 0x8d, 0x68})]
        public BattleSpecification(int numRounds, long inactivityTime, double gunCoolingRate,
                                   BattlefieldSpecification battlefieldSize, RobotSpecification[] robots)
        {
            battlefieldWidth = 800;
            battlefieldHeight = 600;
            this.numRounds = 10;
            this.gunCoolingRate = 0.1;
            this.inactivityTime = 450L;
            this.numRounds = numRounds;
            this.inactivityTime = inactivityTime;
            this.gunCoolingRate = gunCoolingRate;
            battlefieldWidth = battlefieldSize.getWidth();
            battlefieldHeight = battlefieldSize.getHeight();
            this.robots = robots;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x60)]
        public virtual BattlefieldSpecification getBattlefield()
        {
            return new BattlefieldSpecification(battlefieldWidth, battlefieldHeight);
        }

        public virtual double getGunCoolingRate()
        {
            return gunCoolingRate;
        }

        public virtual long getInactivityTime()
        {
            return inactivityTime;
        }

        public virtual int getNumRounds()
        {
            return numRounds;
        }

        [LineNumberTable(new byte[] {0x40, 0x6b, 0xa2, 0x8d, 0x95})]
        public virtual RobotSpecification[] getRobots()
        {
            if (robots == null)
            {
                return null;
            }
            var dest = new RobotSpecification[robots.Length];
            ByteCodeHelper.arraycopy(robots, 0, dest, 0, robots.Length);
            return dest;
        }
    }
}