using System;
using System.Globalization;
using System.Threading;
using net.sf.jni4net;
using net.sf.jni4net.jni;
using net.sf.robocode.dotnet.repository.root;
using NUnit.Framework;

namespace net.sf.robocode.dotnet
{
    public abstract class TestBase
    {
        protected JNIEnv env;

        [TestFixtureSetUp]
        public virtual void Setup()
        {
            Thread.CurrentThread.CurrentUICulture = new CultureInfo("en-US");
            BridgeSetup setup = new BridgeSetup(false) { Verbose = true, Debug = true };
            string prefix;
            if (Environment.CurrentDirectory.EndsWith("target"))
            {
                prefix = "../../";
            }
            else
            {
                prefix = "../../../";
            }
            setup.AddClassPath(@"C:/Users/Zamboch/.m2/repository/org/picocontainer/picocontainer/2.6/picocontainer-2.6.jar");
            setup.AddClassPath(@"C:/Users/Zamboch/.m2/repository/net/sf/jni4net/jni4net.j/0.6.0.0/jni4net.j-0.6.0.0.jar");
            setup.AddClassPath(prefix + "../../robocode.api/target/test-classes");
            setup.AddClassPath(prefix + "../../robocode.api/target/classes");
            setup.AddClassPath(prefix + "../../robocode.battle/target/test-classes");
            setup.AddClassPath(prefix + "../../robocode.battle/target/classes");
            setup.AddClassPath(prefix + "../../robocode.core/target/test-classes");
            setup.AddClassPath(prefix + "../../robocode.core/target/classes");
            setup.AddClassPath(prefix + "../../robocode.host/target/test-classes");
            setup.AddClassPath(prefix + "../../robocode.host/target/classes");
            setup.AddClassPath(prefix + "../../robocode.repository/target/test-classes");
            setup.AddClassPath(prefix + "../../robocode.repository/target/classes");
            setup.AddClassPath(prefix + "../../robocode.roborumble/target/test-classes");
            setup.AddClassPath(prefix + "../../robocode.roborumble/target/classes");
            setup.AddClassPath(prefix + "../../robocode.samples/target/test-classes");
            setup.AddClassPath(prefix + "../../robocode.samples/target/classes");
            setup.AddClassPath(prefix + "../../robocode.sound/target/test-classes");
            setup.AddClassPath(prefix + "../../robocode.sound/target/classes");
            setup.AddClassPath(prefix + "../../robocode.tests/target/test-classes");
            setup.AddClassPath(prefix + "../../robocode.tests/target/classes");
            setup.AddClassPath(prefix + "../../robocode.ui/target/test-classes");
            setup.AddClassPath(prefix + "../../robocode.ui/target/classes");
            setup.AddClassPath(prefix + "../../robocode.ui.editor/target/test-classes");
            setup.AddClassPath(prefix + "../../robocode.ui.editor/target/classes");
            setup.AddClassPath(prefix + "robocode.dotnet.api/target/test-classes");
            setup.AddClassPath(prefix + "robocode.dotnet.api/target/classes");
            setup.AddClassPath(prefix + "robocode.dotnet.host/target/test-classes");
            setup.AddClassPath(prefix + "robocode.dotnet.host/target/classes");
            setup.AddClassPath(prefix + "robocode.dotnet.nhost/target/test-classes");
            setup.AddClassPath(prefix + "robocode.dotnet.nhost/target/classes");
            setup.AddClassPath(prefix + "robocode.dotnet.samples/target/test-classes");
            setup.AddClassPath(prefix + "robocode.dotnet.samples/target/classes");
            setup.AddClassPath(prefix + "robocode.dotnet.tests/target/test-classes");
            setup.AddClassPath(prefix + "robocode.dotnet.tests/target/classes");
            env = Bridge.CreateJVM(setup);
            Bridge.RegisterAssembly(typeof(TestBase).Assembly);
            Bridge.RegisterAssembly(typeof(DllRootHelper).Assembly);
        }

        [TestFixtureTearDown]
        public void TearDown()
        {
            //Assert.AreEqual(JNIResult.JNI_OK, vm.DestroyJavaVM());
        }
    }
}
