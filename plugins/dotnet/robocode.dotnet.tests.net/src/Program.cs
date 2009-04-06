using System;
using System.Collections.Generic;
using System.Text;
using robocode.dotnet.bridge.net.jni;

namespace robocode.dotnet.test.net
{
    class Program
    {
        public static void Main(string[] args)
        {
            JavaVM pvm;
            JNIEnv penv;
            //Thread.CurrentThread.CurrentUICulture = new CultureInfo("en-US");
            JNIResult vm = JNI.JNI_CreateJavaVM(out pvm, out penv);

            JObject outStream = penv.GetStaticObjectField("java/lang/System", "out", "Ljava/io/PrintStream;");
            penv.CallVoidMethod(outStream, "println", "test af sdf as df ", 1);
        }
    }
}
