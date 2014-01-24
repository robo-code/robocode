#region Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using System.IO;
using Robocode;

namespace tested.robotscs
{
    public class FileWriteSize : AdvancedRobot
    {
        public override void Run()
        {
            Out.WriteLine("Data quota: " + DataQuotaAvailable);
            Out.WriteLine("Data directory: " + GetDataDirectory());

            using (Stream dataFile = GetDataFile("test.txt"))
            {
                byte[] buf = new byte[100000];
                for (int i = 0; i < 3; i++)
                {
                    dataFile.Write(buf, 0, buf.Length);
                }
            }
        }
    }
}