using System;
using System.IO;
using System.Reflection;
using System.Security;
using System.Security.Permissions;
using System.Security.Policy;
using net.sf.jni4net;
using net.sf.robocode.dotnet.utils;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using robocode;

namespace net.sf.robocode.dotnet.host.seed
{
    public class AppDomainShell:IDisposable
    {
        private static readonly Assembly robocodeAssembly = typeof(Robot).Assembly;
        private static readonly Assembly hostAssembly = typeof(AppDomainShell).Assembly;
        private static readonly Assembly jniAssembly = typeof(Bridge).Assembly;
        protected AppDomain domain;
        protected IRobotPeer robotPeer;
        private string tempDir;
        private string name;
        private string robotAssemblyFileName;
        private string robotShadow;

        public AppDomainShell(string dllName)
        {
            Init(dllName);
        }

        protected AppDomainShell()
        {
        }

        protected void Init(string dllName)
        {
            robotAssemblyFileName = Path.GetFullPath(dllName);
            name = Path.GetFileNameWithoutExtension(robotAssemblyFileName);
            tempDir = Path.Combine(Path.GetTempPath(), Path.GetRandomFileName());
            robotShadow = Path.Combine(tempDir, Path.GetFileName(robotAssemblyFileName));
            string robocodeShadow = Path.Combine(tempDir, Path.GetFileName(robocodeAssembly.Location));
            string hostShadow = Path.Combine(tempDir, Path.GetFileName(hostAssembly.Location));
            string jniShadow = Path.Combine(tempDir, Path.GetFileName(jniAssembly.Location));

            Directory.CreateDirectory(tempDir);
            File.Copy(robotAssemblyFileName, robotShadow);
            File.Copy(robocodeAssembly.Location, robocodeShadow);
            File.Copy(hostAssembly.Location, hostShadow);
            File.Copy(jniAssembly.Location, jniShadow);

            StrongName[] trustAssemblies = new []
                                               {
                                                   Reflection.GetStrongName(robocodeAssembly), 
                                                   Reflection.GetStrongName(hostAssembly), 
                                                   Reflection.GetStrongName(jniAssembly),
                                               };
            AppDomainSetup domainSetup = new AppDomainSetup();
            Evidence securityInfo = AppDomain.CurrentDomain.Evidence;
            PermissionSet permissionSet = new PermissionSet(PermissionState.None);
            permissionSet.AddPermission(new SecurityPermission(SecurityPermissionFlag.Execution|SecurityPermissionFlag.Assertion));
            permissionSet.AddPermission(new FileIOPermission(FileIOPermissionAccess.Read, tempDir));
            
            domainSetup.ApplicationBase = tempDir;

            domainSetup.ApplicationName = name;
            //domainSetup.SandboxInterop = true;
            domainSetup.DisallowBindingRedirects = true;
            domainSetup.DisallowCodeDownload = true;
            domainSetup.DisallowPublisherPolicy = true;
            domainSetup.AppDomainInitializer = AppDomainSeed.Load;
            domainSetup.AppDomainInitializerArguments = new[] { robotAssemblyFileName, robotShadow };

            domain = AppDomain.CreateDomain(name, securityInfo, domainSetup, permissionSet, trustAssemblies);
            domain.DoCallBack(AppDomainSeed.Bind);
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
            var type = (int) domain.GetData("robotLoaded");
            if (type == 0)
            {
                return RobotType.Invalid;
            }
            return new RobotType(type);
        }


        public void Dispose()
        {
            if (domain != null)
            {
                try
                {
                    AppDomain.Unload(domain);
                }
                catch (Exception ex)
                {
                    LoggerN.logError(ex);
                    if (robotPeer != null)
                    {
                        robotPeer.punishBadBehavior(BadBehavior.UNSTOPPABLE);
                    }
                }
                domain = null;
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
                tempDir = null;
            }
        }
    }
}
