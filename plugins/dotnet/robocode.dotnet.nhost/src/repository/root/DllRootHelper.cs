#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;
using System.Globalization;
using System.IO;
using System.Threading;
using net.sf.robocode.dotnet.host.seed;

namespace net.sf.robocode.dotnet.repository.root
{
    public class DllRootHelper
    {
        public DllRootHelper()
        {
            Thread.CurrentThread.CurrentUICulture = new CultureInfo("en-US");
        }

        public string[] findItems(string dllPath)
        {
            string file = dllPath.Substring("file:/".Length);
            if (!File.Exists(file))
            {
                throw new ArgumentException();
            }

            using (var shell = new AppDomainShell(file))
            {
                return shell.FindRobots();
            }
        }
    }
}