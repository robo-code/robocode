using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.lang;

namespace robocode.exception
{
    public class AbortedException : Error
    {
        private const long serialVersionUID = 1L;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xa7, 0x66})]
        public AbortedException()
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xab, 0x67})]
        public AbortedException(string message) : base(message)
        {
        }
    }
}