using System.Runtime.CompilerServices;
using IKVM.Attributes;

namespace robocode.control
{
    public class RobotResults : BattleResults
    {
        private const long serialVersionUID = 2L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private RobotSpecification robot;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x1f, 0xdf, 0x38, 0x67})]
        public RobotResults(RobotSpecification robot, BattleResults results)
            : base(
                results.getTeamLeaderName(), results.getRank(), results.getScore(), results.getSurvival(),
                results.getLastSurvivorBonus(), results.getBulletDamage(), results.getBulletDamageBonus(),
                results.getRamDamage(), results.getRamDamageBonus(), results.getFirsts(), results.getSeconds(),
                results.getThirds())
        {
            this.robot = robot;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x10, 0x9f, 6, 0x67})]
        public RobotResults(RobotSpecification robot, string teamLeaderName, int rank, double score, double survival,
                            double lastSurvivorBonus, double bulletDamage, double bulletDamageBonus, double ramDamage,
                            double ramDamageBonus, int firsts, int seconds, int thirds)
            : base(
                teamLeaderName, rank, score, survival, lastSurvivorBonus, bulletDamage, bulletDamageBonus, ramDamage,
                ramDamageBonus, firsts, seconds, thirds)
        {
            this.robot = robot;
        }

        [LineNumberTable(new byte[] {0x37, 0x88, 0x6a, 0x2b, 0xa6})]
        public static RobotResults[] convertResults(BattleResults[] results)
        {
            var resultsArray = new RobotResults[results.Length];
            for (int i = 0; i < results.Length; i++)
            {
                resultsArray[i] = (RobotResults) results[i];
            }
            return resultsArray;
        }

        public virtual RobotSpecification getRobot()
        {
            return robot;
        }
    }
}