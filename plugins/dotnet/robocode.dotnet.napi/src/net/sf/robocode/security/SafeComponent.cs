using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.awt;

namespace net.sf.robocode.security
{
    public class SafeComponent : Component
    {
        private static Component safeEventComponent;

        [MethodImpl(MethodImplOptions.NoInlining)]
        public static void __<clinit>()
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xa9, 0x6a, 0x8a})]
        public static Component getSafeEventComponent()
        {
            if (safeEventComponent == null)
            {
                safeEventComponent = new SafeComponent();
            }
            return safeEventComponent;
        }
    }
}