using System;
using System.Collections;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Security;
using System.Security.Permissions;
using System.Security.Policy;
using net.sf.robocode.dotnet.utils;
using net.sf.robocode.repository;
using robocode;
using String=java.lang.String;

namespace net.sf.robocode.dotnet.host
{
    internal class RobotLoader
    {
        private IRobotRepositoryItem item;
        private string readableDirectory;
        private string writableDirectory;

        public RobotLoader(IRobotRepositoryItem item)
        {
            readableDirectory = Path.GetFullPath(item.getReadableDirectory());
            writableDirectory = Path.GetFullPath(item.getWritableDirectory());
            this.item = item;
        }

        public Type LoadRobotMainClass()
        {
            string url = item.getFullUrl().ToString();
            string file = url.Substring("file:/".Length);
            Assembly assembly = Assembly.LoadFrom(file);
            Type type = assembly.GetType(item.getFullClassName(), true);
            return type;
        }

        public void Cleanup()
        {
            
        }

        private Assembly CreateDomain()
        {
            PermissionSet pset = new PermissionSet(PermissionState.None);
            pset.AddPermission(new FileIOPermission(FileIOPermissionAccess.Read, readableDirectory));
            pset.AddPermission(new FileIOPermission(FileIOPermissionAccess.Write, writableDirectory));
            pset.AddPermission(new FileIOPermission(FileIOPermissionAccess.Append, writableDirectory));

            AppDomainSetup ads = new AppDomainSetup();
            ads.ApplicationBase = readableDirectory;

            Evidence hostEvidence = new Evidence();
            hostEvidence.AddHost(new Zone(SecurityZone.Untrusted));
            hostEvidence.AddHost(new Url(item.getReadableDirectory()));

            AppDomain sandbox = AppDomain.CreateDomain(
                "Sandbox",
                hostEvidence,
                ads,
                pset,
                Reflection.GetStrongName(typeof(Robot).Assembly),
                Reflection.GetStrongName(typeof(RobotLoader).Assembly)
                );

            string assemblyFile = Path.GetFullPath(item.getFullUrl().toURI().ToString().Substring("file:/".Length));
            return sandbox.Load(assemblyFile);
        }


    }
}
