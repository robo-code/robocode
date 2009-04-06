using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
using System.Text;

namespace robocode.dotnet.bridge.net.jni
{
    public unsafe class JClass : JObject
    {
        public new Native* native
        {
            get
            {
                return (Native*)base.native;
            }
        }

        JClass(Native* ptr)
            : base((JObject.Native*)ptr)
        {
        }

        public string GetClassName(JNIEnv env)
        {
            return CallStringMethod(env, "getName");
        }

        [StructLayout(LayoutKind.Sequential, Size = 1), NativeCppClass]
        public struct Native
        {
            public JClass Wrap()
            {
                fixed (Native* real1 = &this)
                {
                    return new JClass(real1);
                }
            }
        }
    }
}
