/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;
using System.Globalization;
using System.Threading;
using net.sf.jni4net;
using net.sf.jni4net.jni;
using net.sf.robocode.dotnet.repository.root;
using NUnit.Framework;

namespace net.sf.robocode.dotnet
{
    public abstract class TestBase
    {
        protected JNIEnv env;

        [TestFixtureSetUp]
        public virtual void Setup()
        {
            Thread.CurrentThread.CurrentUICulture = CultureInfo.InvariantCulture;
            Thread.CurrentThread.CurrentCulture = CultureInfo.InvariantCulture;
            var setup = new BridgeSetup(false) { Verbose = true, Debug = true };
            string prefix;
            if (Environment.CurrentDirectory.EndsWith("target"))
            {
                prefix = "../../";
            }
            else
            {
                prefix = "../../../";
            }
            string userHome = Environment.GetEnvironmentVariable("USERPROFILE");
            string version = typeof (BridgeSetup).Assembly.GetName().Version.ToString();
            setup.AddClassPath(userHome + @"/.m2/repository/org/picocontainer/picocontainer/2.14.2/picocontainer-2.14.2.jar");
            setup.AddClassPath(userHome + @"/.m2/repository/net/sf/jni4net/jni4net.j/" + version + "/jni4net.j-" +
                               version + ".jar");
            setup.AddClassPath(prefix + "../../robocode.api/target/Test-classes");
            setup.AddClassPath(prefix + "../../robocode.api/target/classes");
            setup.AddClassPath(prefix + "../../robocode.battle/target/Test-classes");
            setup.AddClassPath(prefix + "../../robocode.battle/target/classes");
            setup.AddClassPath(prefix + "../../robocode.core/target/Test-classes");
            setup.AddClassPath(prefix + "../../robocode.core/target/classes");
            setup.AddClassPath(prefix + "../../robocode.host/target/Test-classes");
            setup.AddClassPath(prefix + "../../robocode.host/target/classes");
            setup.AddClassPath(prefix + "../../robocode.repository/target/Test-classes");
            setup.AddClassPath(prefix + "../../robocode.repository/target/classes");
            setup.AddClassPath(prefix + "../../robocode.roborumble/target/Test-classes");
            setup.AddClassPath(prefix + "../../robocode.roborumble/target/classes");
            setup.AddClassPath(prefix + "../../robocode.samples/target/Test-classes");
            setup.AddClassPath(prefix + "../../robocode.samples/target/classes");
            setup.AddClassPath(prefix + "../../robocode.sound/target/Test-classes");
            setup.AddClassPath(prefix + "../../robocode.sound/target/classes");
            setup.AddClassPath(prefix + "../../robocode.tests/target/Test-classes");
            setup.AddClassPath(prefix + "../../robocode.tests/target/classes");
            setup.AddClassPath(prefix + "../../robocode.ui/target/Test-classes");
            setup.AddClassPath(prefix + "../../robocode.ui/target/classes");
            setup.AddClassPath(prefix + "../../robocode.ui.editor/target/Test-classes");
            setup.AddClassPath(prefix + "../../robocode.ui.editor/target/classes");
            setup.AddClassPath(prefix + "robocode.dotnet.api/target/Test-classes");
            setup.AddClassPath(prefix + "robocode.dotnet.api/target/classes");
            setup.AddClassPath(prefix + "robocode.dotnet.host/target/Test-classes");
            setup.AddClassPath(prefix + "robocode.dotnet.host/target/classes");
            setup.AddClassPath(prefix + "robocode.dotnet.nhost/target/Test-classes");
            setup.AddClassPath(prefix + "robocode.dotnet.nhost/target/classes");
            setup.AddClassPath(prefix + "robocode.dotnet.samples/target/Test-classes");
            setup.AddClassPath(prefix + "robocode.dotnet.samples/target/classes");
            setup.AddClassPath(prefix + "robocode.dotnet.tests/target/Test-classes");
            setup.AddClassPath(prefix + "robocode.dotnet.tests/target/classes");
            env = Bridge.CreateJVM(setup);
            Bridge.RegisterAssembly(typeof (TestBase).Assembly);
            Bridge.RegisterAssembly(typeof (DllRootHelper).Assembly);
        }

        [TestFixtureTearDown]
        public void TearDown()
        {
            //Assert.AreEqual(JNIResult.JNI_OK, vm.DestroyJavaVM());
        }
    }
}