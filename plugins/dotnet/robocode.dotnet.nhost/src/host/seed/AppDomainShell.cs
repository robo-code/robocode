using System;
using System.IO;
using System.Reflection;
using System.Security;
using System.Security.Permissions;
using net.sf.jni4net;
using net.sf.robocode.dotnet.utils;
using net.sf.robocode.repository;
using robocode;

namespace net.sf.robocode.dotnet.host.seed
{
    public class AppDomainShell : IDisposable
    {
        private static readonly Assembly robocodeAssembly = typeof(Robot).Assembly;
        private static readonly Assembly hostAssembly = typeof(AppDomainShell).Assembly;
        private static readonly Assembly jniAssembly = typeof(Bridge).Assembly;
        private AppDomain domain;
        private readonly string tempDir;
        private readonly string name;
        private readonly string robotAssemblyFileName;
        private readonly string robotAssemblyShadowFileName;

        public AppDomainShell(string dllName)
        {
            robotAssemblyFileName = Path.GetFullPath(dllName);
            name = Path.GetFileNameWithoutExtension(robotAssemblyFileName);
            tempDir = Path.Combine(Path.GetTempPath(), Path.GetRandomFileName());
            robotAssemblyShadowFileName = Path.Combine(tempDir, Path.GetFileName(robotAssemblyFileName));

            Directory.CreateDirectory(tempDir);
            File.Copy(robotAssemblyFileName, robotAssemblyShadowFileName);
            File.Copy(robocodeAssembly.Location, Path.Combine(tempDir, Path.GetFileName(robocodeAssembly.Location)));
            File.Copy(hostAssembly.Location, Path.Combine(tempDir, Path.GetFileName(hostAssembly.Location)));
            File.Copy(jniAssembly.Location, Path.Combine(tempDir, Path.GetFileName(jniAssembly.Location)));

            var trustAssemblies = new[] { Reflection.GetStrongName(robocodeAssembly), Reflection.GetStrongName(hostAssembly), Reflection.GetStrongName(jniAssembly) };
            var domainSetup = new AppDomainSetup();
            var securityInfo = AppDomain.CurrentDomain.Evidence;
            //PermissionSet permissionSet = new PermissionSet(PermissionState.None);
            //permissionSet.AddPermission(new SecurityPermission(SecurityPermissionFlag.Execution));
            PermissionSet permissionSet = new PermissionSet(PermissionState.Unrestricted);
            domainSetup.ApplicationBase = tempDir;

            domainSetup.ApplicationName = name;
            domainSetup.SandboxInterop = true;
            domainSetup.DisallowBindingRedirects = true;
            domainSetup.DisallowCodeDownload = true;
            domainSetup.DisallowPublisherPolicy = true;
            //domainSetup.DisallowApplicationBaseProbing = true;
            //domainSetup.PrivateBinPathProbe = "true";
            //domainSetup.PrivateBinPath = tempDir;

            domainSetup.AppDomainInitializer = AppDomainSeed.Load;
            domainSetup.AppDomainInitializerArguments = new[] { robotAssemblyFileName, robotAssemblyShadowFileName };

            domain = AppDomain.CreateDomain(name, securityInfo, domainSetup, permissionSet, trustAssemblies);


        }

        public string[] FindRobots()
        {
            domain.DoCallBack(AppDomainSeed.FindRobots);
            object data = domain.GetData("robotsFound");
            string[] robotsFound = data == null ? new string[] { } : ((string)data).Split(';');
            return robotsFound;
        }

        public RobotType GetRobotType(string typeFullName)
        {
            domain.SetData("loadRobot", typeFullName);
            domain.DoCallBack(AppDomainSeed.GetRobotType);
            RobotType type = (RobotType)domain.GetData("robotLoaded");
            if (type==null)
            {
                return RobotType.Invalid;
            }
            return type;
        }


        public void Dispose()
        {
            if (domain != null)
            {
                AppDomain.Unload(domain);
            }
            if (tempDir != null && Directory.Exists(tempDir))
            {
                try
                {
                    Directory.Delete(tempDir, true);
                }
                catch (IOException)
                {
                    //ignore
                }
            }
        }
    }
}
