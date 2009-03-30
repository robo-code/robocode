using System;
using System.Runtime.InteropServices;
using robocode.dotnet.bridge.net.jni;
using selvin.exportdllattribute;

namespace robocode.dotnet.bridge.net
{
    public unsafe class Bridge
    {
        [ExportDll("Java_net_sf_robocode_dotnet_bridge_Bridge_initDotNet", CallingConvention.StdCall)]
        public static void initDotNet(JNIEnv.Native* env, jobject* obj)
        {
            JNIEnv envi = (*env).Wrap();
            Console.WriteLine(".NET hello" + envi.GetVersion());
            envi.CallVoidMethod(obj, "talkBack", "()V");
            envi.CallVoidMethod(obj, "talkBackInt", "(I)V", new jvalue(1));
        }
    }
}
