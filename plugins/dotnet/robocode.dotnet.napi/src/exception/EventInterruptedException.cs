using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.lang;

namespace robocode.exception
{
    public class EventInterruptedException : Error
    {
        private const long serialVersionUID = 1L;
        private readonly int priority = -2147483648;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xa5, 8, 0xab, 0x67})]
        public EventInterruptedException(int priority)
        {
            this.priority = priority;
        }

        public virtual int getPriority()
        {
            return priority;
        }
    }
}