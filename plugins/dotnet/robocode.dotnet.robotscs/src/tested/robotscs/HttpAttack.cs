#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;
using System.Net;
using robocode;

namespace tested.robotscs
{
    public class HttpAttack : AdvancedRobot
    {
        private static string HTTP_ADDR = "http://robocode.sourceforge.net/";

        public override void Run()
        {
            WebClient req = new WebClient();
            using (var res = req.OpenRead(HTTP_ADDR))
            {
                res.Read(new byte[100000], 0, 100000);
            }
        }
    }
}