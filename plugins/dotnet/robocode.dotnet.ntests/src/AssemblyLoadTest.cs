#region Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using net.sf.robocode.dotnet.host.seed;
using net.sf.robocode.dotnet.repository.root;
using net.sf.robocode.repository;
using NUnit.Framework;
using SampleCs;
using tested.robotscs;

namespace net.sf.robocode.dotnet
{
    [TestFixture]
    public class AssemblyLoadTest : TestBase
    {
        [Test]
        public void testDomain()
        {
            string[] strings = DllRootHelper.findItems(@"file:/" + typeof(MyFirstRobot).Assembly.Location);
            Assert.GreaterOrEqual(strings.Length, 5);
        }

        [Test]
        public void testType()
        {
            var shell = new AppDomainShell();
            shell.Init(false);
            shell.Open(typeof(ThreadAttack).Assembly.Location);
            RobotType robotType = shell.GetRobotType(typeof(ThreadAttack).FullName);
            Assert.IsTrue(robotType.isAdvancedRobot());
            shell.Open(typeof(MyFirstRobot).Assembly.Location);
            robotType = shell.GetRobotType(typeof(MyFirstRobot).FullName);
            Assert.IsTrue(robotType.isStandardRobot());
            shell.Dispose();
        }

    }
}