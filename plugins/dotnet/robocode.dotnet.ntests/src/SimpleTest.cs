/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;
using System.Drawing;
using System.IO;
using System.Reflection;
using net.sf.robocode.dotnet.host.proxies;
using net.sf.robocode.dotnet.host.security;
using NUnit.Framework;

namespace net.sf.robocode.dotnet
{
    [TestFixture]
    public class SimpleTest : TestBase
    {
        [Test]
        public void Graphics()
        {
            var sg = new GraphicsProxy();
            sg.setPaintingEnabled(true);
            /*sg.DrawArc(Pens.Red, new RectangleF(0,0,80,80),-30,10);

            GraphicsPath path = new GraphicsPath();
            path.AddLine(99,98,78,3);
            sg.DrawPath(Pens.Blue, path);
            sg.DrawRectangle(Pens.Yellow, 20,20,30,50);

            sg.DrawLine(Pens.Black, 99, 3, 78, 3);
            sg.DrawRectangle(Pens.Black, 90, 20, 30, 50);

            Brush brush = new HatchBrush(HatchStyle.Vertical, Color.Cyan);
            sg.FillRectangle(brush, new RectangleF(20, 70, 30, 50));
            sg.FillEllipse(brush, new RectangleF(70, 70, 30, 50 ));
             */

            sg.DrawEllipse(Pens.Red, (50), (50), 100, 100);
            sg.FillEllipse(new SolidBrush(Color.FromArgb(0, 0xFF, 0, 30)), (100 - 60), (100 - 60), 120,
                           120);


            byte[] readoutQueuedCalls = sg.readoutQueuedCalls();
            Assert.Greater(readoutQueuedCalls.Length, 0);
        }

        [Test]
        [ExpectedException(typeof (AccessViolationException))]
        public void StreamBig()
        {
            string tempDir = Path.Combine(Path.GetTempPath(), Path.GetRandomFileName());
            Directory.CreateDirectory(tempDir);
            var m = new RobotFileSystemManager(null, 100, tempDir, tempDir);

            using (Stream dataFile = m.getDataFile("test.txt"))
            {
                dataFile.WriteByte(0xFF);
                dataFile.Position = 98;
                dataFile.WriteByte(0xFF);
                dataFile.WriteByte(0xFF);
                dataFile.WriteByte(0xFF);
            }
        }

        [Test]
        [ExpectedException(typeof (AccessViolationException))]
        public void StreamBig2()
        {
            string tempDir = Path.Combine(Path.GetTempPath(), Path.GetRandomFileName());
            Directory.CreateDirectory(tempDir);
            var m = new RobotFileSystemManager(null, 100, tempDir, tempDir);

            using (Stream dataFile = m.getDataFile("test.txt"))
            {
                dataFile.WriteByte(0xFF);
                dataFile.Position = 97;
                dataFile.WriteByte(0xFF);
                using (Stream dataFile2 = m.getDataFile("test.txt"))
                {
                    dataFile2.WriteByte(0xFF);
                    dataFile2.Position = 98;
                    dataFile2.WriteByte(0xFF);
                }
            }
        }

        [Test]
        [ExpectedException(typeof (AccessViolationException))]
        public void StreamBig3()
        {
            string tempDir = Path.Combine(Path.GetTempPath(), Path.GetRandomFileName());
            Directory.CreateDirectory(tempDir);
            var m = new RobotFileSystemManager(null, 100, tempDir, tempDir);

            using (Stream dataFile = m.getDataFile("test.txt"))
            {
                dataFile.WriteByte(0xFF);
                dataFile.Position = 97;
                dataFile.WriteByte(0xFF);
                dataFile.Write(new byte[] {0xFF, 0xFF, 0xFF}, 0, 3);
            }
        }

        [Test]
        public void StreamOk()
        {
            string tempDir = Path.Combine(Path.GetTempPath(), Path.GetRandomFileName());
            Directory.CreateDirectory(tempDir);
            var m = new RobotFileSystemManager(null, 100, tempDir, tempDir);

            using (Stream dataFile = m.getDataFile("test.txt"))
            {
                dataFile.WriteByte(0xFF);
                dataFile.Position = 97;
                dataFile.WriteByte(0xFF);
                dataFile.Write(new byte[] {0xFF, 0xFF, 0xFF}, 0, 2);
            }
        }
    }
}