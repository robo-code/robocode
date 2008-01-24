using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Text;
using java.lang;
using nrobocodeui.repository;
using robocode.manager;
using robocode.peer.robot;
using robocode.repository;
using robocode.security;

namespace nrobocodeui.security
{
    public class NetRobocodeClassLoader : ClassLoader, IRobocodeClassLoader
    {
        private RobocodeManager robocodeManager;
        private RobotClassManager robotClassManager;
        
        public void init(RobotClassManager rcm)
        {
            robotClassManager = rcm;
        }

        public bool couldLoad(IRobotSpecification irs)
        {
            return (irs is NetAssemblySpecification);
        }

        public Class loadRobotClass(string str, bool b)
        {
            IRobotSpecification specification = robotClassManager.getRobotSpecification();

            //TODO ZAMO security
            Assembly assembly = Assembly.LoadFile(specification.getFilePath());
            Type type = assembly.GetType(specification.getFullClassName());
            Class clazz2 = ikvm.runtime.Util.getClassFromTypeHandle(type.TypeHandle);

            robotClassManager.setUid(type.AssemblyQualifiedName);

            return clazz2;
        }

        public new Class loadClass(string name, bool resolve)
        {
            return base.loadClass(name, resolve);
        }

        public void cleanup()
        {
            throw new NotImplementedException();
        }

        public string getRootPackageDirectory()
        {
            throw new NotImplementedException();
        }

        public string getClassDirectory()
        {
            return Path.GetDirectoryName(robotClassManager.getRobotSpecification().getFilePath());
        }

        public string getRootDirectory()
        {
            throw new NotImplementedException();
        }

        public void setRobocodeManager(RobocodeManager rm)
        {
            robocodeManager = rm;
        }
    }
}
