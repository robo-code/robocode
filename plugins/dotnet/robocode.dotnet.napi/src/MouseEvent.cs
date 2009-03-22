using System.Runtime.CompilerServices;
using IKVM.Attributes;

namespace robocode
{
    public abstract class MouseEvent : Event
    {
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private java.awt.@event.MouseEvent source;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xae, 0x68, 0x67})]
        public MouseEvent(java.awt.@event.MouseEvent source)
        {
            this.source = source;
        }

        public virtual java.awt.@event.MouseEvent getSourceEvent()
        {
            return source;
        }
    }
}