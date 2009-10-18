using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using net.sf.jni4net;
using net.sf.robocode.dotnet.host.seed;
using net.sf.robocode.dotnet.repository.root;
using net.sf.robocode.repository;
using NUnit.Framework;

namespace net.sf.robocode.dotnet
{
    [TestFixture]
    public class AssemblyLoadTest
    {
        [TestFixtureSetUp]
        public void initJVM()
        {
            Directory.SetCurrentDirectory(@"..\..\robocode.dotnet.tests\");
            // -Xmx512M -Dsun.io.useCanonCaches=false 
            BridgeSetup setup=new BridgeSetup(false);
            setup.Verbose = true;
            setup.AddClassPath(@"C:\Users\Zamboch\.m2\repository\org\picocontainer\picocontainer\2.6\picocontainer-2.6.jar");
            setup.AddClassPath(@"C:\Users\Zamboch\.m2\repository\junit\junit\4.5\junit-4.5.jar");
            setup.AddClassPath(@"C:\Users\Zamboch\.m2\repository\net\sf\robocode\codesize\1.1\codesize-1.1.jar");
            setup.AddClassPath(@"C:\Users\Zamboch\.m2\repository\net\sf\jni4net\jni4net.j\0.4.0.0\jni4net.j-0.4.0.0.jar");
            setup.AddClassPath(@"..\..\..\robocode.api\target\test-classes");
            setup.AddClassPath(@"..\..\..\robocode.api\target\classes");
            setup.AddClassPath(@"..\..\..\robocode.battle\target\test-classes");
            setup.AddClassPath(@"..\..\..\robocode.battle\target\classes");
            setup.AddClassPath(@"..\..\..\robocode.core\target\test-classes");
            setup.AddClassPath(@"..\..\..\robocode.core\target\classes");
            setup.AddClassPath(@"..\..\..\robocode.host\target\test-classes");
            setup.AddClassPath(@"..\..\..\robocode.host\target\classes");
            setup.AddClassPath(@"..\..\..\robocode.repository\target\test-classes");
            setup.AddClassPath(@"..\..\..\robocode.repository\target\classes");
            setup.AddClassPath(@"..\..\..\robocode.roborumble\target\test-classes");
            setup.AddClassPath(@"..\..\..\robocode.roborumble\target\classes");
            setup.AddClassPath(@"..\..\..\robocode.samples\target\test-classes");
            setup.AddClassPath(@"..\..\..\robocode.samples\target\classes");
            setup.AddClassPath(@"..\..\..\robocode.sound\target\test-classes");
            setup.AddClassPath(@"..\..\..\robocode.sound\target\classes");
            setup.AddClassPath(@"..\..\..\robocode.tests\target\test-classes");
            setup.AddClassPath(@"..\..\..\robocode.tests\target\classes");
            setup.AddClassPath(@"..\..\..\robocode.ui\target\test-classes");
            setup.AddClassPath(@"..\..\..\robocode.ui\target\classes");
            setup.AddClassPath(@"..\..\..\robocode.ui.editor\target\test-classes");
            setup.AddClassPath(@"..\..\..\robocode.ui.editor\target\classes");
            setup.AddClassPath(@"..\robocode.dotnet.api\target\test-classes");
            setup.AddClassPath(@"..\robocode.dotnet.api\target\classes");
            setup.AddClassPath(@"..\robocode.dotnet.host\target\test-classes");
            setup.AddClassPath(@"..\robocode.dotnet.host\target\classes");
            setup.AddClassPath(@"..\robocode.dotnet.nhost\target\test-classes");
            setup.AddClassPath(@"..\robocode.dotnet.nhost\target\classes");
            setup.AddClassPath(@"..\robocode.dotnet.samples\target\test-classes");
            setup.AddClassPath(@"..\robocode.dotnet.samples\target\classes");
            setup.AddClassPath(@"..\robocode.dotnet.tests\target\test-classes");
            setup.AddClassPath(@"..\robocode.dotnet.tests\target\classes");
            Bridge.CreateJVM(setup);
            // robocode.Robocode -battle D:\Sf\RobocodeN\target\robocode-1.7.1.3-setup\battles\ntest.battle
            Bridge.LoadAndRegisterAssembly(typeof(DllRootHelper).Assembly.Location);
        }
        
        [Test]
        public void testDomain()
        {
            var h = new DllRootHelper();
            string[] strings = h.findItems(@"file:/../robocode.dotnet.samples/target/robocode.dotnet.samples-1.7.3.0.dll");
            Assert.AreEqual(2, strings.Length);
        }

        [Test]
        public void testType()
        {
            AppDomainShell shell = new AppDomainShell(@"../robocode.dotnet.samples/target/robocode.dotnet.samples-1.7.3.0.dll");
            RobotType type = shell.GetRobotType("samplecs.MyCsRobot");
            Assert.AreEqual(RobotType.STANDARD, type);
        }
    
    }
}
