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
            string[] strings = h.findItems(@"file:/" + typeof (MyCsRobot).Assembly.Location);
            Assert.GreaterOrEqual(strings.Length, 5);
        }

        [Test]
        public void testType()
        {
            var shell = new AppDomainShell(typeof (MyCsRobot).Assembly.Location);
            RobotType type = shell.GetRobotType("samplecs.MyCsRobot");
            Assert.AreEqual(RobotType.STANDARD, type);
        }
    }
}