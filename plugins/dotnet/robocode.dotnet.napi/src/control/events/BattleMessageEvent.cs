using System.Runtime.CompilerServices;
using IKVM.Attributes;

namespace robocode.control.events
{
    public class BattleMessageEvent : BattleEvent
    {
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string message;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 180, 0x68, 0x67})]
        public BattleMessageEvent(string message)
        {
            this.message = message;
        }

        public virtual string getMessage()
        {
            return message;
        }
    }
}