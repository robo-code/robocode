using System.Runtime.CompilerServices;
using IKVM.Attributes;

namespace robocode.control.events
{
    public class BattleFinishedEvent : BattleEvent
    {
        [Modifiers(Modifiers.Private | Modifiers.Final)] private bool isAborted;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0x84, 0x83, 0x68, 0x67})]
        public BattleFinishedEvent(bool isAborted)
        {
            this.isAborted = isAborted;
        }

        public virtual bool IsAborted()
        {
            return isAborted;
        }
    }
}