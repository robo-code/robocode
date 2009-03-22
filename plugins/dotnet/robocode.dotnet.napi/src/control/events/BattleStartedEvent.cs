using System.Runtime.CompilerServices;
using IKVM.Attributes;

namespace robocode.control.events
{
    public class BattleStartedEvent : BattleEvent
    {
        [Modifiers(Modifiers.Private | Modifiers.Final)] private BattleRules battleRules;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private bool isReplay;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private int robotsCount;

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0x9f, 0x83, 0xa3, 0x68, 0x67, 0x67, 0x67})]
        public BattleStartedEvent(BattleRules battleRules, int robotsCount, bool isReplay)
        {
            this.battleRules = battleRules;
            this.isReplay = isReplay;
            this.robotsCount = robotsCount;
        }

        public virtual BattleRules getBattleRules()
        {
            return battleRules;
        }

        public virtual int getRobotsCount()
        {
            return robotsCount;
        }

        public virtual bool IsReplay()
        {
            return isReplay;
        }
    }
}