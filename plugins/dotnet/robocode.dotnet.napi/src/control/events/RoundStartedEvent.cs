using System.Runtime.CompilerServices;
using IKVM.Attributes;
using robocode.control.snapshot;

namespace robocode.control.events
{
    public class RoundStartedEvent : BattleEvent
    {
        [Modifiers(Modifiers.Private | Modifiers.Final)] private int round;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private ITurnSnapshot startSnapshot;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xb9, 0x68, 0x67, 0x67})]
        public RoundStartedEvent(ITurnSnapshot startSnapshot, int round)
        {
            this.startSnapshot = startSnapshot;
            this.round = round;
        }

        public virtual int getRound()
        {
            return round;
        }

        public virtual ITurnSnapshot getStartSnapshot()
        {
            return startSnapshot;
        }
    }
}