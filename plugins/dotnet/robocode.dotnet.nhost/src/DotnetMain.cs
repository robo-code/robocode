using System.Globalization;
using System.IO;
using System.Runtime.InteropServices;
using System.Threading;
using osadkowski.exportdllattribute;
using robocode.dotnet.nhost.jni;

namespace robocode.dotnet.nhost
{
    public class DotnetMain
    {
        [ExportDll("dotnetmain", CallingConvention.StdCall)]
        public static void Main()
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
