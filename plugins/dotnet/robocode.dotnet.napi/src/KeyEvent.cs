using System.Runtime.CompilerServices;
using IKVM.Attributes;

namespace robocode
{
    public abstract class KeyEvent : Event
    {
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private java.awt.@event.KeyEvent source;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xae, 0x68, 0x67})]
        public KeyEvent(java.awt.@event.KeyEvent source)
        {
            this.source = source;
        }

        public virtual java.awt.@event.KeyEvent getSourceEvent()
        {
            return source;
        }
    }
}