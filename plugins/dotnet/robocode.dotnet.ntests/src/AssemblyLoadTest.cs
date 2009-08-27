using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using net.sf.jni4net;
using net.sf.robocode.dotnet.host.seed;
using net.sf.robocode.dotnet.repository.root;
using NUnit.Framework;

namespace net.sf.robocode.dotnet
{
    [TestFixture]
    public class AssemblyLoadTest
    {
        [TestFixtureSetUp]
        public void initJVM()
        {
            Directory.SetCurrentDirectory(@"D:\Sf\RobocodeN\plugins\dotnet\robocode.dotnet.tests\");
            // -Xmx512M -Dsun.io.useCanonCaches=false 
            Bridge.Verbose = true;
            Bridge.CreateJVM(@"-Djava.class.path=..\..\..\robocode.api\target\test-classes;..\..\..\robocode.api\target\classes;..\..\..\robocode.battle\target\test-classes;..\..\..\robocode.battle\target\classes;..\..\..\robocode.core\target\test-classes;..\..\..\robocode.core\target\classes;C:\Users\Zamboch\.m2\repository\org\picocontainer\picocontainer\2.6\picocontainer-2.6.jar;C:\Users\Zamboch\.m2\repository\junit\junit\4.5\junit-4.5.jar;..\..\..\robocode.host\target\test-classes;..\..\..\robocode.host\target\classes;..\..\..\robocode.repository\target\test-classes;..\..\..\robocode.repository\target\classes;C:\Users\Zamboch\.m2\repository\net\sf\robocode\codesize\1.1\codesize-1.1.jar;..\..\..\robocode.roborumble\target\test-classes;..\..\..\robocode.roborumble\target\classes;..\..\..\robocode.samples\target\test-classes;..\..\..\robocode.samples\target\classes;..\..\..\robocode.sound\target\test-classes;..\..\..\robocode.sound\target\classes;..\..\..\robocode.tests\target\test-classes;..\..\..\robocode.tests\target\classes;..\..\..\robocode.ui\target\test-classes;..\..\..\robocode.ui\target\classes;..\..\..\robocode.ui.editor\target\test-classes;..\..\..\robocode.ui.editor\target\classes;..\robocode.dotnet.api\target\test-classes;..\robocode.dotnet.api\target\classes;..\robocode.dotnet.host\target\test-classes;..\robocode.dotnet.host\target\classes;C:\Users\Zamboch\.m2\repository\net\sf\jni4net\jni4net.j\0.2.0.0\jni4net.j-0.2.0.0.jar;..\robocode.dotnet.nhost\target\test-classes;..\robocode.dotnet.nhost\target\classes;..\robocode.dotnet.samples\target\test-classes;..\robocode.dotnet.samples\target\classes;..\robocode.dotnet.tests\target\test-classes;..\robocode.dotnet.tests\target\classes");
            // robocode.Robocode -battle D:\Sf\RobocodeN\target\robocode-1.7.1.3-setup\battles\ntest.battle
            Bridge.LoadAndRegisterAssembly(typeof(DllRootHelper).Assembly.Location);
        }
        
        [Test]
        public void testDomain()
        {
            Directory.SetCurrentDirectory(@"D:\Sf\RobocodeN2\plugins\dotnet\robocode.dotnet.tests\");
            var h = new DllRootHelper();
            string[] strings = h.findItems(@"file:/D:/Sf/RobocodeN2/plugins/dotnet/robocode.dotnet.samples/target/MyCsRobot.dll");
            Assert.AreEqual(2, strings.Length);
        }

        [Test]
        public void testType()
        {
            Directory.SetCurrentDirectory(@"D:\Sf\RobocodeN2\plugins\dotnet\robocode.dotnet.tests\");
            AppDomainShell shell=new AppDomainShell(@"D:/Sf/RobocodeN2/plugins/dotnet/robocode.dotnet.samples/target/MyCsRobot.dll");
            shell.GetRobotType("samplecs.MyCsRobot");
        }
    
    }
}
