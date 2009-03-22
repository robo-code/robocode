using IKVM.Attributes;
using java.lang;

namespace robocode.control.snapshot
{
    [Implements(new[] {"java.lang.Comparable"}),
     Signature("Ljava/lang/Object;Ljava/lang/Comparable<Lrobocode/control/snapshot/IScoreSnapshot;>;")]
    public interface IScoreSnapshot : Comparable
    {
        double getCurrentBulletDamageScore();
        double getCurrentBulletKillBonus();
        double getCurrentRammingDamageScore();
        double getCurrentRammingKillBonus();
        double getCurrentScore();
        double getCurrentSurvivalBonus();
        double getCurrentSurvivalScore();
        string getName();
        double getTotalBulletDamageScore();
        double getTotalBulletKillBonus();
        int getTotalFirsts();
        double getTotalLastSurvivorBonus();
        double getTotalRammingDamageScore();
        double getTotalRammingKillBonus();
        double getTotalScore();
        int getTotalSeconds();
        double getTotalSurvivalScore();
        int getTotalThirds();
    }
}