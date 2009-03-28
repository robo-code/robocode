using System.Runtime.CompilerServices;
using IKVM.Attributes;

namespace robocode.control.events
{
    public class BattleErrorEvent : BattleEvent
    {
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string error;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 180, 0x68, 0x67})]
        public BattleErrorEvent(string error)
        {
            this.error = error;
        }

        public virtual string getError()
        {
            return error;
        }
    }
}