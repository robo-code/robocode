using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.lang;

namespace robocode.exception
{
    public class DeathException : Error
    {
        private const long serialVersionUID = 1L;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xa6, 0x66})]
        public DeathException()
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 170, 0x67})]
        public DeathException(string message) : base(message)
        {
        }
    }
}