using System.Globalization;
using System.Threading;
using NUnit.Framework;
using robocode.dotnet.bridge.net.jni;

namespace robocode.dotnet.test.net
{
    [TestFixture]
    public class BridgeTest
    {
        [Test]
        public void CreateJVM()
        {
            JavaVM pvm;
            JNIEnv penv;
            //Thread.CurrentThread.CurrentUICulture = new CultureInfo("en-US");
            JNIResult vm = JNI.JNI_CreateJavaVM(out pvm, out penv);

            JObject outStream = penv.GetStaticObjectField("java/lang/System", "out", "Ljava/io/PrintStream;");
            outStream.CallVoidMethod(penv, "println", "message from Java");
        }
    }
}
