using System.Runtime.CompilerServices;
using IKVM.Attributes;
using robocode.control.snapshot;

namespace robocode.control.events
{
    public class TurnEndedEvent : BattleEvent
    {
        [Modifiers(Modifiers.Private | Modifiers.Final)] private ITurnSnapshot turnSnapshot;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xb7, 0x68, 0x67})]
        public TurnEndedEvent(ITurnSnapshot turnSnapshot)
        {
            this.turnSnapshot = turnSnapshot;
        }

        public virtual ITurnSnapshot getTurnSnapshot()
        {
            return turnSnapshot;
        }
    }
}