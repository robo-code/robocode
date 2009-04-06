using System;
using System.Collections.Generic;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
using System.Text;

namespace robocode.dotnet.bridge.net.jni
{
    public unsafe class JObject
    {
        public Native* native;
        protected JObject(Native* obj)
        {
            this.native = obj;
        }

        public JClass GetClass(JNIEnv env)
        {
            return env.GetObjectClass(this);
        }

        public bool IsString(JNIEnv env)
        {
            return env.GetObjectClass(this).GetClassName(env) == "java.lang.String";
        }

        public void CallVoidMethod(JNIEnv env, string method, params object[] args)
        {
            env.CallVoidMethod(this, method, args);
        }

        public JObject CallObjectMethod(JNIEnv env, string method, params object[] args)
        {
            return env.CallObjectMethod(this, method, args);
        }

        public string CallStringMethod(JNIEnv env, string method, params object[] args)
        {
            return env.CallStringMethod(this, method, args);
        }

        [StructLayout(LayoutKind.Sequential, Size = 1), NativeCppClass]
        public struct Native
        {
            public JObject Wrap()
            {
                fixed (Native* real1 = &this)
                {
                    return new JObject(real1);
                }
            }
        }
    }
}
