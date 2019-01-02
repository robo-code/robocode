/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
using Robocode;
using robocode.control;

namespace net.sf.robocode.dotnet.host.seed
{
    public class AppDomainShell : IDisposable
    {
        private static readonly Assembly robocodeAssembly = typeof (Robot).Assembly;
        private static readonly Assembly controlAssembly = typeof (RobocodeEngine).Assembly;
        private static readonly Assembly hostAssembly = typeof(AppDomainShell).Assembly;
        private static readonly Assembly jniAssembly = typeof(Bridge).Assembly;
        protected AppDomain domain;
        private string robotAssemblyFileName;
        protected IRobotPeer robotPeer;
        private string robotShadow;
        private string tempDir;

        #region IDisposable Members

        public void Dispose()
        {
            if (domain != null)
            {
                try
                {
                    AppDomain.Unload(domain);
                    domain = null;
                    GC.Collect();
                }
                catch (Exception ex)
                {
                    LoggerN.logError(ex);
                    if (robotPeer != null)
                    {
                        robotPeer.punishBadBehavior(BadBehavior.UNSTOPPABLE);
                    }
                }
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
				catch (UnauthorizedAccessException)
                {
                    //ignore
                }
                tempDir = null;
            }
        }

        #endregion

        public void Init(bool fullBind)
        {
            string name = Path.GetRandomFileName();
            tempDir = Path.Combine(Path.GetTempPath(), name);
            string robocodeShadow = Path.Combine(tempDir, Path.GetFileName(robocodeAssembly.Location));
            string hostShadow = Path.Combine(tempDir, Path.GetFileName(hostAssembly.Location));
            string controlShadow = Path.Combine(tempDir, Path.GetFileName(controlAssembly.Location));
            string jniShadow = Path.Combine(tempDir, Path.GetFileName(jniAssembly.Location));

            Directory.CreateDirectory(tempDir);
            File.Copy(robocodeAssembly.Location, robocodeShadow);
            File.Copy(controlAssembly.Location, controlShadow);
            File.Copy(hostAssembly.Location, hostShadow);
            File.Copy(jniAssembly.Location, jniShadow);

            var trustAssemblies = new[]
                                      {
                                          Reflection.GetStrongName(robocodeAssembly),
                                          Reflection.GetStrongName(controlAssembly),
                                          Reflection.GetStrongName(hostAssembly),
                                          Reflection.GetStrongName(jniAssembly),
                                      };
            var domainSetup = new AppDomainSetup();
            Evidence securityInfo = AppDomain.CurrentDomain.Evidence;
            var permissionSet = new PermissionSet(PermissionState.None);
            permissionSet.AddPermission(
                new SecurityPermission(SecurityPermissionFlag.Execution | SecurityPermissionFlag.Assertion));
            permissionSet.AddPermission(new FileIOPermission(FileIOPermissionAccess.Read, tempDir));
            permissionSet.AddPermission(new UIPermission(PermissionState.None));
            //permissionSet.AddPermission(HostProtection);

            domainSetup.ApplicationBase = tempDir;

            domainSetup.ApplicationName = name;
            //domainSetup.SandboxInterop = true;
            domainSetup.DisallowBindingRedirects = true;
            domainSetup.DisallowCodeDownload = true;
            domainSetup.DisallowPublisherPolicy = true;
            domainSetup.AppDomainInitializer = AppDomainSeed.Load;
            

            domain = AppDomain.CreateDomain(name, securityInfo, domainSetup, permissionSet, trustAssemblies);
            domain.SetData("fullBind", fullBind);
            domain.SetData("JavaHome", Bridge.Setup.JavaHome);
            domain.DoCallBack(AppDomainSeed.Bind);
        }

        public void Open(string dllName)
        {
            robotAssemblyFileName = Path.GetFullPath(dllName);
            robotShadow = Path.Combine(tempDir, Path.GetFileName(robotAssemblyFileName));
            if (!File.Exists(robotShadow))
            {
                File.Copy(robotAssemblyFileName, robotShadow);
            }
            domain.SetData("robotAssemblyFileName", robotAssemblyFileName);
            domain.SetData("robotAssemblyShadowFileName", robotShadow);
            domain.DoCallBack(AppDomainSeed.Open);
        }


        public string[] FindRobots()
        {
            domain.DoCallBack(AppDomainSeed.FindRobots);
            object data = domain.GetData("robotsFound");
            string[] robotsFound = data == null ? new string[] {} : ((string) data).Split(';');
            return robotsFound;
        }

        public RobotType GetRobotType(string typeFullName)
        {
            domain.SetData("loadRobot", typeFullName);
            domain.DoCallBack(AppDomainSeed.GetRobotType);
            object data = domain.GetData("robotLoaded");
            if (data==null)
            {
                return RobotType.Invalid;
            }
            var type = (int) data;
            if (type == 0)
            {
                return RobotType.Invalid;
            }
            return new RobotType(type);
        }
    }
}