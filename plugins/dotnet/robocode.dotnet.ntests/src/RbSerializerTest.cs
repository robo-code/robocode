using System;
using System.IO;
using net.sf.robocode.api;
using net.sf.robocode.serialization;
using NUnit.Framework;
using robocode.dotnet.ncore.peer;
using robocode.util;

namespace robocode.dotnet.ntests
{
    [TestFixture]
    public class RbSerializerTest
    {
        private readonly byte[] ba;

        public RbSerializerTest()
        {
            var b = new sbyte[]
                        {
                            -95, -34, -34, -64, 0, 2, 7, 1, -81, 0, 0, 0, 1, 14, 45, -78, -99, -17, -61, 98, 64, 0, 0, 0
                            , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -116, 41, 41,
                            -1, -116, 41, 41, -1, -116, 41, 41, -1, -1, 0, 0, -1, -1, -1, -1, -1, -121, 68, -25, 74, 24,
                            87, -58, 63, 0, 0, 0, 0, 0, 0, 32, 64, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, 2, 0, 0,
                            0, 0, 0, 0, -16, 63, 1, 116, -75, 21, -5, -53, -18, -19, 63, 11, 0, 0, 0, 2, 0, 0, 0, 0, 0,
                            0, -16, 63, 0, -58, 109, 52, -128, -73, 64, -18, 63, 12, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, -16,
                            63, 1, 24, 38, 83, 5, -93, -110, -18, 63, -128, -1, -1, -1, -128, -128, 4, 4, 0, 0, 0, 116,
                            101, 115, 116, 8, 0, 0, 0, 116, 101, 115, 116, 68, 97, 116, 97, -128
                        };
            ba = new byte[b.Length];
            Buffer.BlockCopy(b, 0, ba, 0, b.Length);
            Module.Init();
            ncore.Module.Init();
        }


        public static void assertNear(double v1, double v2)
        {
            Assert.AreEqual(v1, v2, Utils.NEAR_DELTA);
        }

        [Test]
        public void Test1()
        {
            var rbs = new RbSerializer();
            var ec2 = (ExecCommands) rbs.deserialize(new MemoryStream(ba));

            assertNear(ec2.getBodyTurnRemaining(), 150.123);
            assertNear(ec2.getBullets()[0].getPower(), 1.0);
            Assert.AreEqual(ec2.getBullets()[1].isFireAssistValid(), false);
            Assert.AreEqual(ec2.getBullets()[2].isFireAssistValid(), true);
            Assert.AreEqual(ec2.getBullets()[2].getBulletId(), -128);
            Assert.AreEqual(ec2.getDebugProperties()[0].getValue(), "testData");
        }
    }
}