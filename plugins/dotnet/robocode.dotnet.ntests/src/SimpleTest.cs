using System;
using System.Collections.Generic;
using System.Text;
using NUnit.Framework;

namespace net.sf.robocode.dotnet
{
    [TestFixture]
    public class SimpleTest
    {
        [Test]
        public void test1()
        {
            string url = "asdsasdas.dll!/samplecs.MyCsRobot";
            url = url.Substring(0, url.LastIndexOf(".dll!/") + 4);
            Console.WriteLine(url);
        }
    }
}
