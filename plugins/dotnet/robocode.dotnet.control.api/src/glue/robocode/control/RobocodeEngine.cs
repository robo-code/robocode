/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
