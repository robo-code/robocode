using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Security;
using System.Security.Permissions;
using System.Security.Policy;
using System.Text;
using System.Threading;
using net.sf.robocode.dotnet.host;
using net.sf.robocode.dotnet.utils;
using robocode;

namespace net.sf.robocode.dotnet.repository.root
{
    public class DllRootHelper
    {
        public DllRootHelper()
        {
            Thread.CurrentThread.CurrentUICulture = new CultureInfo("en-US");
            //sandBox = CreateSandbox();
            //sandBox.InitializeLifetimeService();
        }

        //private AppDomain sandBox;

        public string[] findItems(string dllPath)
        {
            string file = dllPath.Substring("file:/".Length);
            if (!File.Exists(file))
            {
                throw new ArgumentException();
            }
            Assembly assembly = Assembly.LoadFrom(file);
            List<string> items=new List<string>();
            foreach (Type type in assembly.GetTypes())
            {
                if (Reflection.CheckInterfaces(type).isValid())
                {
                    items.Add(dllPath+"!/"+type.Name);
                }
            }

            return items.ToArray();
        }

        private AppDomain CreateSandbox()
        {
            //PermissionSet pset = new PermissionSet(PermissionState.None);
            PermissionSet pset = Reflection.GetNamedPermissionSet("FullTrust");

            AppDomainSetup ads = new AppDomainSetup();
            ads.ApplicationBase = @"D:\Sf\RobocodeN\plugins\dotnet\robocode.dotnet.samples\target\";
//@"D:\Sf\RobocodeN\plugins\dotnet\robocode.dotnet.samples\target\";//TODO
            ads.ShadowCopyFiles = "true";
            Evidence hostEvidence = new Evidence();

            return AppDomain.CreateDomain(
                "Sandbox"
                ,hostEvidence,
                ads,
                pset,
                Reflection.GetStrongName(typeof(Robot).Assembly),
                Reflection.GetStrongName(typeof(RobotClassLoader).Assembly)
                );
        }

    }
}
