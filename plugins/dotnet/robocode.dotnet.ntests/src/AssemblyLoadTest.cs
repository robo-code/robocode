using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using net.sf.jni4net;
using net.sf.robocode.dotnet.host.seed;
using net.sf.robocode.dotnet.repository.root;
using net.sf.robocode.repository;
using NUnit.Framework;
using samplecs;

namespace net.sf.robocode.dotnet
{
    [TestFixture]
    public class AssemblyLoadTest : TestBase
    {
        [Test]
        public void testDomain()
        {
            var h = new DllRootHelper();
            string[] strings = h.findItems(@"file:/" + typeof(MyCsRobot).Assembly.Location);
            Assert.AreEqual(2, strings.Length);
        }

        [Test]
        public void testType()
        {
            AppDomainShell shell = new AppDomainShell(typeof(MyCsRobot).Assembly.Location);
            RobotType type = shell.GetRobotType("samplecs.MyCsRobot");
            Assert.AreEqual(RobotType.STANDARD, type);
        }
    
    }
}
