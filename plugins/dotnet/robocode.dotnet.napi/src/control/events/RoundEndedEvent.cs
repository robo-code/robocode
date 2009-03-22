using System.Runtime.CompilerServices;
using IKVM.Attributes;

namespace robocode.control.events
{
    public class RoundEndedEvent : BattleEvent
    {
        [Modifiers(Modifiers.Private | Modifiers.Final)] private int round;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private int turns;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xb6, 0x68, 0x67, 0x67})]
        public RoundEndedEvent(int round, int turns)
        {
            this.round = round;
            this.turns = turns;
        }

        public virtual int getRound()
        {
            return round;
        }

        public virtual int getTurns()
        {
            return turns;
        }
    }
}