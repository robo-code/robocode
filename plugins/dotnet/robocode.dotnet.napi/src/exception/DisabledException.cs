using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.lang;

namespace robocode.exception
{
    public class DisabledException : Error
    {
        private const long serialVersionUID = 1L;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xa4, 0x66})]
        public DisabledException()
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xa8, 0x67})]
        public DisabledException(string s) : base(s)
        {
        }
    }
}