using System;
using System.Globalization;
using System.IO;
using System.Runtime.InteropServices;
using System.Threading;
using robocode.dotnet.nhost.jni;
using selvin.exportdllattribute;

namespace robocode.dotnet.nhost
{
    public unsafe class DotnetMain
    {
        [ExportDll("Java_net_sf_robocode_dotnet_host_bridge_DotNetBridge_main", CallingConvention.StdCall)]
        public static void main(JNIEnv.Real* env, jobject* obj)
        {
            JNIEnv envi = (*env).Wrap();
            Console.WriteLine(".NET hello" + envi.GetVersion());
            envi.CallVoidMethod(obj, "talkBack", "()V");
            envi.CallVoidMethod(obj, "talkBackInt", "(I)V", new jvalue(1));
        }

        public static void Test()
        {
            Directory.SetCurrentDirectory(@"c:\Program Files\Java\jre1.6.0_07\bin\client\");
            Thread.CurrentThread.CurrentUICulture = new CultureInfo("en-US");

            JavaVM vm;
            JNIEnv env;
            if (JNI.JNI_CreateJavaVM(out vm, out env) == JNIResult.JNI_OK)
            {
                int version = env.GetVersion();
            }
        }
    }

    //http://blogs.msdn.com/devinj/archive/2005/07/12/438323.aspx
    //Marshal.GetFunctionPointerForDelegate(cosi);
    //Marshal.GetDelegateForFunctionPointer()

    //TestObject testObject = new TestObject();
    //GCHandle gcHandle = GCHandle.Alloc(testObject,
    //GCHandleType.Pinned);
}