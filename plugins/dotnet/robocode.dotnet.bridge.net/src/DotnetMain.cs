using System;
using System.Globalization;
using System.IO;
using System.Runtime.InteropServices;
using System.Threading;
using robocode.dotnet.bridge.net.jni;
using selvin.exportdllattribute;

namespace robocode.dotnet.bridge.net
{
    public unsafe class DotnetMain
    {
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