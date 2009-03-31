using System;
using System.IO;
using net.sf.robocode.api;
using net.sf.robocode.serialization;
using NUnit.Framework;
using robocode.dotnet.core.net.peer;
using robocode.util;

namespace robocode.dotnet.test.net
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
            core.net.Module.Init();
        }

        [Test]
        public void empty()
        {
            //throw new ApplicationException();
            var ec = new ExecCommands();

            ec.setBodyTurnRemaining(150.123);
            ec.setTryingToPaint(true);
            var rbs = new RbSerializer();

            var outp = new MemoryStream(2048);

            int len = rbs.serialize(outp, RbSerializer.ExecCommands_TYPE, ec);
            byte[] buffer = outp.GetBuffer();
            var inp = new MemoryStream(buffer, 0, len);
            var ec2 = (ExecCommands) rbs.deserialize(inp);

            assertNear(ec2.getBodyTurnRemaining(), ec.getBodyTurnRemaining());
            Assert.AreEqual(ec2.IsTryingToPaint(), true);
        }

        [Test]
        public void withBullets()
        {
            var ec = new ExecCommands();

            ec.setBodyTurnRemaining(150.123);
            ec.getBullets().Add(new BulletCommand(1.0, true, 0.9354, 11));
            ec.getBullets().Add(new BulletCommand(1.0, false, 0.9454, 12));
            ec.getBullets().Add(new BulletCommand(1.0, true, 0.9554, -128));

            var outp = new MemoryStream(1024);
            var rbs = new RbSerializer();

            int len = rbs.serialize(outp, RbSerializer.ExecCommands_TYPE, ec);
            var inp = new MemoryStream(outp.GetBuffer(), 0, len);
            var ec2 = (ExecCommands) rbs.deserialize(inp);

            assertNear(ec2.getBodyTurnRemaining(), ec.getBodyTurnRemaining());
            assertNear(ec2.getBullets()[0].getPower(), 1.0);
            Assert.AreEqual(ec2.getBullets()[1].isFireAssistValid(), false);
            Assert.AreEqual(ec2.getBullets()[2].isFireAssistValid(), true);
            Assert.AreEqual(ec2.getBullets()[2].getBulletId(), -128);
        }

        [Test]
        public void withMessages()
        {
            var ec = new ExecCommands();

            ec.setBodyTurnRemaining(150.123);
            ec.getBullets().Add(new BulletCommand(1.0, true, 0.9354, 11));
            var data = new byte[20];

            data[10] = 10;
            ec.getTeamMessages().Add(new TeamMessage("Foo", "Bar", data));
            ec.getTeamMessages().Add(new TeamMessage("Foo", "Bar", null));

            var outp = new MemoryStream(1024);
            var rbs = new RbSerializer();

            int len = rbs.serialize(outp, RbSerializer.ExecCommands_TYPE, ec);
            var inp = new MemoryStream(outp.GetBuffer(), 0, len);
            var ec2 = (ExecCommands) rbs.deserialize(inp);

            Assert.AreEqual(ec2.getTeamMessages()[0].message[0], 0);
            Assert.AreEqual(ec2.getTeamMessages()[0].message[10], 10);
            Assert.AreEqual(ec2.getTeamMessages()[0].sender, "Foo");
            Assert.AreEqual(ec2.getTeamMessages()[0].recipient, "Bar");
            Assert.AreEqual(ec2.getTeamMessages()[1].message, null);
        }

        [Test]
        public void withProperties()
        {
            var ec = new ExecCommands();

            ec.setBodyTurnRemaining(150.123);
            ec.getBullets().Add(new BulletCommand(1.0, true, 0.9354, 11));
            ec.getTeamMessages().Add(new TeamMessage("Foo", "Bar", null));
            ec.getDebugProperties().Add(
                new DebugProperty("UTF8 Native characters", "P¯Ìliö ûluùouËk˝ k˘Ú ˙pÏl Ô·belskÈ Ûdy"));

            var outp = new MemoryStream(1024);
            var rbs = new RbSerializer();

            int len = rbs.serialize(outp, RbSerializer.ExecCommands_TYPE, ec);
            var inp = new MemoryStream(outp.GetBuffer(),0,len);
            var ec2 = (ExecCommands) rbs.deserialize(inp);

            Assert.AreEqual(ec2.getDebugProperties()[0].getKey(), "UTF8 Native characters");
            Assert.AreEqual(ec2.getDebugProperties()[0].getValue(), "P¯Ìliö ûluùouËk˝ k˘Ú ˙pÏl Ô·belskÈ Ûdy");
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