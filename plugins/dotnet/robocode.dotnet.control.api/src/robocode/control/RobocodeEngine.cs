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
