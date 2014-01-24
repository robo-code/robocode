#region Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using System.IO;
using net.sf.jni4net;
using File = java.io.File;

namespace robocode.control
{
    public partial class RobocodeEngine
    {
        public static void Init(string robocodeHome)
        {
            BridgeSetup bridgeSetup = new BridgeSetup();
            bridgeSetup.BindStatic = true;
            bridgeSetup.BindNative = true;
            bridgeSetup.AddAllJarsClassPath(Path.Combine(robocodeHome, "libs"));
            Bridge.CreateJVM(bridgeSetup);
            Bridge.LoadAndRegisterAssemblyByName(typeof(RobocodeEngine).Assembly.FullName);
            //Bridge.Setup.BindNative = true;
            Bridge.Setup.BindStatic = true;
        }
    }
}
